require 'java'
require 'ruby/cell'

include_class org.amanzi.neo.core.database.nodes.CellID
include_class org.amanzi.splash.report.model.Report
include_class org.amanzi.splash.report.model.Chart
include_class org.amanzi.splash.report.model.ReportText
include_class org.amanzi.splash.report.model.ReportImage

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

  def chart(name,&block)
    currChart=Chart.new(name)
    currChart.setup(&block)
    addPart(currChart)
  end
end

class Chart
  attr_accessor :sheet, :name
  attr_writer :categories,:values
  def initialize(name)
    self.name = name
  end

  def setup(&block)
    self.instance_eval &block if block_given?
    setCategories(@categories.begin,@categories.end)
    setValues(@values.begin,@values.end)
    setSheet(@sheet)
    self
  end

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
