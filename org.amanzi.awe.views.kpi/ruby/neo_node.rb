module Neo4j::JavaNodeMixin
  def method_missing(meth_name,*args)
#    puts "Neo4j::JavaNodeMixin.method_missing(#{meth_name},*args)"
   has_property(meth_name.to_s)? get_property(meth_name.to_s):0.0
  end
  
  def name
    key_rel=self.rel(:KEY)
    !key_rel.nil? ? key_rel.end_node.get_property("name") : nil
  end
  
end