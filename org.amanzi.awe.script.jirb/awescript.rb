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
end

# Add the AWE commands defined above to the root of the IRB
module IRB
  module ExtendCommandBundle
    include AWE::ExtendCommandBundle
  end
end

begin
  AWE::Awe.setup_libs
  AWE::Awe.print_globals
  AWE::Awe.print_data_model
  AWE::Awe.print_startup_help
rescue
  puts "Error starting AWEScript"
end
require $udig_lib_path+'/geoapi.jar'
require $udig_lib_path+'/gt2-api.jar'
require $udig_lib_path+'/gt2-main.jar'
