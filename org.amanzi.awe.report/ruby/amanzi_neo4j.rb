module Neo4j
  module NodeMixin
    def method_missing(method_id, *args)
      puts "NodeMixin.method_missing(#{method_id}): #{get_property(method_id)}"
      if method_id=='node_type'
        get_property(:type)
      else
        get_property(method_id)
      end
    end
    def type
      get_property(:type)
    end
  end

  module Relationships
    class NodeTraverser
      def stop_on(&proc)
        @stop_evaluator = StopEvaluator.new proc
        self
      end
    end

    class StopEvaluator
      include org.neo4j.graphdb.StopEvaluator
      
      def initialize(proc, raw = false)
        @proc = proc
        @raw = raw
      end

      def isStopNode( traversal_position )
        # if the Proc takes one argument that we give it the traversal_position
        result = if @proc.arity == 1
          # wrap the traversal_position in the Neo4j.rb TraversalPostion object
          @proc.call TraversalPosition.new(traversal_position)
        else # otherwise we eval the proc in the context of the current node
          # do not stop on the start node
          return false if traversal_position.isStartNode()
          eval_context = Neo4j::load_node(traversal_position.currentNode.getId, @raw)
          eval_context.instance_eval(&@proc)
        end

        # java does not treat nil as false so we need to do instead
        (result)? true : false
      end
    end
  end
end
