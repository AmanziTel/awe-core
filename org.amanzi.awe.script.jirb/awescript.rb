#!/usr/bin/env ruby

# We modify the IRB startup to be branded AWE
module IRB
  unless self.respond_to? :normal_setup
    class << self
      alias_method :normal_setup, :setup
      def setup(ap_name)
        normal_setup(ap_name)
        #puts "Initializing IRB for AWEScript"
        @CONF[:IRB_NAME] = 'AWE'  # so the irb prompt includes our branding
      end
    end
  end
  # And now we add the 'awe' command to the base of the shell
  module ExtendCommandBundle
    def awe(cmd=nil,*args,&block)
      if !cmd || cmd === :help
        puts "Usage: awe command <args> <{block}>"
        puts "No command given - searching for documentation ..." unless cmd
        help :awe
      else
        puts "AWE[#{cmd}]: #{args.join(', ')}"
        if block
          puts "AWE-block: #{block}"
        end
      end
    end
  end
end

