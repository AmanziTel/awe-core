# And now define some useful commands that can be added either to the IRB shell base, or to any AWEScript run
module AWE
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
    def project(index=nil)
      $active_project = $projects[index||0] if($projects && ($active_map.nil? || index))
      $active_project
    end
    # Find all projects as an array
    def projects
      $projects
    end
    # Find the active map (pass project index and map index if different from 0,0)
    def map(index=nil,map_index=nil)
      $active_map = project(index).elements[map_index||0] if(project && ($active_map.nil? || map_index))
      $active_map
    end
    # Find all maps in current or specified project
    def maps(index=nil)
      project(index) && project.elements
    end
    # Find the current or specified layer in current map
    def layer(index=nil)
      $active_layer = map.map_layers[index||0] if(map && ($active_layer.nil? || index))
      $active_layer
    end
    # Find all the map layers in the current or specified map
    def layers(index=nil)
      map(index) && map(index).map_layers
    end
    # Find the geo_resource of the current or specified layer (pass layer index, default 0)
    def geo_resource(index=nil)
      (lyr=layer(index)) && lyr.geo_resource
    end
    # Find the feature store in the current or specified layer (default layer index 0, default store type FeatureSource)
    def feature_store(index=nil,fs_class=$feature_source_class)
      (geo_rs=geo_resource(index)) && (try_fs_from(geo_rs,fs_class) || try_fs_from(geo_rs,$feature_source_class) || try_fs_from(geo_rs,$json_reader_class))
    end
    # Find all features in the current or specified layer (default layer index 0, default store type FeatureSource)
    def features(index=nil,fs_class=$feature_source_class)
      (fea_str=feature_store(index,fs_class)) && fea_str.features
    end
private
    def try_fs_from(geo_rs,fs_class)
      geo_rs.can_resolve(fs_class) && geo_rs.resolve(fs_class,nil)
    end
  end
end

# Some utilities for setting up AWE, including paths to uDIG jars
module AWE
  class Awe
    class << self
      def returning
        yield
      end
      def setup_libs
        $udig_plugin_path ||= returning do
          Java::JavaLang::System.get_property 'osgi.syspath'
        end
        $udig_lib_path ||= returning do
          $udig_plugin_path+'/net.refractions.udig.libs_1.1.1/lib'
        end
      end
      def print_globals
        print "Useful global variables: "
        puts Kernel.global_variables.find_all{|v| v.length>7 && v.downcase==v || v=~/map/ || v=~/view/}.join(', ')
        puts
      end
      def print_data_model
        if $projects && $projects.length>0
          puts "Data model:"
          ($projects||[]).each_with_index do |pp,pi|
            puts "#{pi}:#{pp.name}" if($projects.length>1)
            pp.elements.each_with_index do |pm,mi|
              puts "#{(mi==0 && pp.elements.length>1) ? '   maps ' : '        '}#{mi}:#{pm.name}"
              pm.map_layers.each_with_index do |ml,li|
                puts "#{(li==0) ? '         layers ' : '                '}#{ml.name}"
              end
            end
          end
        else
          puts "No projects loaded"
        end
        puts
      end
      def print_startup_help
        puts "Useful AWE methods/commands:"
        puts "\t#{AWE::ExtendCommandBundle.public_instance_methods.sort.join(', ')}"
        puts
        puts "Most AWE commands take an optional index parameter and cache the result for later use. " +
             "For example, 'layer 2' will return the third layer, and later calls to 'layer' will return the same one again. " +
             "Methods that depend on elements higher in the tree will use the previously cached results. " +
             "For example, 'features' will return an enumeration of all features in the cached layer. " +
             "To use a different layer, either use 'features #' or use 'layer #' and then 'features'. "
        puts
      end
    end
  end
end

true