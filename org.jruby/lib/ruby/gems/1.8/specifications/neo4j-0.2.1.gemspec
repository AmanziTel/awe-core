# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{neo4j}
  s.version = "0.2.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Andreas Ronge"]
  s.date = %q{2009-03-15}
  s.description = %q{A graph database for JRuby}
  s.email = %q{andreas.ronge@gmail.com}
  s.extra_rdoc_files = ["README.rdoc"]
  s.files = ["LICENSE", "CHANGELOG", "README.rdoc", "Rakefile", "neo4j.gemspec", "lib/neo4j.rb", "lib/lucene", "lib/lucene/index_info.rb", "lib/lucene/jars", "lib/lucene/jars/lucene-core-2.4.0.jar", "lib/lucene/field_info.rb", "lib/lucene/config.rb", "lib/lucene/jars.rb", "lib/lucene/index_searcher.rb", "lib/lucene/index.rb", "lib/lucene/hits.rb", "lib/lucene/query_dsl.rb", "lib/lucene/document.rb", "lib/lucene/transaction.rb", "lib/lucene.rb", "lib/neo4j", "lib/neo4j/reference_node.rb", "lib/neo4j/mixins", "lib/neo4j/mixins/transactional.rb", "lib/neo4j/mixins/node.rb", "lib/neo4j/mixins/relation.rb", "lib/neo4j/mixins/dynamic_accessor.rb", "lib/neo4j/version.rb", "lib/neo4j/jars", "lib/neo4j/jars/jta-spec1_0_1.jar", "lib/neo4j/jars/neo-1.0-b7.jar", "lib/neo4j/jars/shell-1.0-b7.jar", "lib/neo4j/config.rb", "lib/neo4j/jars.rb", "lib/neo4j/indexer.rb", "lib/neo4j/search_result.rb", "lib/neo4j/neo.rb", "lib/neo4j/transaction.rb", "lib/neo4j/relations", "lib/neo4j/relations/relations.rb", "lib/neo4j/relations/relation_info.rb", "lib/neo4j/relations/has_n.rb", "lib/neo4j/relations/node_traverser.rb", "lib/neo4j/relations/dynamic_relation.rb", "lib/neo4j/relations/traversal_position.rb", "lib/neo4j/relations/relation_traverser.rb", "test/lucene", "test/lucene/sort_spec.rb", "test/lucene/transaction_spec.rb", "test/lucene/index_info_spec.rb", "test/lucene/spec_helper.rb", "test/lucene/query_dsl_spec.rb", "test/lucene/document_spec.rb", "test/lucene/field_info_spec.rb", "test/lucene/index_spec.rb", "test/neo4j", "test/neo4j/relation_traverser_spec.rb", "test/neo4j/neo_spec.rb", "test/neo4j/transaction_spec.rb", "test/neo4j/spec_helper.rb", "test/neo4j/node_traverser_spec.rb", "test/neo4j/has_one_spec.rb", "test/neo4j/indexer_spec.rb", "test/neo4j/node_lucene_spec.rb", "test/neo4j/order_spec.rb", "test/neo4j/index_spec.rb", "test/neo4j/value_object_spec.rb", "test/neo4j/property_spec.rb", "test/neo4j/ref_node_spec.rb", "test/neo4j/person_spec.rb", "test/neo4j/has_n_spec.rb", "test/neo4j/node_mixin_spec.rb", "examples/imdb", "examples/imdb/install.sh", "examples/imdb/model.rb", "examples/imdb/find_actors.rb", "examples/imdb/create_neo_db.rb", "examples/imdb/db", "examples/imdb/db/neo", "examples/imdb/db/neo/neostore.propertystore.db.strings", "examples/imdb/db/neo/neostore.propertystore.db.index.id", "examples/imdb/db/neo/neostore.propertystore.db.index.keys.id", "examples/imdb/db/neo/neostore.relationshiptypestore.db", "examples/imdb/db/neo/neostore.propertystore.db.index.keys", "examples/imdb/db/neo/neostore.id", "examples/imdb/db/neo/neostore.relationshiptypestore.db.id", "examples/imdb/db/neo/neostore.relationshipstore.db", "examples/imdb/db/neo/neostore.propertystore.db.strings.id", "examples/imdb/db/neo/neostore.relationshipstore.db.id", "examples/imdb/db/neo/neostore.propertystore.db.arrays.id", "examples/imdb/db/neo/neostore.nodestore.db.id", "examples/imdb/db/neo/tm_tx_log.1", "examples/imdb/db/neo/neostore.propertystore.db.arrays", "examples/imdb/db/neo/neostore.relationshiptypestore.db.names.id", "examples/imdb/db/neo/neostore.relationshiptypestore.db.names", "examples/imdb/db/neo/neostore.propertystore.db.index", "examples/imdb/db/neo/neostore.propertystore.db", "examples/imdb/db/neo/neostore.propertystore.db.id", "examples/imdb/db/neo/active_tx_log", "examples/imdb/db/neo/neostore.nodestore.db", "examples/imdb/db/neo/neostore", "examples/imdb/data", "examples/imdb/data/test-movies.list", "examples/imdb/data/test-actors.list"]
  s.has_rdoc = true
  s.homepage = %q{http://github.com/andreasronge/neo4j/tree}
  s.rdoc_options = ["--quiet", "--title", "Neo4j.rb", "--opname", "index.html", "--line-numbers", "--main", "README.rdoc", "--inline-source"]
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.4")
  s.rubyforge_project = %q{neo4j}
  s.rubygems_version = %q{1.3.1}
  s.summary = %q{A graph database for JRuby}

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 2

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
    else
    end
  else
  end
end
