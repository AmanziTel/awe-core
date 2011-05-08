include NodeUtils
time_property="timestamp"
unit="#"
ticks={:hourly=>[:hour,1,"HH:00, dd"],
  :three_hourly=>[:hour,3,"HH:00, dd"],
  :six_hourly=>[:hour,6,"HH:00, dd"],
  :half_day=>[:hour,12,"HH:00, dd"],
  :daily=>[:day,1,"MM.dd"],
  :weekly=>[:day,7,"w, yyyy"],
  :monthly=>[:month,1,"MMMMM"]}

def get_statistics(dataset_name,dataset_type,template_name,statistics,time_property,groups=nil)
  ds_root=dataset(dataset_name,dataset_type)
  analysis_root=find_first(ds_root,{"type"=>"statistics_root","template"=>template_name},:ANALYSIS)
  level=find_first(analysis_root,{"type"=>"statistics","name"=>"#{statistics}"},:CHILD)
  data=select_properties :key=>"type",:value=>"name" do
    from do
      root level
      traverse :CHILD, :NEXT
      depth :all
      if groups.nil?
        where {get_property("type")=="s_group"}
      else
        where {get_property("type")=="s_group" and (property? "name" and groups.include? get_property("name"))}
      end
      select_properties [time_property,"name"] do
        from do
          traverse :CHILD, :NEXT
          depth :all
          stop_on {get_property("type")=="s_group"}
          where {get_property("type")=="s_row"}
          select_properties "value"=>"value","name"=>"kpi_name" do
            from do
              traverse :CHILD, :NEXT
              depth :all
              stop_on {get_property("type")=="s_row"}
              where {get_property("type")=="s_cell" and property? "value"}
            end
          end
        end
      end
    end
  end
  group_data=[]
  sum=0.0
  count=0
  groups!=[]
  averages={}
  data.each do |row|
    group=row["s_group"]
    kpi_name=row["kpi_name"]
    groups<<group unless groups.include? group
    period=row["name"]
    if period!="total"
      group_data<<{time_property=>row[time_property],group=>row["value"],"kpi_name"=>kpi_name}
    else
      val=row["value"]
      if !averages.include? kpi_name
        averages[kpi_name]=[0.0,0]
      else
        averages[kpi_name][0]+=val
        averages[kpi_name][1]+=1
      end
    end
  end
  [group_data,averages,groups]
end

report "KPI report for #{dataset_name}\nstatistics - #{statistics}" do |r|
  stats=get_statistics(dataset_name,dataset_type,template_name,statistics, time_property,groups)
  kpis.each do |kpi|
    kpi_name=kpi[0]
    threshold=kpi[1]
    averages=stats[1][kpi_name]
    average=averages[1]==0?0.0:averages[0]/averages[1]
    data=stats[0].select{|x| x["kpi_name"]==kpi_name}
    chart kpi_name do |chart|
      chart.data=data
      chart.type=data.size<10 ? :combined : :time
      #        chart.subtitle=group
      chart.aggregation=aggregation
      chart.values=stats[2]
      chart.time=time_property
      chart.threshold=average if threshold
      chart.threshold_label="average (#{(average*1000).round/1000})" if threshold
      chart.range_axis_ticks=ticks
      #chart.range_axis="#"
    end
  end

  file("Report for #{dataset_name} - #{template_name} - #{statistics}.pdf")
  save

end