require 'java'

java_import 'org.amanzi.awe.statistics.template.Condition'
java_import 'org.amanzi.awe.statistics.template.TemplateColumn'
java_import 'org.amanzi.awe.statistics.template.Template'
java_import 'org.amanzi.awe.statistics.template.Threshold'
java_import 'org.amanzi.awe.statistics.engine.KpiBasedHeader'
java_import 'org.amanzi.awe.statistics.engine.PropertyBasedHeader'

class Thresholds
  attr_reader :threshold
  def alert()
    self
  end

  def >(value)
    @threshold=Threshold.new(java.lang.Double.new(value),">")
  end

  def >=(value)
    @threshold=Threshold.new(java.lang.Double.new(value),">=")
  end

  def <(value)
    @threshold=Threshold.new(java.lang.Double.new(value),"<")
  end

  def <=(value)
    @threshold=Threshold.new(java.lang.Double.new(value),"<=")
  end
  alias lt :<
  alias le :<=
  alias gt :>
  alias ge :>=
end

class TemplateColumn
  attr_accessor :name
  def initialize(name)
    self.name=name
  end

  def property(property)
    setHeader(PropertyBasedHeader.new(property.to_s,@name))
    self
  end

  def formula(formula)
    setHeader(KpiBasedHeader.new(formula,@name))
    self
  end

  def aggregation(aggregation)
    setFunction(aggregation.to_s)
    self
  end

  alias property= property
  alias formula= formula
  alias aggregation= aggregation

  def thresholds(&block)
    t=Thresholds.new
    t.instance_eval &block if block_given?
    setThreshold(t.threshold)
  end

  def format(type,pattern)
    type||=:decimal
    if type=:decimal
      setFormat(java.text.DecimalFormat.new(pattern))
    elsif type=:date
      setFormat(java.text.SimpleDateFormat.new(pattern))
    end
  end

  def method_missing(method, *args)
    if method.to_s=~/alert_(\w+)/
      thresholds{alert.send($1,*args)}
    else
      super.method_missing(method, *args)
    end
  end
end

class Template
  attr_accessor :name
  def metadata(hash)
    setMetadata(java.util.HashMap.new(hash))
  end

  def column(name, &block)
    column=TemplateColumn.new(name)
    column.instance_eval &block if block_given?
    add(column)
    column
  end

  def author(author)
    setAuthor(author)
  end

  def date(date)
    setDate(date)
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
