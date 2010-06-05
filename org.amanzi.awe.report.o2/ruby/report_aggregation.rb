require 'ruby/report.rb'
require 'neo4j'
require 'java'

include_class 'org.amanzi.awe.catalog.neo.GeoNeo'

module Amanzi
  module Report
    module Distribution
      def run(map,drive,region,aggregation)
        property=aggregation[:name]
        report "Distribution analysis for '#{property}'" do |r|
          r.file "report_#{region}_#{property}_#{Time.now.usec}.pdf"
          r.map "Drive map", :map=>map, :width=>600, :height=>400 do |m|
            layer=m.layers.find(:type=>'drive').first
            layer.aggregation=aggregation
            layer.geo_filter=region
          end
          r.chart property do |chart|
            chart.domain_axis='Value'
            chart.range_axis='Count'
            chart.statistics=drive
            chart.property=property
            chart.distribute=aggregation[:distribute]
            chart.select=aggregation[:select]
          end
        end
      end
    end
  end
end

module Amanzi
  module Report
    module Filters
      def run1(map,drive,region,filter,style)
        report "#{filter.property}" do |r|
          r.file "filter_#{region}_#{filter.property}#{Time.now.usec}.pdf"
          r.map "Drive map", :map=>map, :width=>600, :height=>400 do |m|
            layer=m.layers.find(:type=>'drive').first
            layer.filter=filter
            layer.geo_filter=region
            layer.style=style
          end
        end
      end
    end
  end
end

include Amanzi::Report::Distribution
include Amanzi::Report::Filters

def automation
  puts "automation"
  map=GIS.maps.first.copy
  layer=map.layers.find(:type=>'drive').first
  if !layer.nil?
    resource = layer.findGeoResource(GeoNeo.java_class)
    if !resource.nil?
      geoNeo=resource.resolve(GeoNeo.java_class,nil)
      gisNode=geoNeo.getMainGisNode
      style=styles "default" do |ss|
        cs=color_scheme do
          add 255,0,204
          add 102,102,102
          add 51,51,255
          add 0,204,204
          add 255,153,153
          add 255,0,0
          add 153,255,204
          add 0,102,153
          add 255,255,0
          add 153,0,255
          add 102,102,0
          add 0,204,0
          add 255,153,0
          add 153,153,255
        end
        add :circle, cs
      end
      if !gisNode.nil?
        Neo4j::Transaction.run {
          report_props=Hash.new
          properties_node=gisNode.rel(:NEXT).end_node.outgoing(:PROPERTIES).depth(1).first
          traverser=properties_node.outgoing(:CHILD).depth(1).filter do
            get_property("name")=="integer"
          end
          traverser.each do |node|
            puts "node #{node}"
            node.rels.each do |rel|
              property=rel[:property]
              max=rel['max value']
              min=rel["min value"]
              count=rel[:count]
              if min!=max
                if ((max-min)>=10)
                  puts "property=#{property} min=#{min} max=#{max} count=#{count}"
                  puts "#{property} is divided into 10 parts"
                  delta=(max-min)/10
                  values=[]
                  for i in (0..10)
                    if i==10
                      val=max
                    else
                      val=min+delta*i
                    end
                    values<<val.round
                  end
                  report_props[property]=values
                else
                  puts "property=#{property} min=#{min} max=#{max} count=#{count}"
                  puts "#{property} passed to report as is"
                  #traverse
                end
              else
                #  puts "Report will not be generated for #{property}"
              end
            end
          end
          report_props.each_pair do |prop,vals|
            puts prop
            group_filter=filters prop do
              for i in 1..vals.length-1
                puts "#{vals[i-1]} - #{vals[i]}"
                f1=filter :condition=>:ge, :value=>vals[i-1]
                f2=filter :condition=>:le, :value=>vals[i]
                add f1 & f2
              end
            end
            #automatic generation of reports based on filters
            #        run1(map,gisNode[:name],nil,group_filter,style).save
          end

          map=GIS.maps.first.copy
          puts map
          #automatic generation of distribution analysis reports
          gisNode.rel(:NEXT).end_node.outgoing(:AGGREGATION).depth(1).each do |aggr|
            run(map,gisNode[:name],nil,aggr).save
          end
        }
      end
    end
  end
end