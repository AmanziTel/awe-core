require 'neo4j'
require 'java'
require 'ruby/gis'

include_class 'org.amanzi.awe.catalog.neo.GeoNeo'

module Amanzi
  module Report
    module Distribution
      def run(map,drive,aggregation, suffix=nil)
        property=aggregation[:name]
        report "Distribution analysis of '#{drive}' #{property}" do |r|
          r.file "#{property}_#{drive}#{suffix}.pdf"
          r.map "Drive map", :map=>map, :width=>600, :height=>400 do |m|
            layer=m.layers.find(:type=>'drive',:name=>drive).first
            layer.aggregation=aggregation
          end
          r.chart property do |chart|
            chart.domain_axis='Value'
            chart.range_axis='Count'
            chart.statistics=drive.to_s
            chart.property=property
            chart.distribute=aggregation[:distribute]
            chart.select=aggregation[:select]
          end
        end
      end
    end
  end
end

include Amanzi::Report::Distribution

def automation
  begin
    puts "automation"
    map=GIS.maps.first.copy
    gis_nodes=[]
    #find all drive layers
    map.layers.each do |layer|
      puts layer.getID
      resource = layer.findGeoResource(GeoNeo.java_class)
      if !resource.nil?
        geoNeo=resource.resolve(GeoNeo.java_class,nil)
        gisNode=geoNeo.getMainGisNode
        if gisNode[:gis_type]=='drive'
          gis_nodes<<gisNode
        end
      end
    end
    #generate reports for all found drive layers
    gis_nodes.each do |gisNode|
      map.layers.each do |layer|
        resource = layer.findGeoResource(GeoNeo.java_class)
        if !resource.nil?
          geoNeo=resource.resolve(GeoNeo.java_class,nil)
          #          GIS::setLayerVisibility(layer,geoNeo.getMainGisNode[:name]==gisNode[:name]) if geoNeo.getMainGisNode[:gis_type]=='drive'
          gisNode.rel(:NEXT).end_node.outgoing(:AGGREGATION).depth(1).each do |aggr|
            run(map,gisNode[:name],aggr).save
            drive_props=geoNeo.getProperties(GeoNeo::DRIVE_INQUIRER)
            puts drive_props
            if !drive_props.nil?
              geoNeo.setProperty(GeoNeo::DRIVE_INQUIRER,nil)
              run(map,gisNode[:name],aggr,"_no_lines").save
              geoNeo.setProperty(GeoNeo::DRIVE_INQUIRER,drive_props)

            end
          end
        end
      end
    end
  rescue =>e
    puts e
    puts e.backtrace.join("\n")
  end
end