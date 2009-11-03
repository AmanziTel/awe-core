module Splash
  module Formulas
    module Demo
      
      def max(*args)
        args[0].respond_to?('max') ? args[0].max : args.max
      end

      def min(*args)
        args[0].respond_to?('min') ? args[0].min : args.min
      end

      def base_value(value)
        value.is_a?(String) && '' ||
        value.is_a?(Float) && 0.0 ||
        0
      end

      def sum(*args)
        if args[0].respond_to? 'sum'
          args[0].send 'sum'
        elsif args[0].respond_to? 'inject'
          args[0].inject(base_value(args[0].first)){|a,x| a+=x;a}
        else
          sum(args)  # recurse in and use the 'inject' option
        end
      end

      def count(*args)        
        args[0].respond_to?('length') && args[0].length ||
        args[0].respond_to?('count') && args[0].count ||
        args.length
      end

      def average(*args)
        (count_value = count(*args).to_i)>0 ? (sum(*args).to_f / count_value.to_f) : 0.0
      end

      def avg(*args)
        average(*args)
      end
      
      def sum2(*args)
        args[0].respond_to?('sum') && args[0].sum ||
        args[0].respond_to?('inject') &&
        args[0].inject(base_value(args[0].first)){|a,x| a+=x;a} ||
        sum(args)  # recurse in and use the 'inject' option
      end
      
    end
  end
end