require 'java'

puts "trying to include GIS"

include_class 'org.amanzi.awe.report.util.GIS'
include_class 'net.refractions.udig.project.ui.ApplicationGIS'
include_class 'org.amanzi.awe.neostyle.NeoStyleContent'
include_class 'org.amanzi.awe.filters.experimental.GroupFilter'

#include_class 'org.amanzi.awe.catalog.neo.GeoNeo'

include_class 'org.geotools.feature.DefaultFeature'
include_class 'org.geotools.data.FeatureSource'
include_class 'org.geotools.feature.FeatureCollection'
include_class 'com.vividsolutions.jts.geom.Geometry'

puts "GIS included"
include_class 'net.refractions.udig.project.internal.impl.LayerImpl'
include_class 'net.refractions.udig.project.internal.SimpleBlackboard'

class DefaultFeature
  def geometry
    getDefaultGeometry()
  end
end

class Geometry
  def ==(other)
    self.getGeometryType().upcase==other.to_s.upcase
  end
end

class SimpleBlackboard
  def filter(property=nil,&block)
    f=GroupFilter.new(property)
    f.setup(&block)
    put("FILTER",f)
  end
end

class LayerImpl
  def style=(style)
    neo_style=getStyleBlackboard().get(NeoStyleContent::ID)
    neo_style.clearStyle()
    style.styles.each do |shape,color_scheme|
      neo_style.addStyle(shape,color_scheme.colors.to_java(java.awt.Color))
    end
  end

  def geo_filter=(feature)
    getBlackboard().put("GEO_FILTER",feature)
  end
  
  def group_filter(property=nil,&block)
    puts "LayerImpl.group_filter"
    puts block_given?
    gfilter=GroupFilter.new(property)
    gfilter.setup(&block)
    #    getBlackboard().put("FILTER",[filter].to_java(GroupFilter))
    gfilter
  end

  def filters(&block)
    puts "LayerImpl.filters"
    self.instance_eval &block
    getBlackboard().put("FILTER",@filters.to_java(GroupFilter))
  end

  def add(filter)
    puts "LayerImpl.add"
    if @filters.nil?
      @filters=[filter]
    else
      @filters<<filter
    end
  end

  def filter=(filter)
    puts "LayerImpl.filter="
    getBlackboard().put("FILTER",[filter].to_java(GroupFilter))
  end

  #  def blackboard
  #    getBlackboard()
  #  end
end

include_class 'net.refractions.udig.project.internal.impl.MapImpl'

class MapImpl
  def copy
    ApplicationGIS::copyMap(self)
  end

  def layers
    LayerIterator.new(self.getMapLayers())
  end

  def print_features
    GIS::printFeatures(self)
  end

  def selected_features
    features=[]
    getMapLayers().each do |layer|
      source=layer.getResource(FeatureSource.java_class,nil);
      if !source.nil?
        collection = source.getFeatures(layer.getFilter())
        if !collection.nil?
          featuresIterator = collection.features()
          begin
            while featuresIterator.hasNext()
              features<<featuresIterator.next()
            end
          ensure
            featuresIterator.close()
          end
        end
      end
    end
    features
  end
end

class MapIterator
  def initialize(collection)
    #    puts "initialize"
    @collection=collection
  end

  def each
    iter=iterator
    while (iter.hasNext)do
      yield iter.next
    end
  end

  def first
    iter=iterator
    iter.hasNext ? iter.next : ApplicationGIS::NO_MAP
  end

  def iterator
    @collection.iterator
  end
end

class LayerIterator
  def initialize(collection)
    @collection=collection
  end

  def each
    iter=iterator
    while (iter.hasNext)do
      yield iter.next
    end
  end

  def iterator
    @collection.iterator
  end

  def find(hash,&block)
    type=hash[:type]
    iter=iterator
    layers=[]
    while (iter.hasNext)do
      layer=iter.next
      resource = layer.findGeoResource($geo_neo_class)
      if !resource.nil?
        geo_neo=resource.resolve($geo_neo_class,nil)
        layers<<layer if geo_neo.getGisType().to_s==type.to_s.upcase
      end
    end
    layers
  end
end

class GIS
  #Returns all maps
  def self.maps
    MapIterator.new(GIS::getAllMaps())
  end

end