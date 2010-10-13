include NodeUtils

def generate_dial_charts(dataset_name,kpi_name,aggregation,type)
  report "#{kpi_name}" do
    ds_root=dataset(dataset_name,'oss')
    analysis_root=find_first(ds_root,{"type"=>"statistics_root"},:ANALYSIS)
    level=find_first(analysis_root,{"type"=>"statistics","name"=>"site, #{aggregation.to_s}"},:CHILD)
    data=select_properties ["name"] do
      from do
        root level
        traverse :CHILD, :NEXT
        depth :all
        where {get_property("type")=="s_group"}
        select_properties ["time"] do
          from do
            traverse :CHILD, :NEXT
            depth :all
            stop_on {get_property("type")=="s_group"}
            where {get_property("type")=="s_row" and get_property("name")=="total"}
            select_properties "value" do
              from do
                traverse :CHILD, :NEXT
                depth :all
                stop_on {get_property("type")=="s_row" or get_property("name")==kpi_name}
                where {get_property("type")=="s_cell" and get_property("name")==kpi_name}
              end
            end
          end
        end
      end
    end
    sum=0.0
    count=0
    rows=[]
    max=10
    data.each do |row|
      val=row["value"]
      rows<<row
      sum+=val
      count+=1
      max=val if val>max
    end

    chart "Entire network", :width=>250,:height=>250 do |chart|
      chart.type=:dial
      chart.data=sum/count
    end
    text "10 worst sites"
    rows.sort {|x,y| y["value"] <=> x["value"] }[0,10].each do |row|
      val=(row["value"]*100).round/100
      chart "#{row["name"]}",:width=>250,:height=>250 do |chart|
        chart.type=:dial
        chart.subtitle="#{val}%"
        chart.data= kpi_name.downcase=~/handover/ ? val : (val<=10.0? val : 10.0)
      end
    end
    file("Dial_#{kpi_name}.pdf")
    save
  end
end

def generate_reports(dataset_name,kpi_name,aggregation,type)
  unit="#"
  ticks={:hourly=>[:hour,1,"HH:00, dd"],
    :three_hourly=>[:hour,3,"HH:00, dd"],
    :six_hourly=>[:hour,6,"HH:00, dd"],
    :half_day=>[:hour,12,"HH:00, dd"],
    :daily=>[:day,1,"MM.dd"],
    :weekly=>[:day,7,"w, yyyy"],
    :monthly=>[:month,1,"MMMMM"]}
  ds_root=dataset(dataset_name,'oss')
  analysis_root=find_first(ds_root,{"type"=>"statistics_root"},:ANALYSIS)
  level=find_first(analysis_root,{"type"=>"statistics","name"=>"site, #{aggregation.to_s}"},:CHILD)

  data=select_properties :key=>"type",:value=>"name" do
    from do
      root level
      traverse :CHILD, :NEXT
      depth :all
      where {get_property("type")=="s_group"}
      select_properties ["time","name"] do
        from do
          traverse :CHILD, :NEXT
          depth :all
          stop_on {get_property("type")=="s_group"}
          where {get_property("type")=="s_row"}
          select_properties "value" do
            from do
              traverse :CHILD, :NEXT
              depth :all
              stop_on {get_property("type")=="s_row" or get_property("name")==kpi_name}
              where {get_property("type")=="s_cell" and get_property("name")==kpi_name}
            end
          end
        end
      end
    end
  end
  site_data={}
  averages={}
  sum=0.0
  count=0
  max=10
  puts "aggregating sites"
  data.each do |row|
    site=row["s_group"]
    period=row["name"]
    if period!="total"
      if site_data.has_key? site
        site_data[site]<<row
      else
        site_data[site]=[row]
      end
    else
      val=row["value"]
      sum+=val
      count+=1
      max=val if val>max
      averages[site]=val
    end
  end
  puts "creating reports"
  puts averages
  report 'KPI report' do

    site_data.each do |site,data|
      chart kpi_name,:width=>250,:height=>250 do |chart|
        chart.data=data
        chart.type=type
        chart.subtitle=site
        chart.aggregation=aggregation
        chart.values="value"
        chart.time="time"
        chart.threshold=averages[site]
        chart.threshold_label="average (#{(averages[site]*1000).round/1000})"
        chart.range_axis_ticks=ticks
        chart.range_axis="#"

      end
    end
    file("#{kpi_name}.pdf")
    save
  end
end
