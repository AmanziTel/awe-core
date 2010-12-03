include_class org.amanzi.neo.services.INeoConstants
include_class org.amanzi.neo.services.enums.GisTypes

module AWE
  module ExtendCommandBundle
    
    #
    # Returns a GIS node by it's name
    #
    def gis(name, type = nil)
      gis_type = INeoConstants::GIS_TYPE_NAME
      
      transaction = Neo4j::ref_node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{self[:type] == gis_type && self[:name] == name}
      
      update_transaction(transaction, type) unless type.nil?
      
      Neo4j::Transaction.run{transaction.first}      
    end
    
    #
    # Returns all GIS nodes
    #
    def all_gis(type = nil)
      gis_type = INeoConstants::GIS_TYPE_NAME
      
      transaction = Neo4j::ref_node.traverse.depth(:all).outgoing(:CHILD, :NEXT).filter{self[:type] == gis_type}
      
      update_transaction(transaction, type) unless type.nil?
      
      Neo4j::Transaction.run{transaction.to_a}
    end
    
    #
    # Returns TEMS dataset node by it's name
    #
    def dataset(name, start_node = nil) 
      node_type = INeoConstants::DATASET_TYPE_NAME    
      
      tems_element(name, node_type, start_node)
    end
    
    # 
    # Returns all TEMS Dataset nodes
    #
    def all_datasets(start_node = nil) 
      node_type = INeoConstants::DATASET_TYPE_NAME
      
      all_tems_elements(node_type, start_node)
    end
    
    #
    # Returns TEMS file node by it's name
    #
    def tems_file(name, start_node = nil) 
      node_type = INeoConstants::FILE_TYPE_NAME
      
      tems_element(node_type, start_node)
    end
    
    #
    # Returns all TEMS file nodes
    #
    def all_tems_files(start_node = nil)
      node_type = INeoConstants::FILE_TYPE_NAME
      
      all_tems_elements(node_type, start_node)
    end
    
    #
    # Returns all TEMS mp nodes
    #
    def all_tems_mp(start_node = nil)
      node_type = INeoConstants::MP_TYPE_NAME
      
      all_tems_elements(node_type, start_node)
    end
    
    #
    # Returns all TEMS ms nodes
    #
    def all_tems_ms(start_node = nil)
      node_type = INeoConstants::HEADER_MS
      
      all_tems_elements(node_type, start_node)
    end
    
    #
    # Returns Network node by it's name
    #
    def network(name, start_node = nil)
      network_type = NetworkElementTypes::NETWORK
      
      network_element(network_type, start_node)      
    end
    
    #
    # Returns all Network Nodes
    #
    def all_networks(start_node = nil)
      network_type = NetworkElementTypes::NETWORK
      
      all_network_elements(network_type, start_node)
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
      
      all_network_elements(city_type, start_node)
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
    def all_bsc(start_node = nil)
      bsc_type = NetworkElementTypes::BSC
      
      all_network_elements(bsc_type, start_node)
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
      node = if start_node.nil? then Neo4j::ref_node else start_node end
      
      parent = self
      
      traverser = node.traverse.depth(:all).outgoing(:CHILD).filter{parent.network_element?(element_type, self[:type]) && self[:name] == name}
      traverser.outgoing(:NEXT) if start_node.nil?
      
      Neo4j::Transaction.run{traverser.first}  
    end
    
    #
    # Returns all NetworkElement nodes by given NetworkElementType
    #
    def all_network_elements(element_type, start_node) 
      node = if start_node.nil? then Neo4j::ref_node else start_node end
      
      parent = self
      
      traverser = node.traverse.depth(:all).outgoing(:CHILD).filter{parent.network_element?(element_type, self[:type])}
      traverser.outgoing(:NEXT) if start_node.nil?
      
      Neo4j::Transaction.run{traverser.to_a}
    end
    
    #
    # Returns TEMS node by it's type and name
    #
    def tems_element(name, element_type, start_node)
      node = if start_node.nil? then Neo4j::ref_node else start_node end
      
      traverser = node.traverse.depth(:all).filter{self[:type] == element_type && self[:name] == name}
      update_relationship(traverser, start_node)
      
      Neo4j::Transaction.run{traverser.first}
    end
    
    #
    # Returns all TEMS element by given type
    #
    def all_tems_elements(element_type, start_node)
      node = if start_node.nil? then Neo4j::ref_node else start_node end
      
      traverser = node.traverse.depth(:all).filter{self[:type] == element_type}
      update_relationship(traverser, start_node)
      
      Neo4j::Transaction.run{traverser.to_a}
    end
    
    #
    # Updates Traverser for searching TEMS nodes with relationship and directions
    #
    def update_relationship(traverser, start_node)
      if (start_node.nil?)
        traverser.outgoing(:NEXT, :CHILD)
      else
        case start_node[:type]
          when INeoConstants::MP_TYPE_NAME
            traverser.outgoing(:CHILD)
          when INeoConstants::AWE_PROJECT_NODE_TYPE
            traverser.outgoing(:CHILD)
            traverser.outgoing(:NEXT)
          else
            traverser.outgoing(:NEXT) 
        end 
      end
    end
    
    #
    # Updates Traverser for searching GIS nodes with GisType
    #
    def update_transaction(transaction, type)
      case type
        when :network
          transaction.filter{self[:gis_type] == GisTypes::Network.header}
        when :tems          
          transaction.filter{self[:gis_type] == GisTypes::Tems.header}
      end
    end
    
  end
end

true