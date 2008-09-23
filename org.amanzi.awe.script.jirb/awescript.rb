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
  # And now we add some useful commands to the base of the shell
  module ExtendCommandBundle
    # This is a placeholder for a possible set of AWE commands for the DSL or API
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
    # Find and set the active project (pass project index if other than 0)
    def project(index=0)
      $active_project ||= $projects[index] if $projects
    end
    # Find the active map (pass project index and map index if different from 0,0)
    def map(index=0,map_index=0)
      $active_map = nil if($active_map.to_s =~ /ProjectImpl/)	# workaround bug
      $active_map ||= project(index).elements[map_index] if project
    end
    # Find the map layer (pass index, defaults to 0)
    def layer(index=0)
      map && map.map_layers[index]
    end
    # Find the geo_resource of the layer (pass layer index, default 0)
    def geo_resource(index=0)
      (lyr=layer(index)) && lyr.geo_resource
    end
    # Find the feature store in the specified layer (default layer index 0, default store type FeatureSource)
    def feature_store(index=0,fs_class=$feature_source_class)
      (geo_rs=geo_resource(index)) && (try_fs_from(geo_rs,fs_class) || try_fs_from(geo_rs,$feature_source_class) || try_fs_from(geo_rs,$json_reader_class))
    end
    def try_fs_from(geo_rs,fs_class)
      geo_rs.can_resolve(fs_class) && geo_rs.resolve(fs_class,nil)
    end
    # Find all features in the specified layer (default layer index 0, default store type FeatureSource)
    def features(index=0,fs_class=$feature_source_class)
      (fea_str=feature_store(index,fs_class)) && fea_str.features
    end
  end
end

module AWE
  class Setup
    class << self
      def setup
        setup_libs
        print_globals
      end
      def returning
        yield
      end
      def setup_libs
        $udig_sdk_plugins ||= returning do
          '/home/craig/dev/udig-1.1-aug18/udig-sdk/plugins'
        end
        $udig_sdk_lib ||= returning do
          $udig_sdk_plugins+'/net.refractions.udig.libs_1.1.0/lib'
        end
      end
      def print_globals
        print "Useful global variables: "
        puts Kernel.global_variables.find_all{|v| v.length>7 && v.downcase==v || v=~/map/ || v=~/view/}.join(', ')
        if Kernel.global_variables.grep(/projects/)
          puts "Loaded projects: #{$projects.map{|p|p.name}.join(', ')}"
        end
        if Kernel.global_variables.grep(/maps/)
          puts "Open maps: #{$maps.map{|m|m.name}.join(', ')}"
        end
      end
    end
  end
end

AWE::Setup.setup
require $udig_sdk_lib+'/geoapi.jar'
require $udig_sdk_lib+'/gt2-api.jar'
require $udig_sdk_lib+'/gt2-main.jar'
