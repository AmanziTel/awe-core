require 'neo4j'
include Java

include_class 'org.amanzi.neo.db.manager.DatabaseManagerFactory'

database_location = DatabaseManagerFactory.getDatabaseManager().getLocation()
neo_service = DatabaseManagerFactory.getDatabaseManager().getDatabaseService()

Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)

true
