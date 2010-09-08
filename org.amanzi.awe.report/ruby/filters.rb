require 'java'

include_class 'org.amanzi.awe.filters.experimental.Relation'

module LogicalExpressions
  def &(filter)
    puts "&filter"
    cf=CompositeFilter.new(self.property)
    cf.subfilters<<self<<filter
    cf.relations<<Relation::AND
    cf
  end

  def |(filter)
    puts "|filter"
    cf=CompositeFilter.new(self.property)
    cf.subfilters<<self<<filter
    cf.relations<<Relation::OR
    cf
  end
end

include_class 'java.util.ArrayList'

class ArrayList
  def <<(element)
    self.add(element)
    self
  end
end

include_class 'org.amanzi.awe.filters.experimental.GroupFilter'
include_class 'org.amanzi.awe.filters.experimental.Condition'

class GroupFilter
  def initialize(property)
    @property=property
  end

  def setup(&block)
    self.instance_eval &block
  end

  def filter(params)
    puts "GroupFilter.filter"
    prop=params[:property]
    f=Filter.new(!prop.nil? ? prop : @property)
    f.condition=Condition.value_of(params[:condition].to_s.upcase)
    f.value=params[:value]
    f
  end

  def add(filter)
    puts "GroupFilter.add"
    self.filters<<filter
  end

  def to_s
    s="GroupFilter:"
    self.filters.each {|f| s+="\n#{f}" }
    s
  end
end

include_class 'org.amanzi.awe.filters.experimental.Filter'

class Filter
  include LogicalExpressions
  def accepts?(value)
    if condition==:gt
      res=value>value
    elsif condition==:ge
      res=value>=value
    elsif condition==:lt
      res=value<value
    elsif condition==:le
      res=value<=value
    elsif condition==:eq
      res=value==value
    end
    res
  end

  def to_s
    property+" "+condition.to_s+" "+value.to_s
  end
end

include_class 'org.amanzi.awe.filters.experimental.CompositeFilter'

class CompositeFilter
  include LogicalExpressions

  def accepts?(value)
    n=subfilters.size
    res=subfilters[0].accepts? value
    for i in 1..n-1
      res1=subfilters[i].accepts? value
      if relations[i-1]==:and
        res=(res and res1)
      elsif relations[i-1]==:or
        res=(res or res1)
      end
    end
    res
  end

  def to_s
    n=subfilters.size
    s="("+subfilters[0].to_s+")"
    for i in 1..n-1
      s+=" "+relations[i-1].to_s+" ("+subfilters[i].to_s+")"
    end
    s
  end
end

def filters(property=nil,&block)
  f=GroupFilter.new(property)
  f.setup(&block)
  f
end