awe_console_plugin = Java::org.eclipse.core.runtime.Platform.getBundle("org.amanzi.awe.script.jirb").getEntry("/")
awe_console_path = Java::org.eclipse.core.runtime.FileLocator.resolve(awe_console_plugin).getFile

require awe_console_path + 'gisCommands.rb'
require awe_console_path + 'neoSetup.rb'
require awe_console_path + 'neoSpreadsheet.rb'

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
    end
  end
end

AWE::Awe.setup_libs  
require $udig_lib_path+'/geoapi.jar'
require $udig_lib_path+'/gt2-api.jar'
require $udig_lib_path+'/gt2-main.jar'