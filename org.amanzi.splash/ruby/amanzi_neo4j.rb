module Neo4j
  module Relationships
    class NodeTraverser
      def stop_on(&proc)
        @stop_evaluator = StopEvaluator.new proc
        self
      end
    end

    class StopEvaluator
      include org.neo4j.api.core.StopEvaluator
      def initialize(proc)
        @proc = proc
      end

      def isStopNode( traversal_position )
        # if the Proc takes one argument that we give it the traversal_position
        result = if @proc.arity == 1
          # wrap the traversal_position in the Neo4j.rb TraversalPostion object
          @proc.call TraversalPosition.new(traversal_position)
        else # otherwise we eval the proc in the context of the current node
          # do not stop on the start node
          return false if traversal_position.isStartNode()
          eval_context = Neo4j::load(traversal_position.currentNode.getId)
          eval_context.instance_eval(&@proc)
        end

        # java does not treat nil as false so we need to do instead
        (result)? true : false
      end
    end
  end
end
