require 'java'

java_import 'org.amanzi.awe.statistics.template.Condition'
java_import 'org.amanzi.awe.statistics.template.ITemplateColumn'
java_import 'org.amanzi.awe.statistics.template.ITemplate'
java_import 'org.amanzi.awe.statistics.template.IThreshold'
java_import 'org.amanzi.awe.statistics.headers.impl.KPIBasedHeader'
java_import 'org.amanzi.awe.statistics.template.functions.impl.AggregationFunctions'

class Threshold
  include IThreshold
  attr_accessor :threshold
  attr_accessor :value
  attr_accessor :condition
  def alert()
    self
  end

  def >(value)
    self.value = value
    condition = ">"
  end

  def >=(value)
    self.value = value
    condition = ">="
  end

  def <(value)
    self.value = value
    condition = "<"
  end

  def <=(value)
    self.value = value
    condition = "<="
  end
  alias lt :<
  alias le :<=
  alias gt :>
  alias ge :>=
end

class TemplateColumn
  include ITemplateColumn

  attr_accessor :name
  attr_accessor :header
  attr_writer :function
  attr_accessor :threshold
  attr_accessor :format
  def initialize(name)
    @name=name
  end

  def formula(formula)
    @header = KPIBasedHeader.new(formula, @name)
    self
  end

  def aggregation(aggregation)
    @function = aggregation.to_s
    self
  end

  def thresholds(&block)
    t = Threshold.new
    t.instance_eval &block if block_given?
    @threshold = t.threshold
    self
  end

  def format(type,pattern)
    type||=:decimal
    case type
    when :decimal
      @format = java.text.DecimalFormat.new(pattern)
    when :date
      @format = java.text.SimpleDateFormat.new(pattern)
    end
    self
  end

  def method_missing(method, *args)
    if method.to_s=~/alert_(\w+)/
      thresholds{alert.send($1, *args)}
    else
      super.method_missing(method, *args)
    end
  end
  
  def function
    AggregationFunctions.getFunctionByName @function
  end

  alias formula= formula
  alias aggregation= aggregation
end

class Template
  include ITemplate

  attr_accessor :name
  def initialize(name)
    self.name = name
    @columns = {}
    @metadata = {}
  end

  def metadata(hash)
    @metadata = hash
  end
  
  def getColumn(name)
    @columns[name]
  end

  def column(name, &block)
    column=TemplateColumn.new(name)
    column.instance_eval &block if block_given?

    @columns[column.name] = column
    column
  end

  def author(author)
    setAuthor(author)
  end

  def date(date)
    setDate(date)
  end

  def calculate(element)
    result = {}
    @columns.each_value do |column|
        header = column.header
        result[header.name] = eval(header.formula + ' element')
    end
    
    java.util.HashMap.new(result)
  end

  def canResolve(model)
    result = true
    
    @metadata.each do |key, value|
      result &&= model.instance_eval(key.to_s).id.eql?(value.to_s)
    end
    
    result
  end
  
  def to_s
    @name
  end

end

def template(name,&block)
  begin
    t=Template.new(name)
    t.instance_eval &block if block_given?
  rescue =>e
    puts e
    puts e.backtrace.join("\n")
  end
  t
end
