module Neo4j::JavaNodeMixin
  def method_missing(meth_name,*args)
#    puts "Neo4j::JavaNodeMixin.method_missing(#{meth_name},*args)"
   has_property(meth_name.to_s)? get_property(meth_name.to_s):0.0
  end

end