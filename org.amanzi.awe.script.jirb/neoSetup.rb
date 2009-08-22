require 'Neo4j'
include Java

include_class 'org.amanzi.neo.core.service.NeoServiceProvider'

database_location = NeoServiceProvider.getProvider.getDefaultDatabaseLocation
neo_service = NeoServiceProvider.getProvider.getService

Neo4j::Config[:storage_path] = database_location
Neo4j::start(neo_service)