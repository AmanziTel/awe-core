#!/usr/bin/env ruby

require 'rubygems'
require 'parseexcel'

module Amanzi
  module Reader
    class NetworkReader
      class BSC
              attr_reader :name
              def initialize(name)
                      @name=name
              end
              def to_s ; name ; end
      end
      class Site
              attr_reader :bsc, :name, :lat, :long, :lac
              def initialize(bsc,name,lat,long,lac)
                      @bsc,@name,@lat,@long,@lac=bsc,name,lat,long,lac
              end
              def to_s ; "#{name} at (#{long}:#{lat} - #{lac})" ; end
      end
      class Sector
              attr_reader :site, :name, :antenna_type, :height, :azimuth, :beamwidth, :mtilt, :etilt, :type, :status, :channel, :pn_code, :pilot_power
              def initialize(site,name,antenna_type,height,azimuth,beamwidth,mtilt,etilt,type,status,channel,pn_code,pilot_power)
                      @site,@name,@antenna_type,@height,@azimuth,@beamwidth,@mtilt,@etilt,@type,@status,@channel,@pn_code,@pilot_power=
                       site, name, antenna_type, height, azimuth, beamwidth, mtilt, etilt, type, status, channel, pn_code, pilot_power
              end
              def to_s ; "#{name} antenna[#{antenna_type}] height[#{height}] azimuth[#{azimuth}] bw[#{beamwidth}] tilt[#{mtilt}:#{etilt}] type[#{type}] status[#{status}] channel[#{channel}] pn_code[#{pn_code}] pwr[#{pilot_power}]" ; end
      end
      CELL_KEY_MAP = {
              :antenna_beamwidth => :beamwidth,
              :antenna_m_tilt => :mtilt,
              :antenna_e_tilt => :etilt,
              :antenna_height => :height,
              :cell_type => :type}
      attr_reader :bscs, :sites, :sectors
      def initialize(worksheet)
              @headings = []
              @headings_map = {}
              worksheet.row(0).each do |cell|
                      @headings_map[head_cell_to_key(cell)] = @headings.length
                      @headings << cell.to_s('UTF-8')
              end
              @worksheet = worksheet
#              @headings_map.each do |key,i|
#                      puts "Heading: #{key} => #{i} \t(from #{@headings[i]})"
#              end
      end
      def head_cell_to_key(cell)
              map_cell_keys(cell.to_s('UTF-8').chomp.gsub(/[\s\-]+/,'_').gsub(/\_+$/,'').downcase.intern)
      end
      def map_cell_keys(key)
              CELL_KEY_MAP[key] || key
      end
      def read
              @worksheet.each(1) do |row|
                      str = lambda{|key| row[i_of(key)].to_s('UTF-8')}
                      flt = lambda{|key| row[i_of(key)].to_f}
                      int = lambda{|key| row[i_of(key)].to_i}
                      bsc = get_bsc(str.call(:bsc))
                      site = get_site(bsc, str.call(:name), flt.call(:lat), flt.call(:long), int.call(:lac))
                      sector = get_sector(site, str.call(:cell), str.call(:antenna_type), flt.call(:height), flt.call(:azimuth), flt.call(:beamwidth), int.call(:mtilt), int.call(:etilt), str.call(:type), str.call(:status), int.call(:channel), int.call(:pn_code), int.call(:pilot_power))
              end
      end
      def i_of(key)
              @headings_map[key] || puts("Unknown header key: #{key}")
      end
      def get_bsc(name)
              @bscs||={}
              @bscs[name]||=BSC.new(name)
      end
      def get_site(bsc,name,*args)
              @sites||={}
              @sites[name]||=Site.new(bsc,name,*args)
      end
      def get_sector(site,name,*args)
              @sectors||={}
              @sectors[name]||=Sector.new(site,name,*args)
      end
    end # NetworkReader
  end # Reader
end # Amanzi

# If this file is run as a script, then process the command-line
# otherwise we assume the code that required this file will call
# these lines itself.
if __FILE__ == $0
  ARGV[0] || puts("No excel filename given") || exit(-1)

  workbook = Spreadsheet::ParseExcel.parse(ARGV[0])
  worksheet = workbook.worksheet(0)

  reader = Amanzi::Reader::NetworkReader.new(worksheet)
  reader.read
  reader.bscs.each{|name,bsc| puts "BSC: #{bsc}"}
  reader.sites.each{|name,site| puts "SITE: #{site}"}
  reader.sectors.each{|name,sector| puts "SECTOR: #{sector}"}
end
