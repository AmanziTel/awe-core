require 'java'
require 'date'
require 'neo4j'

#require 'neo4j/auto_tx'
#require 'ruby/cell'
require 'ruby/amanzi_neo4j'
require 'ruby/gis'
require 'ruby/filters'
require "ruby/style"
require 'ruby/search_utils'
require "initKpi"


include_class org.amanzi.neo.core.database.nodes.CellID
include_class org.amanzi.neo.core.service.NeoServiceProvider

include_class org.amanzi.awe.report.model.Report
include_class org.amanzi.awe.report.model.Chart
include_class org.amanzi.awe.report.model.ReportText
include_class org.amanzi.awe.report.model.ReportImage
include_class org.amanzi.awe.report.model.ReportTable
include_class org.amanzi.awe.report.model.ReportMap
include_class org.amanzi.awe.report.charts.ChartType
include_class org.amanzi.awe.report.charts.EventDataset
include_class org.amanzi.awe.report.charts.Charts
include_class org.amanzi.awe.report.charts.CustomBarRenderer
include_class org.amanzi.awe.report.util.ReportUtils
include_class org.amanzi.awe.report.pdf.PDFPrintingEngine

include_class "java.text.SimpleDateFormat"
include_class org.jfree.data.category.DefaultCategoryDataset;
include_class org.jfree.data.xy.XYSeries
include_class org.jfree.data.xy.XYSeriesCollection
include_class org.jfree.data.xy.XYBarDataset
include_class org.jfree.chart.axis.DateAxis
include_class org.jfree.chart.axis.TickUnits
include_class org.jfree.chart.axis.DateTickUnit
include_class org.jfree.chart.plot.PlotOrientation
include_class org.jfree.chart.plot.Plot
include_class org.jfree.data.time.Millisecond
include_class org.jfree.data.time.Minute
include_class org.jfree.data.time.Day
include_class org.jfree.data.time.Hour
include_class org.jfree.data.time.Week
include_class org.jfree.data.time.Month
include_class org.jfree.data.time.TimeSeries
include_class org.jfree.data.time.TimeSeriesCollection

puts "Starting service...."
neo_service = NeoServiceProvider.getProvider.getService
database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation
Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)
puts "Service started"

module NodeUtils
  def children_of(parent_id)
    begin
      Neo4j::Transaction.run {
        Neo4j.load(parent_id).relationships.outgoing(:CHILD).nodes
      }
    rescue Exception =>exc
      puts "children_of: an exception occured #{exc}"
      nil
    end
  end

  def find_dataset(dataset_name)
    puts "Neo4j.reference_node #{Neo4j.ref_node}"
    traverser=Neo4j.ref_node.outgoing(:CHILD).depth(2).filter      do
      puts "find_dataset #{get_property(:name.to_s)} #{neo_id}"
      get_property(:name.to_s)== dataset_name
    end
    traverser.first
  end

  def find_aggr_node(gis_node,property, distribute, select)
    puts "gis node #{gis_node}"
    if gis_node.rel? :NEXT
      dataset_node=gis_node.rel(:NEXT).end_node
    else
      dataset_node=gis_node
    end
    traverser=dataset_node.outgoing(:AGGREGATION).depth(1).filter do
      prop_name=get_property('name')
      prop_distr=get_property('distribute')
      prop_select=get_property('select')
      prop_name==property and prop_distr==distribute and prop_select==select
    end
    traverser.first
  end

  def create_chart_dataset_aggr(aggr_node,renderer,type=:bar)
    ds=DefaultCategoryDataset.new()
    aggr_node.outgoing(:CHILD,:NEXT).depth(:all).each do |node|
      #      puts "count_node: #{node[:name]} -  #{node[:name]} - #{node[:value]}"
      if node.property? renderer.colorPropertyName
        renderer.addColor(node[renderer.colorPropertyName])
      end
      ds.addValue(java.lang.Double.parseDouble(node[:value].to_s), "name", node[:name]);
    end
    ds
  end

  def create_chart_dataset(nodes,category,values,type=:bar)
    if type==:bar
      ds=DefaultCategoryDataset.new()
      nodes.each do |node|
        puts node.props
        values.each do |value|
          #          puts node[value]
          #          puts node[category]
          ds.addValue(java.lang.Double.parseDouble(node[value].to_s), value, node[category].to_s);
        end
      end
    elsif type==:pie
      ds=DefaultPieDataset.new()
      nodes.each do |node|
        values.each do |value|
          ds.setValue(node[category].to_s,java.lang.Double.parseDouble(node[value].to_s));#TODO may be value?
        end
      end
    end
    ds
  end

  def update_chart_dataset(ds,average,p)
    average.each_with_index do |row,j|
      if j!=0
        val=row.last
        ds.addValue(java.lang.Double.parseDouble(val.to_s), p.to_s,row[3].to_s);
      end
    end
    ds
  end

  def update_chart_dataset(ds,aggregation,property,x_label)
    aggregation.each do |row|
      ds.addValue(java.lang.Double.parseDouble(row[property].to_s), property.to_s,row[x_label].to_s);
      #        ds.addValue(java.lang.Double.parseDouble(row[property].to_s), property.to_s,Hour.new(x_label).to_s);
    end
    ds
  end

  def create_time_chart_dataset(nodes,name,time_period,time_property,value)
    ds=TimeSeriesCollection.new()
    series=TimeSeries.new(name)
    puts "create_time_chart_dataset: #{name} : #{value}"
    nodes.each do |node|
      if time_period==:millisecond
        if node.property? value
          series.addOrUpdate(Millisecond.new(java.util.Date.new(node[time_property])), java.lang.Double.parseDouble(node[value].to_s));
        end
      elsif time_period==:hour
        puts "time_period==:hour #{Hour.new(java.util.Date.new(node[time_property])).to_s} = #{node[time_property]}"
        series.addOrUpdate(Hour.new(java.util.Date.new(node[time_property])), java.lang.Double.parseDouble(node[value].to_s));
      end
    end
    ds.addSeries(series)
    ds
  end

  def create_event_chart_dataset(nodes,name,time_period,time_property,event_property,event_value)
    ds=EventDataset.new(name,event_value)
    nodes.each do |node|
      if time_period==:millisecond
        if node.property? event_property
          puts "event found: #{node.neo_id} - #{node[time_property]} - #{node[event_property]}"
          ds.addEvent(node[event_property], Millisecond.new(java.util.Date.new(node[time_property])))
        end
      end
    end
    ds
  end

  def  get_sub_nodes_with_props(parent, node_type, relation, time)
    time_format = '%H:%M:%S'
    parent.outgoing(relation).depth(:all).stop_on do
      prop_time=get_property(Java::org.amanzi.neo.core.INeoConstants::PROPERTY_TIME_NAME)
      #      puts "#{node_type}.props #{props}"
      if prop_time.nil?
        false
      else
        t=DateTime.strptime(prop_time,time_format)
        Time.gm(t.year,t.mon,t.day,t.hour,t.min,t.sec)>=time
      end
    end
  end
end

class Search
  attr_accessor :name
  def initialize(name,params)
    @name=name
    @traversers=[Neo4j.ref_node.outgoing(:CHILD).depth(1)]
    @params=params
  end

  def traverse(direction, depth, *relation)
    new_traversers=[]
    if direction==:outgoing
      @traversers.each do |traverser|
        new_traversers<<traverser.outgoing(*relation).depth(depth)
      end
    elsif direction==:incoming
      @traversers.each do |traverser|
        new_traversers<<traverser.incoming(*relation).depth(depth)
      end
    else #both
      @traversers.each do |traverser|
        new_traversers<<traverser.both(*relation).depth(depth)
      end
    end
    @traversers=new_traversers
  end

  def filter(&block)
    new_traversers=[]
    @traversers.each do |traverser|
      new_traversers<<traverser.filter(&block)
    end
    @traversers=new_traversers
  end

  def stop(&block)
    new_traversers=[]
    @traversers.each do |traverser|
      new_traversers<<traverser.stop_on(&block)
    end
    @traversers=new_traversers
  end

  def parent(&block)
    self.instance_eval &block
    new_traversers=[]
    @traversers.each do |traverser|
      traverser.each do |node|
        new_traversers<<node
      end
    end
    @traversers=new_traversers
  end

  def where(&block)
    filter(&block)
  end

  def from(&block)
    parent(&block)
  end

  def each
    @traversers.each do |traverser|
      traverser.each do |node|
        yield node
      end
    end
  end
end

class CellID
  attr_writer :beginRange
  attr_writer :endRange
  def initialize(name)
    @fullID = name
  end

  #
  # Returns a succesor of a CellID
  #
  def succ
    if self.rowIndex<@endRange.rowIndex
      cellId=CellID.new(rowIndex+1,columnIndex)
      cellId.beginRange=@beginRange
      cellId.endRange=@endRange
      cellId
    else
      cellId=CellID.new(@beginRange.rowIndex,columnIndex+1)
      cellId.beginRange=@beginRange
      cellId.endRange=@endRange
      cellId
    end
  end

  #
  # Compares two cellIDs
  #
  def <=>(value)
    if @beginRange.nil?
      @beginRange=self
    end
    if @endRange.nil?
      @endRange=value
    end
    if self.rowIndex!=value.rowIndex
      self.rowIndex<=>value.rowIndex
    else
      self.columnIndex<=>value.columnIndex
    end
  end
end

class Report
  include NodeUtils
  attr_reader :name
  attr_accessor :date,:author
  def initialize(name)
    @name = name
  end

  def setup(&block)
    Neo4j::Transaction.run{
      self.instance_eval &block
      self
    }
  end

  def author (new_author)
    setAuthor(new_author)
  end

  def date (new_date)
    setDate(new_date)
  end

  def text (new_text)
    begin
      addPart(ReportText.new(new_text))
    rescue =>e
      comment="#{getErrors().size+1}) the text '#{new_text}' was not added because of the error: #{e}\n"
      addError(comment+e.backtrace.join("\n"))
    end
  end

  def file (file_name)
    setFile(file_name.to_s)
  end

  def image (image_file_name)
    begin
      addPart(ReportImage.new(image_file_name))
    rescue =>e
      comment="#{getErrors().size+1}) the image '#{image_file_name}' was not added because of the error: #{e}\n"
      addError(comment+e.backtrace.join("\n"))
    end
  end

  def table (title,parameters=nil,&block)
    begin
      currTable=ReportTable.new(title)
      currTable.setup(&block)
      currTable.height=parameters[:height] if !parameters.nil?
      currTable.width=parameters[:width]  if !parameters.nil?
      addPart(currTable)
    rescue =>e
      comment="#{getErrors().size+1}) the table '#{title}' was not added because of the error: #{e}\n"
      addError(comment+e.backtrace.join("\n"))
    end
  end

  def chart(name,parameters=nil,&block)
    begin
      currChart=Chart.new(name)
      currChart.setup(&block)
      currChart.height=parameters[:height]||400 if !parameters.nil?
      currChart.width=parameters[:width]||400 if !parameters.nil?
      addPart(currChart)
    rescue =>e
      comment="#{getErrors().size+1}) the chart '#{name}' was not added because of the error: #{e}\n"
      addError(comment+e.backtrace.join("\n"))
    end
  end

  def map(title,parameters,&block)
    begin
      udig_map=parameters[:map]
      puts udig_map.nil?
      #      puts "map: #{udig_map.getID()}"
      currMap=ReportMap.new(title,udig_map)
      currMap.height=parameters[:height]
      currMap.width=parameters[:width]
      currMap.setup(&block)
      addPart(currMap)
    rescue =>e
      comment="#{getErrors().size+1}) the map '#{title}' was not added because of the error: #{e}\n"
      addError(comment+e.backtrace.join("\n"))
    end
  end

  def save
    engine=PDFPrintingEngine.new
    engine.printReport(self)
    puts "Report '#{@name}' saved"
  end
end

class ReportMap
  def setup(&block)
    self.instance_eval &block
    self
  end

  def layers
    map.layers
  end
end

class ReportTable
  include NodeUtils

  attr_accessor :title, :properties, :nodes, :sheet, :range, :kpi
  def initialize(title)
    self.title = title
  end

  def setup(&block)
    self.instance_eval &block if block_given?
    get_data
  end

  def  get_data
    Neo4j::Transaction.run {
      if !@nodes.nil?
        #      puts @nodes.class
        @nodes.each do |n|
          n=Neo4j.load_node(n) if n.is_a? Fixnum
          @properties=n.props.keys if @properties.nil?
          row=Array.new
          @properties.each do |p|
            if p!="id"
              row<<if n.property? p then n.get_property(p).to_s else "" end
            else
              row<<n.neo_id.to_s
            end
          end
          addRow(row.to_java(java.lang.String))
        end
        setHeaders(@properties.to_java(java.lang.String)) if @properties.is_a? Array
      elsif !@sheet.nil?
        sheetName=@sheet
        sheetNodes=Neo4j.load_node($RUBY_PROJECT_NODE_ID).outgoing(:SPREADSHEET).depth(:all).filter      do
          get_property('name')== sheetName
        end
        range=@range
        columnNodes=sheetNodes.first.outgoing(:COLUMN).depth(1).filter do
          #          puts "sheetNode.traverse.outgoing(:COLUMN) #{get_property(:name)}"
          puts get_property(:name)
          index=CellID.getColumnIndexFromCellID(get_property(:name))
          puts "index #{index}"
          index>=range.begin.getColumnIndex() and index<=range.end.getColumnIndex()
        end
        columnNodes.each do |col|
          puts "---> col #{col}"
          cells=col.traverse.outgoing(:COLUMN_CELL).depth(1).filter do
            name=relationship(:ROW_CELL,:incoming).start_node[:name]
            puts "cells row name #{name}"
            rowIndex=name.to_i
            rowIndex>=range.begin.getRowName().to_i and rowIndex<=range.end.getRowName().to_i
            true
          end
          cells.each {|cell| puts "cell #{cell}"}
        end
        #      TODO traverse rows and columns
      elsif !@kpi.nil?
        @kpi.each_with_index do |row,i|
          if i==0
            setHeaders(row.to_java(java.lang.String))
          else
            addRow(row.to_java(java.lang.String))
          end
        end
      end
    }
  end

  def select(name,params=nil,&block)
    Neo4j::Transaction.run {
      #          puts "======> [ReportTable.select]"
      nodes=Search.new(name,params)
      nodes.instance_eval &block
      @properties=params[:properties]if !params.nil?  #TODO
      nodes.each do |n|
        properties=n.props.keys if @properties.nil?
        row=Array.new
        @properties.each do |p|
          if p!="id"
            row<< if n.property? p then n.get_property(p).to_s else "" end
          else
            row<<n.neo_id.to_s
          end
        end
        addRow(row.to_java(java.lang.String))
      end
      setHeaders(@properties.to_java(java.lang.String)) if @properties.is_a? Array
    }
  end

end

class Chart
  include NodeUtils

  attr_accessor :title, :type, :orientation, :domain_axis, :range_axis, :range_axis_ticks, :legend
  attr_writer :categories,:values, :nodes, :statistics
  attr_writer:property, :distribute, :select
  attr_writer :drive, :event, :property1, :property2, :start_time, :length
  attr_accessor :dataset, :properties, :aggregation, :kpi
  attr_accessor :renderer
  attr_writer :data, :time, :threshold, :threshold_label
  def initialize(title)
    @datasets=[]
    self.title = title
  end

  def sheet=(sheet_name)
    @sheet=sheet_name
  end

  def subtitle=(subtitle)
    addSubtitle(subtitle)
  end

  def select(name,params,&block)
    Neo4j::Transaction.run {
      nodes=Search.new(name,params)
      nodes.instance_eval &block
      if @type==:time
        event=params[:event]
        if !event.nil?
          #assume that we have one value
          @datasets<< create_event_chart_dataset(nodes,name,params[:time_period],params[:categories],params[:values],event)
        else
          params[:values].each do |value|
            @datasets<< create_time_chart_dataset(nodes,value,params[:time_period],params[:categories],value)
          end
        end
      else
        puts params[:values]
        puts params[:categories]
        if params[:values].is_a? String
          #            setDataset(create_chart_dataset(nodes,params[:categories],[params[:values]]))
          @datasets<<create_chart_dataset(nodes,params[:categories],[params[:values]])
        elsif params[:values].is_a? Array
          @datasets<<create_chart_dataset(nodes,params[:categories],params[:values])
          #            setDataset(create_chart_dataset(nodes,params[:categories],params[:values]))
        else
          puts "Error: Only Strings or Arrays of Strings are supported for chart values!"
        end
      end
    }
  end

  def setup(&block)
    self.instance_eval(&block) #if block_given?
    Neo4j::Transaction.run {
      #JFreeChart specific settings
      if !@orientation.nil?
        if @orientation==:vertical
          setOrientation(PlotOrientation::VERTICAL)
        elsif @orientation==:horizontal
          setOrientation(Java::org.jfree.chart.plot.PlotOrientation::HORIZONTAL)
        end
      end
      setDomainAxisLabel(@domain_axis) if !@domain_axis.nil?
      setRangeAxisLabel(@range_axis) if !@range_axis.nil?
      setShowLegend(@legend) if !@legend.nil?
      @type=:bar if @type.nil?
      setChartType(ChartType.value_of(@type.to_s.upcase))
      #
      if !@sheet.nil?
        setCategories(@categories.begin,@categories.end)if !@categories.nil?
        setValues(@values.begin,@values.end) if !@values.nil?
        setSheet(@sheet)
      elsif !@nodes.nil?
        setNodeIds(@nodes.to_java(java.lang.Long)) if @nodes.is_a? Array
        setCategoriesProperty(@categories) if !@categories.nil? and  @categories.is_a? String
        setValuesProperties(@values.to_java(java.lang.String)) if !@values.nil? and   @values.is_a? Array
        #            setDataset(create_chart_dataset(@nodes,@categories,@values))
        @datasets<<create_chart_dataset(@nodes,@categories,@values)
      elsif !@statistics.nil?
        puts "@statistics #{@statistics}"
        dataset_node=find_dataset(@statistics)
        puts "dataset_node #{dataset_node}"
        if !@property.nil? and !@distribute.nil? and !@select.nil?
          aggr_node=find_aggr_node(dataset_node,@property,@distribute,@select)
          @renderer=CustomBarRenderer.new
          @datasets<<create_chart_dataset_aggr(aggr_node,@renderer)
          setRenderer(@renderer)
        end
      elsif !@dataset.nil?
        ds=DefaultCategoryDataset.new
        @properties.each do |property|
          @time||="time"
          update_chart_dataset(ds,@dataset,property,@time)
        end
        @datasets<<ds
      elsif !@kpi.nil?
        ds=DefaultCategoryDataset.new()
        @datasets<<update_chart_dataset(ds,@kpi,"value")
      elsif !@data.nil?
        ds=TimeSeriesCollection.new()
        if @values.is_a? String
          ds_series=TimeSeries.new(@values)
        elsif @values.is_a? Array
          ds_series=[]
          @values.each {|val| ds_series<<TimeSeries.new(val)}
        end
        if !@threshold.nil?
          ds_time=TimeSeriesCollection.new()
          ds_time_series=TimeSeries.new(!@threshold_label.nil? ? @threshold_label : "Threshold")
        end

        @data.each do |row|
          if !row[@time].nil?
            time=java.util.Date.new(row[@time])
            if @aggregation==:hourly
              date=Hour.new(time)
            elsif @aggregation==:daily
              date=Day.new(time)
            elsif @aggregation==:minutely
              date=Minute.new(time)
            elsif @aggregation==:weekly
              date=Week.new(time)
            elsif @aggregation==:monthly
              date=Month.new(time)
            end
          end
          if @values.is_a? String
            ds_series.add(date, java.lang.Double.parseDouble(row[@values].to_s)) if !row[@time].nil? and !row[@values].nil?
          elsif @values.is_a? Array
            @values.each_with_index do |val,i|
              ds_series[i].add(date, java.lang.Double.parseDouble(row[val].to_s))
            end
          end
          #          ds_series.add(date, java.lang.Double.parseDouble(row[@values].to_s)) if !row[@time].nil? and !row[@values].nil?
          ds_time_series.add(date, @threshold) if !@threshold.nil? and !row[@time].nil?
        end
        if @values.is_a? String
          ds.addSeries(ds_series)
        elsif @values.is_a? Array
          ds_series.each do |series|
            ds.addSeries(series)
          end
        end
        #        ds.addSeries(ds_series)
        if !@threshold.nil?
          ds_time.addSeries(ds_time_series)
          @datasets<<ds_time
        end
        if @aggregation==:hourly
          width=1000*60*60*0.5
        elsif @aggregation==:daily
          width=1000*60*60*24*0.5
        elsif @aggregation==:weekly
          width=1000*60*60*24*7*0.5
        elsif @aggregation==:monthly
          width=1000*60*60*24*7*4*0.5
        else
          width=0.5
        end
        xybardataset=XYBarDataset.new(ds,width)
        @datasets<<XYBarDataset.new(ds,width)
      end

      if @type==:time
        plot=Java::org.jfree.chart.plot.XYPlot.new
        #        plot.setDomainAxis(createDateAxis(nil,@aggregation)) if @aggregation
        for i in 0..@datasets.size-1
          puts "i=#{i} #{@datasets[i]}" #TODO delete debug info
          Charts.applyDefaultSettings(plot,@datasets[i],i)
        end
        Charts.applyMainVisualSettings(plot, getRangeAxisLabel(),getDomainAxisLabel(),getOrientation())
      elsif @type==:bar
        plot=Java::org.jfree.chart.plot.CategoryPlot.new
        plot.setRenderer(@renderer) if @renderer
        plot.setDataset(@datasets[0])
      elsif @type==:combined
        plot=Java::org.jfree.chart.plot.XYPlot.new
        #        date_axis=DateAxis.new("Date")
        #        if @aggregation==:hourly
        #          date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::HOUR,24,SimpleDateFormat.new("HH, dd")))
        #        elsif @aggregation==:daily
        #          date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::DAY,1,SimpleDateFormat.new("MM.dd")))
        #        elsif @aggregation==:weekly
        #          date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::DAY,7,SimpleDateFormat.new("w, yyyy")))
        #        elsif @aggregation==:monthly
        #          date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::MONTH,1,SimpleDateFormat.new("MMMMM")))
        #        end
        #        plot.setDomainAxis(date_axis)
        plot.setDomainAxis(createDateAxis(@range_axis_ticks,@aggregation))
        #        Charts.applyDefaultSettingsToPlot(plot)
        for i in 0..@datasets.size-1
          Charts.applyDefaultSettingsToDataset(plot,@datasets[i],i)
        end
        Charts.applyMainVisualSettings(plot, getRangeAxisLabel(),getDomainAxisLabel(),getOrientation())
      end
      setPlot(plot)
    }
    self
  end

  def createDateAxis(tick_units,aggregation)
    date_axis=DateAxis.new("Date")
    if !tick_units.nil? and aggregation==:hourly
      tu=TickUnits.new()
      tick_units.each do |name,config|
        unit=config[0]
        num=config[1]
        format=config[2]
        if unit==:hour
          tu.add(DateTickUnit.new(DateTickUnit::HOUR,num,SimpleDateFormat.new(format)))
        elsif unit==:day
          tu.add(DateTickUnit.new(DateTickUnit::DAY,num,SimpleDateFormat.new(format)))
        elsif unit==:month
          tu.add(DateTickUnit.new(DateTickUnit::MONTH,num,SimpleDateFormat.new(format)))
        elsif unit==:week #ignore number of ticks at this case
          tu.add(DateTickUnit.new(DateTickUnit::HOUR,7,SimpleDateFormat.new(format)))
        end
      end
      date_axis.setStandardTickUnits(tu)
    else
      if aggregation==:hourly
        #        date_axis.setDateFormatOverride(SimpleDateFormat.new("HH, dd"))
        date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::HOUR,24,SimpleDateFormat.new("HH, dd")))
      elsif aggregation==:daily
        #        date_axis.setDateFormatOverride(SimpleDateFormat.new("MM.dd"))
        date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::DAY,1,SimpleDateFormat.new("MM.dd")))
      elsif aggregation==:weekly
        #        date_axis.setDateFormatOverride(SimpleDateFormat.new("w, yyyy"))
        date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::DAY,7,SimpleDateFormat.new("w, yyyy")))
      elsif aggregation==:monthly
        #        date_axis.setDateFormatOverride(SimpleDateFormat.new("MMMMM"))
        date_axis.setTickUnit(DateTickUnit.new(DateTickUnit::MONTH,1,SimpleDateFormat.new("MMMMM")))
      end
    end
    date_axis.setVerticalTickLabels(true);
    date_axis
  end
end

def method_missing(method_id, *args)
  if method_id.to_s =~ /([a-z]{1,3})([0-9]+)/
    CellID.new(method_id.to_s)
  else
    #      $missing_methods=$missing_methods||Hash.new
    #      if !$missing_methods.has_key method_id
    #        $missing_methods[method_id]=[]
    #      end
    puts "report.rb: Unknown method: #{method_id}"
    super.method_missing(method_id.to_s, *args)
  end
end

def report (name, &block)
  report=Report.new(name)
  begin
    report.setup(&block)
  rescue =>e
    comment="#{report.getErrors().size+1}) #{e}:\n"
    report.addError(comment+e.backtrace.join("\n"))
    ReportUtils::showErrorDlg("A ruby exception occurred during the report creation: #{e}",e.backtrace.join("\n"))
    puts e
  ensure
    report
  end
end

def chart(name,&block)
  begin
    currChart=Chart.new(name)
    currChart.setup(&block)
    $report_model.createPart(currChart)
  rescue =>e
    ReportUtils::showErrorDlg("A ruby exception occurred: #{e}",e.backtrace.join("\n"))
  end
end

def text (new_text)
  begin
    $report_model.createPart(ReportText.new(new_text))
  rescue =>e
    ReportUtils::showErrorDlg("A ruby exception occurred: #{e}",e.backtrace.join("\n"))
  end
end

def image (image_file_name)
  begin
    $report_model.createPart(ReportImage.new(image_file_name))
  rescue =>e
    ReportUtils::showErrorDlg("A ruby exception occurred: #{e}",e.backtrace.join("\n"))
  end
end

