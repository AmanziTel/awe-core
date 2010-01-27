require 'neo4j'
include Java

include_class 'org.amanzi.neo.core.service.NeoServiceProvider'
include_class 'org.amanzi.awe.views.kpi.KPIPlugin'

neo_service = NeoServiceProvider.getProvider.getService
database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation


Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)
module KpiBuilder
def sum(property_set)
  property_set.sum
end
def count(property_set)
  property_set.count
end
end
def allFormula
  KpiBuilder.instance_methods
end
include KpiBuilder
def init
  networkid=KPIPlugin.getDefault.getNetworkId
  $network_root_node=if networkid==nil then nil else Neo4j.load_node(networkid) end
puts $network_root_node
driveId=KPIPlugin.getDefault.getDriveId
$drive_root_node=if driveId==nil then nil else Neo4j.load_node(driveId) end	

puts $drive_root_node
end

def sites(options={})
NodeSet.new filter($network_root_node,'site', options)
end
def sectors(options={})
NodeSet.new filter($network_root_node,'sector', options)
end
def messages(options={})
NodeSet.new filter($drive_root_node,'ms', options)
end
def events(options=nil)
options ||= {}
unless options.is_a Hash
options = {:event => options.to_s}
end
NodeSet.new filter($drive_root_node,'ms', {:event => true}.merge(options))
end
def filter(root_node,type_name,options={})
options.merge! 'type' => type_name
root_node.outgoing(:CHILD, :NEXT).depth(:all).filter do
node_properties = props # defined in Neo4j::NodeMixin
options.keys.find{|key| options[key]==true ? node_properties[key] : node_properties[key] === options[key]}
end
end


class NodeSet
def initialize(traverser)
@traverser=traverser
end
def each
  puts "each nodeset"
  
@traverser.each{|node|
      yield node
}
end
def count
puts "count"
num=0
@traverser.each{|n| num+=1}
num
end
def method_missing(method_id,*args)
puts method_id
PropertySet.new(self,method_id)
end
end


class PropertySet
def initialize(node_set,property)
@node_set=node_set
@property=property.to_s
end
def each
@node_set.each do |node|
  yield node.props[@property]
end
end
def count
num=0
@node_set.each{|n|
  num += 1 if(!n.props[@property].nil?)
}
num
end
def sum
num=0.0
@node_set.each{|n|
  puts n.props[@property]
  if !n.props[@property].nil?
    num+= n.props[@property]
  end
}
num
end 
end