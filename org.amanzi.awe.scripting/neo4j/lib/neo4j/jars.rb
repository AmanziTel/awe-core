include Java

module Neo4j
  #Lagutko, 20.09.2009, sine Neo4j RubyGem will uses in plugins it should not have built-in Neo4j JARS,
  #but should use Neo4j JARs from plugin
  
  neo4j_url = Java::org.eclipse.core.runtime.Platform.getBundle('org.neo4j').getEntry('/')
  neo4j_dir = Java::org.eclipse.core.runtime.FileLocator.resolve(neo4j_url).getFile
  
  include_package neo4j_dir
  
  #require 'neo4j/jars/neo-1.0-b10.jar'
  #require 'neo4j/jars/shell-1.0-b10.jar'
  #require 'neo4j/jars/jta-1_1.jar'
end
