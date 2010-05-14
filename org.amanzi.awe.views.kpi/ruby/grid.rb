require 'formulas'
require 'search'
require 'neo4j'
require 'parsedate'

module KPI
  module Default
    module Grid
      module Performance
        def RFLayer.dispatch_blocking_queue_rate(counters,aggregation=:none)
          puts "dispatch_blocking_queue_rate method called"
          aggr=counters.collect("time","date","site_name","cell_name",
          "dis_tch_queued",
          "dis_tch_requests"
          ).aggregate("site_name")
          result=[]
          result<<['site_name','cell_name','time','KPI']
          sites=Hash.new
          aggr.each do |obj,rows|
            rows.each do |row|
              site=obj
              cell=row['cell_name']
              date=row['date']
              time=row['time']
              kpi=row['dis_tch_queued'].quo(row['dis_tch_requests'])
              kpi_rounded=(kpi*10000).round/10000.to_f
              aggregate_sites(sites,site,cell,date,time,kpi_rounded)
            end
          end
          calculate_average(sites, aggregation)
        end

        def RFLayer.average_dispatch_call_delay(counters,aggregation=:none)
          aggr=counters.collect("time","date","site_name","cell_name",
          "dis_queued_time_total",
          "dis_tch_requests"
          ).aggregate("site_name")
          result=[]
          result<<['site_name','cell_name','time','KPI']
          sites=Hash.new
          aggr.each do |obj,rows|
            rows.each do |row|
              site=obj
              cell=row['cell_name']
              date=row['date']
              time=row['time']
              kpi=row['dis_queued_time_total'].quo(row['dis_tch_requests'])
              kpi_rounded=(kpi*10000).round/10000.to_f
              aggregate_sites(sites,site,cell,date,time,kpi_rounded)
            end
          end
          calculate_average(sites, aggregation)
        end

        def RFLayer.dispatch_queue_hold_time(counters,aggregation=:none)
          aggr=counters.collect("time","date","site_name","cell_name",
          "dis_queued_time",
          "dis_queued_time_total"
          ).aggregate("site_name")
          result=[]
          result<<['site_name','cell_name','time','KPI']
          sites=Hash.new
          aggr.each do |obj,rows|
            rows.each do |row|
              site=obj
              cell=row['cell_name']
              date=row['date']
              time=row['time']
              kpi=row['dis_queued_time_total'].quo(row['dis_queued_time'])
              kpi_rounded=(kpi*10000).round/10000.to_f
              aggregate_sites(sites,site,cell,date,time,kpi_rounded)
            end
          end
          calculate_average(sites, aggregation)
        end
      end

    end
  end
end