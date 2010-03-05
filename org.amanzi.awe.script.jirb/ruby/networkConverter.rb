require 'Neo4j'

include Java
include_class org.amanzi.neo.core.enums.NodeTypes
include_class org.amanzi.neo.core.utils.NeoUtils
include_class org.amanzi.neo.core.enums.GisTypes

include_class org.amanzi.neo.loader.NetworkLoader

module Network
  
  class Converter < NetworkLoader
    
    include AWE::ExtendCommandBundle
    
    def initialize(options = {}, network_name = nil)
      project_name = options[:project]
      map_name = options[:map]
      layer_name = options[:layer]
        
      #get a project
      if project_name.nil?
        project 0
        project_index = 0
      else
        projects.size.times do |i|
          project_index = 0
          break if project(i).name == project_name
        end 
      end
      
      #get a map
      if map_name.nil?
        map project_index, 0
      else
        maps.size.times do |i|
          break if map(project_index, i).name == map_name
        end
      end
      
      #get a layer
      if layer_name.nil?
        layer 0
      else
        layers.size.times do |i|
          break if layer(i).name = layer_name
        end
      end
      
      @layer = layer
      
      #get a name for new network
      if network_name.nil?
        network_name = @layer.name
      end
      
      super network_name, network_name, nil
    end
    
    def run(monitor)
    end
    
    def convert
      Neo4j::Transaction.run do
      
        return if !setup
        initializeIndexes
        
        have_headers = false
        
        #iterate through all features
        features.each do |single_feature|
          line = ''
          header = ''          
          
          #iterate throug all feature attributes
          single_feature.feature_type.attribute_types.each do |single_attribute|
            #get attribute name
            name = single_attribute.name            
            if !have_headers   
              #if no headers initialized than add name of attribute to header           
              header = header << name << "\t"
            end
            
            #otherwise create a line of attribute values
            line = line << single_feature.get_attribute(name.to_s).to_s << "\t"
          end
          
          
          if !have_headers
            #if no headers than initialize them
            #Fake: add beamwidth to parsed headers
            parseHeader header << "beamwidth"
            have_headers = true
          end
          
          #parse line with parameters
          #Fake: set default beamwidth
          parseLine line << "60.0"          
        end
        
        commit true
        saveProperties
        finishUpIndexes
        finishUp
      end
      
      print_stats false
      Converter.addDataToCatalog
      addLayersToMap
    end
    
  end
  
end

module AWE
  
  module ExtendCommandBundle
  
    def convert_network(options = {}, network_name = nil)
      converter = Network::Converter.new options, network_name
      converter.convert
    end
  end
  
end

true