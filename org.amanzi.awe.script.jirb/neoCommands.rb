include_class org.amanzi.neo.core.INeoConstants
include_class org.amanzi.neo.core.enums.NetworkElementTypes

module AWE
  module ExtendCommandBundle
    
    #
    # Returns a GIS node by it's name
    #
    def gis(name)
      gis_type = INeoConstants::GIS_TYPE_NAME
      
      Neo4j::Transaction.run{Neo4j::ref_node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{self[:type] == gis_type && self[:name] == name}.first}      
    end
    
    #
    # Returns all GIS nodes
    #
    def all_gis
      result = []
      
      gis_type = INeoConstants::GIS_TYPE_NAME
      Neo4j::Transaction.run{Neo4j::ref_node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{self[:type] == gis_type}.each {|gis| result << gis}}
      
      result
    end
    
    #
    # Returns filtered GIS nodes
    #
    def gis_by_filter(filter) 
      result = []
      
      Neo4j::Transaction.run{Neo4j::ref_node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter(filter).each {|gis| result << gis}}
      
      result
    end
    
    #
    # Returns Network node by it's name
    #
    def network(name, start_node = nil)
      network_type = NetworkElementTypes::NETWORK
      
      network_element(network_type, name, start_node)      
    end
    
    #
    # Returns all Network Nodes
    #
    def all_networks(start_node = nil)
      network_type = NetworkElementTypes::NETWORK
      
      all_network_elements(network_type, name, start_node)
    end
    
    #
    # Returns filtered Network nodes
    #
    def networks_by_filter(filter, start_node = nil) 
      network_type = NetworkElementTypes::NETWORK
      
      network_elements_by_filter(network_type, filter, start_node)
    end
    
    #
    # Returns City node by it's name
    #
    def city(name, start_node = nil)
      city_type = NetworkElementTypes::CITY
      
      network_element(city_type, name, start_node)      
    end
    
    #
    # Returns all City Nodes
    #
    def all_cities(start_node = nil)
      city_type = NetworkElementTypes::CITY
      
      all_network_elements(city_type, name, start_node)
    end
    
    #
    # Returns filtered City nodes
    #
    def cities_by_filter(filter, start_node = nil) 
      city_type = NetworkElementTypes::CITY
      
      network_elements_by_filter(city_type, filter, start_node)
    end
    
    #
    # Returns BSC node by it's name
    #
    def bsc(name, start_node = nil)
      bsc_type = NetworkElementTypes::BSC
      
      network_element(bsc_type, name, start_node)
    end
    
    #
    # Returns all BSC nodes
    #
    def all_bscs(start_node = nil)
      bsc_type = NetworkElementTypes::BSC
      
      all_network_elements(bsc_type, start_node)
    end
    
    #
    # Returns filtered BSC nodes
    #
    def bsc_by_filter(filter, start_node = nil)
      bsc_type = NetworkElementTypes::BSC
      
      network_elements_by_filter(bsc_type, filter, start_node)
    end
    
    #
    # Returns Site node by it's name
    #
    def site(name, start_node = nil)
      site_type = NetworkElementTypes::SITE
      
      network_element(site_type, name, start_node)
    end
    
    #
    # Returns all Sites
    #
    def all_sites(start_node = nil)
      site_type = NetworkElementTypes::SITE
      
      all_network_elements(site_type, start_node)
    end
    
    #
    # Returns filtered Site nodes 
    #
    def sites_by_filter(filter, start_node = nil)
      site_type = NetworkElementTypes::SITE
      
      nework_elements_by_filter(site_type, filter, start_node)
    end
    
    #
    # Returns Sector node by it's name
    # 
    def sector(name, start_node = nil)
      sector_type = NetworkElementTypes::SECTOR
      
      network_element(sector_type, name, start_node)
    end
    
    #
    # Returns all Sector nodes
    #
    def all_sectors(start_node = nil) 
      sector_type = NetworkElementTypes::SECTOR
      
      all_network_elements(sector_type, start_node)      
    end
    
    # 
    # Returns filtered Sector nodes
    #
    def sectors_by_filter(filter, start_node = nil) 
      sector_type = NetworkElementTypes::SECTOR
      
      network_elements_by_filter(sector_type, filter, start_node)
    end
    
    #
    # Utility function that checks is this type matches to given NetworkElementType
    #
    def network_element?(element_type, type_name)
      if element_type.matches(type_name)
        true
      else
        false
      end
    end
    
    private
    
    #
    # Returns NetworkElement node by given name and NetworkElementType
    #
    def network_element(name, element_type, start_node) 
     if start_node.nil?        
        node = Neo4j::ref_node
      else
        node = start_node
      end
      
      parent = self
      
      traverser = node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{parent.network_element?(element_type, self[:type]) && self[:name] == name}
      
      if (start_node.nil?)
        traverser.outgoing(:NEXT)
      end
      
      Neo4j::Transaction.run{traverser.first}  
    end
    
    #
    # Returns all NetworkElement nodes by given NetworkElementType
    #
    def all_network_elements(element_type, start_node) 
      result = []
      
      if start_node.nil?        
        node = Neo4j::ref_node
      else
        node = start_node
      end
      
      parent = self
      
      traverser = node.traverse.depth(:all).outgoing(:CHILD).filter{parent.network_element?(element_type, self[:type])}
      
      if (start_node.nil?)
        traverser.outgoing(:NEXT)
      end
      
      Neo4j::Transaction.run{traverser.each {|element| result << element}}
      
      result
    end
    
    #
    # Returns filtered NetworkElement nodes by given NetworkelementType
    #
    def network_elements_by_filter(element_type, filter, start_node)
      result = []
      
      if start_node.nil?        
        node = Neo4j::ref_node
      else
        node = start_node
      end
      
      parent = self
      
      traverser = node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{parent.network_element?(element_type, self[:type])}.filter{filter}
      
      if (start_node.nil?)
        traverser.outgoing(:NEXT)
      end
      
      Neo4j::Transaction.run{traverser.each {|element| result << element}}
      
      result
    end
    
  end
end

true