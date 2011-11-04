#!/usr/bin/env ruby
#
# This code parses data in Ericsson TEMS drive test file format.
# The data is provided as a stream to downstream managers, if provided
# or simply output to stdout.
#
module Amanzi
  module Reader
    class TEMSReader
      def initialize(file,line,options={})
        @file = file
        @basename = File.basename(@file)
        @tmpfile = options[:tmpfile] || "/tmp/amanzi_tems_reader_#{$$}_#{File.basename(file).gsub('.','_').downcase}"
        @use_database = options[:use_database]
        @save_files = !@use_database || options[:save_files]
        @verbose = options[:verbose]
        @limit = options[:limit].to_i
        @headings = []
        @headings_map = {}
        line.chomp.split(/\t/).each do |cell|
          @headings_map[head_cell_to_key(cell)] = @headings.length
          @headings << cell.to_s
        end
        #@headings_map.each do |key,i|
        #  puts "Heading: #{key} => #{i} \t(from #{@headings[i]})"
        #end
        @first_line = nil
        @last_line = nil
        @previous_ms = nil
        @previous_time = nil
        @previous_pn_code = nil
        @previous_latlong = nil
        @started = Time.now
        @stats = {}
        @signals = {}
        @line_number = nil  # allows logging to know if we are parsing or not
        @count_valid_message = 0
        @count_valid_location = 0
        @count_valid_changed = 0
        # Get the next available index from the database
        @file_id = 0
        @m_id = 1
        @mp_id = 1
        if @use_database
          @file_id = determine_file_id_from_database
          @m_id = Measurement.maximum(:id).to_i + 1
          @mp_id = MeasurementPoint.maximum(:id).to_i + 1
        end
        @first_m_id = @m_id
        @first_mp_id = @mp_id
      end
      def created_at
        @created_at ||= Time.now.strftime('%Y-%m-%d %H:%M:%S')
      end
      def started_on
        unless @started_on
          if @file =~ /^(\d{2})(\d{2})\_/
            @started_on = "#{Time.now.strftime('%Y')}-#{$2}-#{$1}"
          else
            @started_on = Time.now.strftime('%Y-%m-%d')
          end
        end
        @started_on
      end
      def tmpfile(suffix='table')
        "#{@tmpfile}_#{suffix}.txt"
      end
      # This method is an important one differentiating the different behaviours
      # of this script when called from the web application or from the command-line.
      # The web application is expected to create an UploadedFile and possibly
      # a MeasurementFile referencing it. Here we wil find those and return the
      # if of the MeasurementFile for use in the tables created. If the MeasurementFile
      # does not exist, it is created. However, it is not simple to create an
      # UploadedFile without an actual web upload, so if this script is called
      # on a file that has not really been uploaded, it will create the MeasurementFile
      # with 0 for the uploaded_file_id. This is only suitable for testing as it
      # leaves the database in a state that may not suite the web GUI.
      def determine_file_id_from_database
        return 0 unless @use_database
        if (@file =~ /uploaded_files\/(\d+)\/(\d+)\/(.*)$/) &&
           (u_file = UploadedFile.find([$1,$2].join.to_i)) &&
           (u_file.filename == $3)
          notify "Found previous uploaded file: #{u_file.inspect}"
          if (@mu_file = MeasurementFile.find_by_uploaded_file_id(u_file.id))
            notify "Found previous measurement file: #{@mu_file.inspect}"
            #notify "Deleting #{@mu_file.measurements.count} previous measurements for this upload: #{@mu_file.inspect}"
            #@mu_file.measurements.destroy_all
            #TODO: The above deletion takes too long. Either never call this twice,
            # or provide a measurement_file_id to measurement_points and use delete_all
            # on both measurements and measurement_points
            @mu_file.id
          else
            @mu_file = MeasurementFile.create(:uploaded_file_id => u_file.id, :started_on => started_on)
            notify "Created new measurement file: #{@mu_file.inspect}"
            @mu_file.id
          end
        else
          notify "Found no previous uploaded file: #{@file}"
          u_file = StringIO.open(File.open(@file){|f| f.read}) do |s|
            def s.size=(value) ; @file_size=value ; end
            def s.size ; @file_size ; end
            def s.original_filename=(value) ; @original_filename=value ; end
            def s.original_filename ; @original_filename ; end
            def s.content_type ; "text/plain" ; end
            s.size = File.size(@file)
            s.original_filename = File.basename(@file)
            UploadedFile.create(:uploaded_data => s)
          end
          notify "Created new uploaded file: #{u_file.inspect}"
          @mu_file = MeasurementFile.create(:uploaded_file_id => u_file.id, :started_on => started_on)
          notify "Created new measurement file: #{@mu_file.inspect}"
          @mu_file.id
        end
      end
      def head_cell_to_key(cell)
        cell.to_s.chomp.gsub(/[\s\-\[\]\(\)\/\.]+/,'_').gsub(/\_+$/,'').downcase.intern
      end
      def status
        @line_number ? "line:#{@line_number}" : "#{Time.now - @started}s"
      end
      def error(message)
        STDERR.puts "TEMS:#{@basename}:#{status}: #{message}"
      end
      def notify(message)
        STDOUT.puts "TEMS:#{@basename}:#{status}: #{message}"
      end
      def info(message)
        STDOUT.puts "TEMS:#{@basename}:#{status}: #{message}" if @verbose
      end
      def debug(message)
        STDOUT.puts "TEMS:#{@basename}:#{status}: #{message}" if @verbose || @debug
      end
      def read(line)
        @line_number ||= 1  # we read the header line before, so we start here at line 2
        @line_number += 1
        cells=line.split(/\t/)
        @time = cells[i_of(:time)]
        ms = cells[i_of(:ms)]
        event = cells[i_of(:event)]    # currently only getting this as a change marker
        message_type = cells[i_of(:message_type)]    # need this to filter for only relevant messages
        #message_id = cells[i_of(:message_id)]    # parsing this is not faster
        return unless message_type == 'EV-DO Pilot Sets Ver2'
        @count_valid_message += 1
        #return unless message_id == '27019'    # not faster
        #return unless message_id.to_i == 27019    # not faster
        
        # TODO: Ignore lines with Event=~/Idle/ since these generally contain invalid All-RX-Power
        # TODO: Also be careful of any All-RX-Power of -63 (since it is most often invalid data)
        # TODO: If number of PN codes does not match number of EC-IO make sure to align correct values to PNs

        latitude = cells[i_of(:all_latitude)]
        longitude = cells[i_of(:all_longitude)]
        @latlong = "#{latitude}\t#{longitude}"
        if @latlong != @previous_latlong
          save_data
          @previous_latlong = @latlong
        end
        return if latitude.to_s.length==0 || longitude.to_s.length==0
        @count_valid_location += 1

        channel = cells[i_of(:all_active_set_channel_1)]
        pn_code = cells[i_of(:all_active_set_pn_1)]
        ec_io = cells[i_of(:all_active_set_ec_io_1)]
        measurement_count = cells[i_of(:all_pilot_set_count)].to_i
        if measurement_count > 12
            error "Measurement count #{measurement_count} > 12"
            measurement_count = 12
        end
        changed = false
        if ms != @previous_ms
            changed = true
            @previous_ms = ms
        end
        if @time != @previous_time
            changed = true
            @previous_time = @time
        end
        if pn_code != @previous_pn_code
            error "SERVER CHANGED" if @previous_pn_code
            changed = true
            @previous_pn_code = pn_code
        end
        if measurement_count > 0 && (changed || event.length>0)
          return if @limit>0 && @count_valid_changed > @limit
          @first_line ||= @line_number
          @last_line = @line_number
          @count_valid_changed += 1
          debug "#{@time}: server channel[#{channel}] pn[#{pn_code}] Ec/Io[#{ec_io}]\t#{event}\t#{@latlong}"
          (1..measurement_count).each do |i|
            # Delete invalid data, as you can have empty ec_io
            # zero ec_io is correct, but empty ec_io is not
            ec_io = cells[i_of(:"all_pilot_set_ec_io_#{i}")]
            if ec_io.nil? || ec_io.length<1
              error "UNEXPECTED EMPTY EC-IO for measurment #{i}"
              next
            end
            ec_io = ec_io.to_i
            channel = cells[i_of(:"all_pilot_set_channel_#{i}")].to_i
            pn_code = cells[i_of(:"all_pilot_set_pn_#{i}")].to_i
            debug "\tchannel[#{channel}] pn[#{pn_code}] Ec/Io[#{ec_io}]"
            @stats[pn_code]||=[0,0]
            @stats[pn_code][0]+=1
            @stats[pn_code][1]+=ec_io
            chan_code = "#{channel}\t#{pn_code}"
            @signals[chan_code] ||= [0.0,0]
            @signals[chan_code][0] += dbm2mw(ec_io)
            @signals[chan_code][1] += 1
          end
        end
      end
      # Save the currently cached data, including the latest measurement
      # and all signals associated with it, clearing the signal cache.
      def save_data
        debug "Saving data at #{@latlong}"
        if @signals.length > 0
          debug "Saving measurement: #{@m_id}\t#{@latlong}\t#{@file_id}\t#{@time}"
          @m_file ||= File.open(tmpfile('m'),'w')
          @m_file.puts "#{@m_id}\t#{@latlong}\t#{@file_id}\t#{@time}\t#{@first_line.to_i}\t#{@last_line.to_i}"
          @signals.each do |chan_code,signal|
            mw = signal[0]/signal[1]
            mp_line = "#{@mp_id}\t#{@m_id}\t#{chan_code}\t#{mw2dbm(mw)}\t#{mw}"
            debug "Saving measurement_point: #{mp_line}"
            @mp_file ||= File.open(tmpfile('mp'),'w')
            @mp_file.puts mp_line
            @mp_id += 1
          end
          @m_id += 1          
        end
        @signals.clear
        @first_line = nil
        @last_line = nil
      end
      def find_signals(sig_counts)
        total_mw = 0
        count_mw = 0
        sig_counts.each do |sig,count|
          total_mw += dbm2mw(sig) * count
          count_mw += count
        end
        total_mw /= count_mw
        [mw2dbm(total_mw),total_mw]
      end
      def dbm2mw(dbm)
        10.0**(dbm.to_f/10.0)
      end
      def mw2dbm(mw)
        10.0*Math.log10(mw)
      end
      def i_of(key)
        @headings_map[key] || error("Unknown header key: #{key}")
      end
      # Load the temporary data into the database.
      # This can be done by sourcing the sql file that is output here, or
      # by passing the -use-database switch in which case a connection will
      # be found and sql commands injected directly into the database.
      # Note that we use the high performance 'load data infile...' syntax
      # which does not support functions in the data file, so we cannot have
      # the spatial types, instead we must have columns for the lat/long
      # and load those, and then afterwards calculation the spatial columns
      # using mysql commands like 'update measurements set geom = PointFromText(...'
      # See http://dev.mysql.com/doc/refman/5.0/en/populating-spatial-columns.html
      # for more information and discussions on this option
      def load
        close_tmpfiles
        if @use_database
          notify "Loading data into database"
          time_m = load_tmpfile('m',Measurement.connection,'measurements','id,latitude,longitude,measurement_file_id,measured_at,first_line,last_line',@first_m_id,@m_id) do |connection|
            #execute_sql connection, "Update measurements set geom = PointFromText(Concat('Point(',measurements.longitude,' ',measurements.latitude,')')) where id >= #{@first_m_id} and id < #{@m_id} or geom is null"
            execute_sql connection, "Update measurements set geom = PointFromWKB(Point(measurements.longitude,measurements.latitude)) where id >= #{@first_m_id} and id < #{@m_id} or geom is null"
            execute_sql connection, "Update measurements set rindex = 1000000*rand(0) where id >= #{@first_m_id} and id < #{@m_id} or rindex is null"
          end
          time_mp = load_tmpfile('mp',MeasurementPoint.connection,'measurement_points','id,measurement_id,channel,code,signal_dbm,signal_mw',@first_mp_id,@mp_id)
          notify "Loaded data into database in #{time_m+time_mp} seconds"
          notify "\t#{time_m} seconds for measurements"
          notify "\t#{time_mp} seconds for measurement points"
        end
        if @save_files
          # Do not delete the files, but rather create a command file for manual use later on (mostly for testing/debugging)
          File.open(tmpfile('sql'),'w') do |file|
            (@sql_commands_cache||[]).each{|line|file.puts "#{line};"}
          end
        else
          # We've used the files, so now delete them
          delete_tmpfiles
        end
      end
      def load_tmpfile(suffix,connection,table,fields,first_id,max_id)
        start_load = Time.now
        if @use_database && File.exist?(tmpfile(suffix))
          #execute_sql "lock tables #{table}" # generates error (perhaps permissions issue?)
          #execute_sql connection, "alter table #{table} disable keys"
          execute_sql connection, "load data infile '#{tmpfile(suffix)}' replace into table #{table} (#{fields})"
          execute_sql connection, "update #{table} set created_at='#{created_at}', updated_at='#{created_at}' where id >= #{first_id} and id < #{max_id}"
          yield connection if(block_given?)
          #execute_sql connection, "alter table #{table} enable keys"
          #execute_sql connection, "unlock tables"
        end
        Time.now - start_load
      end
      def execute_sql(connection,cmd)
        @sql_commands_cache ||= []
        @sql_commands_cache << cmd
        notify cmd
        connection.execute(cmd)
      end
      def close_tmpfiles
        if @m_file
          notify "Wrote #{@m_id} measurements to #{@m_file.path}"
          @m_file.close
          @m_file = nil
        end
        if @mp_file
          notify "Wrote #{@mp_id} measurements to #{@mp_file.path}"
          @mp_file.close
          @mp_file = nil
        end
      end
      def delete_tmpfiles
        close_tmpfiles
        [tmpfile('m'),tmpfile('mp')].each{|f| File.delete(f) if File.exist?(f)}
      end
      def print_stats
        taken = Time.now-@started
        TEMSReader.add_times(taken)
        notify "Finished loading data in #{taken} seconds"
        notify "Read #{@line_number-1} data lines and then filtered down to:"
        notify "\t#{@count_valid_message} with valid messages"
        notify "\t#{@count_valid_location} with known locations"
        notify "\t#{@count_valid_changed} with changed data"
        notify "Read #{@stats.keys.length} unique PN codes:"
        @stats.keys.sort.each do |pn_code|
          notify "\t#{pn_code} measured #{@stats[pn_code][0]} times (average Ec/Io = #{@stats[pn_code][1]/@stats[pn_code][0]})"
        end
        close_tmpfiles
        if @mu_file
          @mu_file.parse_stats = @stats
          @mu_file.save
          notify "Saved parse stats to the measurement file: #{@mu_file.inspect}"
        end
        @line_number = nil # finished parsing
      end
      def self.add_times(taken)
        @times||=[0,0]
        @times[0]+=1
        @times[1]+=taken
      end
      def self.time_stats
        STDERR.puts "Finished #{@times[0]} loads in #{@times[1].to_f/60} minutes (average #{@times[1]/@times[0]} seconds per load)"
      end
    end # TEMSReader
  end # Reader
end # Amanzi

# If this file is run as a script, then process the command-line
# otherwise we assume the code that required this file will call
# these lines itself.
if __FILE__ == $0
  options = {}
  files = ARGV.inject([]) do |f,a|
    if a =~ /^(\-+)([\w\-\_]+)/
      option = $2
      if option =~ /use.database/i
        options[:use_database] = true
        STDERR.puts "Running in database mode, will use ActiveRecord to access database"
      elsif option =~ /save.files/
        options[:save_files] = true
      elsif option =~ /verbose/
        options[:verbose] = true
      elsif option =~ /limit[\=\+\-]?(\d+)/
        options[:limit] = $1.to_i
      end
    elsif File.exist?(a)
      f << a
    else
      STDERR.puts "No such file: #{a}"
    end
    f
  end.compact
  files[0] || puts("No TEMS FMT filename given") || exit(-1)
  if options[:use_database]
    require File.join(File.dirname(__FILE__), '..', '..', '..', 'config', 'environment') 
    Measurement.connection.execute "alter table measurements disable keys"
    Measurement.connection.execute "alter table measurement_points disable keys"
  end
  STDERR.puts "Reading #{files.length} files ..."
  for file in files
    reader = nil
    STDERR.puts "Reading file: #{file}"
    File.open(file).each do |line|
        if reader
            reader.read(line)
        else
            reader = Amanzi::Reader::TEMSReader.new(file,line,options)
        end
    end
    reader.save_data
    reader.print_stats
    reader.load if options[:use_database]
  end
  if options[:use_database]
    start_enabling_keys = Time.now
    STDOUT.puts "TEMS: Re-enabling all database keys"
    Measurement.connection.execute "alter table measurement_points enable keys"
    Measurement.connection.execute "alter table measurements enable keys"
    STDOUT.puts "TEMS: Re-enabled database keys in #{Time.now - start_enabling_keys} seconds"
  end
  Amanzi::Reader::TEMSReader.time_stats
end
