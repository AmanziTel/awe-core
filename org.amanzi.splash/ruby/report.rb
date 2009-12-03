require 'java'
require 'neo4j'

#require 'neo4j/auto_tx'
require 'ruby/cell'

include_class org.amanzi.neo.core.database.nodes.CellID
include_class org.amanzi.splash.report.model.Report
include_class org.amanzi.splash.report.model.Chart
include_class org.amanzi.splash.report.model.ReportText
include_class org.amanzi.splash.report.model.ReportImage
include_class org.amanzi.splash.report.model.ReportTable
include_class org.amanzi.neo.core.service.NeoServiceProvider
include_class org.jfree.data.category.DefaultCategoryDataset;

neo_service = NeoServiceProvider.getProvider.getService
database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation
Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)

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
    traverser=Neo4j.ref_node.traverse.outgoing(:CHILD).depth(1).filter      do
      get_property(:name)== dataset_name
    end
    traverser.first
  end

  def find_aggr_node(dataset_node,property, distribute, select)
    traverser=dataset_node.traverse.outgoing(:AGGREGATION).depth(1).filter do
      prop_name=get_property(:name)
      prop_distr=get_property(:distribute)
      prop_select=get_property(:select)
      prop_name==property and prop_distr==distribute and prop_select==select
    end
    traverser.first
  end

  def create_chart_dataset(aggr_node,type=:bar)
    ds=DefaultCategoryDataset.new()
    aggr_node.traverse.outgoing(:CHILD).depth(:all).each do |node|
      puts "count_node: #{node.get_property(:name)} - #{node.get_property(:value)}"
      ds.addValue(java.lang.Double.parseDouble(node.get_property(:value).to_s), "name", node.get_property(:name).to_s);
    end
    ds
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
    self.instance_eval &block
    self
  end

  def author (new_author)
    setAuthor(new_author)
  end

  def date (new_date)
    setDate(new_date)
  end

  def text (new_text)
    addPart(ReportText.new(new_text))
  end

  def image (image_file_name)
    addPart(ReportImage.new(image_file_name))
  end

  def table (title,&block)
    currTable=ReportTable.new(title)
    currTable.setup(&block)
    addPart(currTable)
  end

  #    def table (name,options={})
  #      currTable=Table.new()
  #      #          currTable.setup(&block)
  #
  #      addPart(currTable)
  #    end

  def chart(name,&block)
    currChart=Chart.new(name)
    currChart.setup(&block)
    addPart(currChart)
  end
end

class ReportTable
  include NodeUtils
  extend Neo4j::TransactionalMixin

  attr_accessor :title, :properties, :nodes, :sheet, :range
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
        begin
          @nodes.each do |n|
            n=Neo4j.load(n) if n.is_a? Fixnum
            @properties=n.props.keys if @properties.nil?
            row=Array.new
            @properties.each {|p| if p!="id" then row<<n.get_property(p).to_s else row<<n.neo_node_id.to_s end}
            addRow(row.to_java(java.lang.String))
          end
          setHeaders(@properties.to_java(java.lang.String)) if @properties.is_a? Array
        rescue =>e
          puts "An exception occured #{e}"
        end
      elsif !@sheet.nil?
        sheetName=@sheet
        sheetNodes=Neo4j.load($RUBY_PROJECT_NODE_ID).traverse.outgoing(:SPREADSHEET).depth(:all).filter      do
          get_property(:name)== sheetName
        end
        range=@range
        columnNodes=sheetNodes.first.traverse.outgoing(:COLUMN).depth(1).filter do
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

      end
    }
  end
#  transactional :get_data
end

class Chart
  include NodeUtils
  extend Neo4j::TransactionalMixin

  attr_accessor :sheet, :name, :type
  attr_writer :categories,:values, :nodes, :statistics
  attr_writer:property, :distribute, :select
  attr_writer :drive, :event, :property1, :property2, :start_time, :length
  def initialize(name)
    self.name = name
  end

  def setup(&block)
    self.instance_eval &block if block_given?
    Neo4j::Transaction.run {
      begin
        @type=:bar if @type.nil?
        setChartType(Java::org.amanzi.splash.chart.ChartType.value_of(@type.to_s.upcase))
        if !@sheet.nil?
          if !@categories.nil?
            if @categories.is_a? Range
              setCategories(@categories.begin,@categories.end)
            elsif @categories.is_a? String
              setCategoriesProperty(@categories)
            end
          end
          if !@values.nil?
            if @values.is_a? Range
              setValues(@values.begin,@values.end)
            elsif @values.is_a? Array
              setValuesProperties(@values.to_java(java.lang.String))
            end
            setSheet(@sheet)
          end
        elsif !@nodes.nil?
          if @nodes.is_a? Array
            setNodeIds(@nodes.to_java(java.lang.Long))
          end
        elsif !@statistics.nil?
          dataset_node=find_dataset(@statistics)
          #      puts "dataset_node #{dataset_node}"
          if !@property.nil? and !@distribute.nil? and !@select.nil?
            #        puts "@property #{@property} @distribute #{@distribute} @select #{@select} "
            aggr_node=find_aggr_node(dataset_node,@property,@distribute,@select)
            #        puts "aggr_node #{aggr_node}"
            setDataset(create_chart_dataset(aggr_node))

          end
        elsif !@drive.nil?
          dataset_node=find_dataset(@drive)
          puts "dataset_node #{dataset_node}"
          puts "@event #{@event}"
          puts "@property1 #{@property1}"
          puts "@property2 #{@property2}"
          puts "@start_time #{@start_time}"
          puts "@length #{@length}"
        end
      rescue =>e
        puts "An exception occured during chart setup: #{e}"
      end
    }
    self
  end
  transactional :find_dataset, :find_aggr_node, :create_chart_dataset
end

def method_missing(method_id, *args)
  if method_id.to_s =~ /([a-z]{1,3})([0-9]+)/
    CellID.new(method_id.to_s)
  else
    puts "report.rb: Unknown method: #{method_id}"
    super.method_missing(method_id.to_s, *args)
  end
end

def report (name, &block)
  report=Report.new(name)
  report.setup(&block)
  $report_model.updateReport(report)
end

