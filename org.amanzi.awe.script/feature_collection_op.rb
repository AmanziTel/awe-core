require 'java'

puts "About to define class"

class SomeJRubyObject
   include java.lang.Runnable
   def run
     puts "Running"
   end
end

puts "About to construct object"

s = SomeJRubyObject.new

puts "About to call run method"

s.run

puts "Finished"

begin
	puts "Running JRuby"
	puts "Display: #{$display}"
	puts "Target: #{$target}"
	puts "Features: #{$target.features}"
	puts "Target methods: #{$target.methods.join(', ')}"
	puts "Monitor: #{$monitor}"
	puts "Monitor methods: #{$monitor.methods.join(', ')}"
	features = $target.features

        # Iterate with normal iterator
        feature_iterator = features.iterator;
        begin
            count = 0
            $monitor.beginTask "Ruby iterator", features.size
            puts "Iterating over feature collection using ruby:"
            while(feature_iterator.has_next) do
                feature = feature_iterator.next
                puts "    "+feature.getID if count<10
                count += 1
                $monitor.worked 1
                break if $monitor.isCanceled
                sleep(0.1) if(count%100 == 0)
            end
            puts "... and #{count-10} more features suppressed" if count>=10
        ensure
            features.close(feature_iterator)
            $monitor.done
        end
rescue
	puts "Rescued: #{$!}"
end

