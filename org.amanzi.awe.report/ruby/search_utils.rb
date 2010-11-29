require 'neo4j'

include_class 'net.refractions.udig.project.internal.ProjectPlugin'

class SearchQuery
  attr_accessor :root
  def initialize(properties)
    @properties=properties

    #    if properties.is_a? String
    #      @properties=[properties]
    #    elsif properties.is_a? Array
    #      @properties=properties
    #    elsif properties.is_a? Hash
    #    end
  end

  def root(root)
    @root=root
  end

  def traverse(*relations)
    @relations=*relations
  end

  def depth(depth)
    @depth=depth
  end

  def where(&block)
    @filter=block
  end

  def stop_on(&block)
    @stop_on=block
  end
  def get_node_property(node,prop)
    (node.property? prop) ? node.get_property(prop): node.send(prop.to_sym)
  end
  def each
    configure_traverser
    @traverser.each do |node|
#      puts "#{node[:type]} ->#{node[:name]} ->#{node.props}"
      result=Hash.new
#      puts @properties.class
      if @properties.is_a? Hash
        key_property=@properties[:key]
        value_property=@properties[:value]
        result[node[key_property]]=get_node_property(node,value_property)#node[value_property]
      else
        @properties.each do |prop|
          result[prop]=(node.property? prop) ? node.get_property(prop): nil
        end
      end
      if @child
        @child.root=node
        @child.each do |child|
          result_with_child=result.merge(child)
          yield result_with_child
        end
      else
        yield result
      end
    end
  end

  def configure_traverser
    @depth=@depth||1
    @relations=@relations||[:CHILD,:NEXT]
    @traverser=@root.outgoing(*@relations).depth(@depth)
    @traverser.filter &@filter if @filter
    @traverser.stop_on &@stop_on if @stop_on
  end

  def select_properties(properties,&block)
#    puts self
#    puts "Search.select(#{properties},&block)"
    search=SearchQuery.new(properties)
    search.setup &block
    @child=search
  end

  def setup(&block)
    self.instance_eval &block
  end

  def  from(subtree_name=nil,&block)
    self.instance_eval &block
  end

  #  def method_missing(meth_name,*args)
  #    puts "method_missing: #{meth_name}, #{args}"
  #    #create an instance variable with the given name
  #    var_name="@"+meth_name.to_s
  #    if !(var=instance_variable_get(var_name))
  #      instance_variable_set(var_name,Search.new(self,meth_name.to_s))
  #      var=instance_variable_get(var_name)
  #    end
  #    var
  #  end
end

module NodeUtils
  def dataset(name,type="dataset",project=nil)
    datasets=project_node.outgoing(:CHILD,:VIRTUAL_DATASET).depth(2).filter do
      get_property("type")==type and get_property("name")==name
    end
    datasets.first
  end

  def current_project
    ProjectPlugin::getPlugin().getProjectRegistry().getCurrentProject()
  end

  def project_node(project=nil)
    project=project||current_project
    project_nodes=Neo4j.ref_node.outgoing(:AWE_PROJECT).filter do
      get_property("name")==project.name
    end
    project_nodes.first
  end

  def find_first(root,properties,*relation)
    nodes=root.outgoing(*relation).depth(:all).filter do
      found=true
      properties.each do |key,value|
        found&&=get_property(key.to_s)==value
      end
      found
    end
    nodes.first
  end

  def select_properties(properties,&block)
    search=SearchQuery.new(properties)
    search.setup &block
    search
  end
end