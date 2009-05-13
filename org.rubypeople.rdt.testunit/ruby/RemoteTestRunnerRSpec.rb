require 'spec/runner/formatter/base_formatter'

module Spec
  module Runner
    class EclipseExampleGroupRunner < ExampleGroupRunner
      
      def initialize(options, *args)
        super(options)
      end
      
      def run
        prepare
        success = true
        example_groups.each do |example_group|
          eclipse_formatter.prepare(example_group)
          success = success & example_group.suite.run
        end
        return success
      ensure
        finish
      end
      
      def prepare
        eclipse_formatter.begin_block(number_of_examples)
        super
      end
      
      def finish        
        eclipse_formatter.end_block
        super
      end
      
      def eclipse_formatter
        return @eclipse_formatter if @eclipse_formatter
        
        @eclipse_formatter = @options.formatters.find{|o| Spec::Runner::Formatter::EclipseProgressBarFormatter===o }
        
        if @eclipse_formatter.nil?
          raise "Something went wrong!  The RemoteTestRunner.rb script should have set spec_options.formatters = [ Spec::Runner::Formatter::EclipseProgressBarFormatter.new(spec_options, session) ].  Something must have overwritten it, or this script is not installed properly"
        end
        @eclipse_formatter
      end
    end
    
    module Formatter
      class EclipseProgressBarFormatter < BaseFormatter
        def initialize(options, output)
          @output = output
          @started_at = Time.now
          
          @uniq_ids={}
          @uniq_id = 0
          
          super
        end
        
        def begin_block(count_tests)
          @started_at = Time.now
          send_message("TESTC", "#{count_tests} v2")
        end
        
        def end_block()
          return if @started_at.nil?
          send_message("RUNTIME", ((Time.now-@started_at)*1000).to_i.to_s )
        end
        
        def add_example_group(name)
        end
        
        def start_dump
        end
        
        def example_pending(example_group_name, example_name, message)
          if @last_example
            # we're going to trick the Test::Unit runner into displaying a 4th "pending" status
            # if we pass it a different ID for the tree entry than used by starting/stopping the test, it will show up as no status
            send_tree_entry(uniq_id(example_group, @last_example, 'pending'), description_for_example(@last_example),false,1)
            send_example_message("TESTS", @last_example)
            puts("#{message} - #{example_group_name} #{example_name}")
            send_example_stopped(@last_example)
            @last_example = nil
          end
        end
  
        def example_started(example)
          @last_example = example
          # communicate nothing - because the description might change on the spec, so we can't send any information about the spec until after it's done running
        end
        
        def send_example_started(example)
          send_tree_entry(uniq_id(example_group, example), description_for_example(example),false,1)
          send_example_message("TESTS", example)
        end
        
        def send_example_stopped(example)
          send_example_message("TESTE", example)
        end
        
        def clean_backtrace(backtrace)
          found = false
          
          backtrace.map{|b| 
            if /RemoteTestRunner/.match(b) 
              found = true
            end
            
            found ? nil : b
          }.compact
        end
        
        def example_failed(example, counter, failure)
          send_example_started(example)
          was_failure = failure.exception.is_a?(Spec::Expectations::ExpectationNotMetError)
          failure_message = was_failure ? "FAILED" : "ERROR"
          send_example_message(failure_message, example)
          send_example_trace(<<-EOF)
#{failure.exception.class}:
#{failure.exception.message}
#{clean_backtrace(failure.exception.backtrace) * "\n"}\n
          EOF
          send_example_stopped(example)
        end
        
        def example_passed(example)
          send_example_started(example)
          send_example_stopped(example)
        end
        
        def send_example_trace(text)
          send_message("TRACES")
          output_single("#{text.strip}\n")
          send_message("TRACEE")
        end
        
        def send_example_message(message_code, example)
          send_message(message_code, pad_number(uniq_id(example_group, example)), description_for_example(example))
        end
        
        def send_message(message_code, *message_args)
          out = "%" + pad_string(message_code, 7)
          out << message_args.map{|m| escape_commas(m)} * ","
          out << "\n"
          output_single(out)
        end
        
        def prepare(example_group)
          self.example_group = example_group
          send_tree(example_group)
        end
        
        def send_tree(example_group)
          return false unless example_group.suite.examples.length > 0
          path=example_group.instance_variable_get("@spec_path")
          formatted_path = path.to_s.strip.gsub(/\:[0-9]*$/, "")
          
          formatted_path = formatted_path.gsub(/^.+(\/spec\/.+)$/) {|m| $1 }
          
          send_tree_entry(uniq_id(example_group), escape_description(example_group_description), true, example_group.suite.examples.length)
        end
        
        def send_tree_entry(tree_id, name, parent_node = false, count_children = 1)
          send_message("TSTTREE", pad_number(tree_id), name, parent_node, count_children)
        end
        
        def example_group_description
          example_group.description
        end
        
        def description_for_example(example)
          escape_description(example.description) + "(" + escape_description(example_group_description) + ")"
        end
        
        def escape_description(s)
          s.to_s.strip.gsub(/[\n\r\)\(,"']/, "_")
        end
       
        def escape_commas(s)
          s.to_s.gsub(/\\/, "\\\\").gsub(',', "\\,")
        end
        
        def pad_number(number, count=8)
          number = number.to_s
          ("0" * (count-number.length)) + number
        end
        
        def pad_string(str, count, pad_char = " ")
          str = str.to_s
          str + (pad_char * (count - str.length))
        end
        
        # when using shared behaviors, examples will not have unique object_id's.  thus it becomes necessary to generate a new unique value based on the Suite and example_id
        def uniq_id(*args)
          @uniq_ids[args] || @uniq_ids[args] = (@uniq_id += 1)
        end
        
        def output(something)
          @output.puts(something)
          @output.flush
        end
        
        def output_single(something)
          @output.write(something)
          @output.flush
        end
      end
    end
  end
end