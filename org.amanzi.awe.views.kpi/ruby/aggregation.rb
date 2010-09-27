require 'java'
include_class 'java.text.SimpleDateFormat'
module Aggregatable
  def aggregate(properties_to_aggr,period,time_property,*values,&block)
    aggregation={}
    each do |node|
      result=(properties_to_aggr.is_a? Array) ? properties_to_aggr.collect{|p| node[p]} : [node[properties_to_aggr]]
      args=[]
      values.each do |val_name|
        puts "node[val_name] #{node[val_name]}"
        args<<node[val_name]
      end
      #TODO it's a quick fix for datasets which have both date and time String properties
      time=clear(period,(time_property.is_a? Array) ? time_property.collect{|p| node[p]} : [node[time_property]])
#      if ((t=node[time_property]).is_a? String) and !(d=node['date']).nil?
#        time=clear(period,t,d)
#      else
#        time=clear(period,t)
#      end
      result<<time
      result<< (block_given? ? [(yield args.size==1 ? args[0] : args)]: args)
      level=(properties_to_aggr.is_a? Array) ? properties_to_aggr.size : 1
      aggregation=update_aggregation(aggregation,result,level)
    end
    #    puts print_values(aggregation)
    puts "aggregation #{aggregation}"
    puts "properties_to_aggr #{properties_to_aggr}"
    puts "time_property #{time_property}"
    puts "values #{values}"
#    TimeAggregation.new(aggregation,properties_to_aggr,time_property,*values)
    TimeAggregation.new(aggregation,properties_to_aggr,"time",*values)
  end

  def update_aggregation(aggregation,array,level)
    key=array[0]
    values=array.size==2 ? array[1] : array[1,array.size-1]
    if aggregation.has_key? key
      if level>0
        aggregation[key]=update_aggregation(aggregation[key],values,level-1)
      else
        aggregation[key]<<values
      end
    else
      aggregation[key]=level>0 ? update_aggregation({},values,level-1)  : [values]
    end
    aggregation
  end
  
  def parse_time(time)
    puts time
#    SimpleDateFormat.new("dd.MM.yyyy h:mm a").parse(time)
    SimpleDateFormat.new("MM.dd.yyyy").parse(time)
  end
  
  def clear(aggregation, time)
    calendar=java.util.GregorianCalendar::instance
    if time.is_a? Array
      calendar.time=parse_time(time.join(" "))
    elsif time.is_a? String
      calendar.time=parse_time(time)
    else
      calendar.time_in_millis=time
    end
    case aggregation
    when :hourly
      calendar.set(java.util.Calendar::MILLISECOND,0)
      calendar.set(java.util.Calendar::SECOND,0)
      calendar.set(java.util.Calendar::MINUTE,0)
    when :daily
      calendar.set(java.util.Calendar::MILLISECOND,0)
      calendar.set(java.util.Calendar::SECOND,0)
      calendar.set(java.util.Calendar::MINUTE,0)
      calendar.set(java.util.Calendar::HOUR,0)
    when :weekly
      calendar.set(java.util.Calendar::MILLISECOND,0)
      calendar.set(java.util.Calendar::SECOND,0)
      calendar.set(java.util.Calendar::MINUTE,0)
      calendar.set(java.util.Calendar::HOUR,0)
    when :monthly
      calendar.set(java.util.Calendar::MILLISECOND,0)
      calendar.set(java.util.Calendar::SECOND,0)
      calendar.set(java.util.Calendar::MINUTE,0)
      calendar.set(java.util.Calendar::HOUR,0)
      calendar.set(java.util.Calendar::DAY_OF_MONTH,1)
    when :yearly
      calendar.set(java.util.Calendar::MILLISECOND,0)
      calendar.set(java.util.Calendar::SECOND,0)
      calendar.set(java.util.Calendar::MINUTE,0)
      calendar.set(java.util.Calendar::HOUR,0)
      calendar.set(java.util.Calendar::DAY_OF_MONTH,1)
      calendar.set(java.util.Calendar::MONTH,0)
    else
      #    nothing
    end
    calendar.time_in_millis
  end
end

class TimeAggregation
  def initialize(aggregation,aggr_props,time_prop,*properties)
    @aggr_properties=aggr_props<<time_prop
    @val_properties=properties
    @aggregation=aggregation
  end

  def sum(matrix)
    if (first=matrix.first).is_a? Array #we have 2 dimensional matrix
      s=Array.new(first.size){0.0}
      matrix.each do |row|
        row.each_with_index do |element,i|
          s[i]+=element
        end
      end
    else
      s=0.0
      matrix.each {|element| s+=element}
    end
    s
  end

  def average(matrix)
    if (first=matrix.first).is_a? Array #we have 2 dimensional matrix
      s=Array.new(first.size){[0.0,0]}
      matrix.each do |row|
        row.each_with_index do |element,i|
          s[i][0]+=element
          s[i][1]+=1
        end
      end
      s.collect{|a| a[0].quo(a[1])}
    else
      s=0.0
      c=0
      matrix.each_with_index do |element,i|
        s+=element
        c+=1
      end
      s.quo(c)
    end
  end

  def count(matrix)
    if (first=matrix.first).is_a? Array #we have 2 dimensional matrix
      matrix.transpose.collect {|element| element.nitems}
    else
      matrix.collect{|e| e.nil? ? 0 : 1}
    end
  end

  def each(&block)
    @aggregation.each do |k,v|
      traverse(v,{@aggr_properties[0]=>k},1,&block)
    end
  end

  def traverse(old_str,new_str,level,&block)
    if old_str.is_a? Array
      hash={}
      res=average(old_str) #TODO add func
      @val_properties.each_with_index do |v,i|
        hash[v]=res[i]
      end
      yield new_str.merge(hash)
    elsif old_str.is_a? Hash
      old_str.each do |k,v|
        traverse(v,new_str.merge(@aggr_properties[level]=>k),level+1,&block)
      end
    end
  end
end

