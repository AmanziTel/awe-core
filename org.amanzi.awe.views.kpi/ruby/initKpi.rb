require 'neo4j'
require 'formulas'
require 'default'
require 'search'
include Java

include_class 'org.amanzi.neo.core.service.NeoServiceProvider'
include_class 'org.amanzi.awe.views.kpi.KPIPlugin'

splash_plugin = Java::org.eclipse.core.runtime.Platform.getBundle("org.amanzi.splash").getEntry("/")
splash_plugin_path = Java::org.eclipse.core.runtime.FileLocator.resolve(splash_plugin).getFile
$LOAD_PATH <<splash_plugin_path
puts "$LOAD_PATH: #{$LOAD_PATH}"
#require splash_plugin_path + 'ruby/jruby.rb'
  
neo_service = NeoServiceProvider.getProvider.getService
database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation


Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)

include KPI

def allFormula
  KPI.instance_methods.sort
end

def init
  networkid=KPIPlugin.getDefault.getNetworkId
  $network_root_node=if networkid==nil then nil else Neo4j.load_node(networkid) end

  driveId=KPIPlugin.getDefault.getDriveId
  $drive_root_node=if driveId==nil then nil else Neo4j.load_node(driveId) end

  if !$drive_root_node.nil?
    name=$drive_root_node["name"]
    $event_property=name=~/\.asc|\.FMT/?"event_type":"event_id"
  end
  
  counterId=KPIPlugin.getDefault.getCounterId
  $counter_root_node=if counterId==nil then nil else Neo4j.load_node(counterId) end
end

def find_nested_modules(mod)
  mod.constants.select  do |c|
      (mod.const_get c.to_sym).class == Module
    end
end

def find_all_nested_modules(modules_found,mod)
  modules=mod.constants.select {|c| (mod.const_get c.to_sym).class == Module}
  modules.each do |m|
    modules_found<<nested_module=mod.const_get(m.to_sym)
    find_all_nested_modules(modules_found,nested_module)
  end
end

def find_kpis(type=nil)
  modules_found=[] 
  methods=[]
  find_all_nested_modules(modules_found,KPI)
  modules_found.each do |mod|
    puts mod.name
    methods_found=mod.singleton_methods.sort
    puts methods_found
    methods.concat(methods_found.collect! {|x| mod.name+"."+x}) if !methods_found.empty?
  end
  methods
end
