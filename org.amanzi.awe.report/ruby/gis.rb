include Java

puts "include GIS"
include_class org.amanzi.awe.report.util.GIS
puts "class included"
class Map
#  include_class net.refractions.udig.project.IMap
  #  def copy
  #    ApplicationGIS::copyMap(self)
  #  end
end

class MapIterator
  def initialize(collection)
    puts "initialize"
    @collection=collection
  end

  def each
    iter=iterator
    while (iter.hasNext)do
      yield iter.next
    end
  end

  def first
    puts "first"
    iter=iterator
    map=iter.hasNext ? iter.next : GIS::NO_MAP
    puts "actMap.getID() #{map.getID()}" if map!=GIS::NO_MAP
  end

  def iterator
    @collection.iterator
  end
end
puts "class GIS"
class GIS
  #  map = GIS.maps.first.copy # make copy for report
  #Returns all open maps
  def self.maps
    puts "maps"
    #    GIS::getOpenMaps()
    MapIterator.new(GIS::getOpenMaps())
  end

end