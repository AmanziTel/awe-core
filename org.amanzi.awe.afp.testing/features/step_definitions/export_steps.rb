include Java

begin 
  require 'rspec/expectations'; 
  rescue LoadError; 
  require 'spec/expectations';
    
  end 
  
$:.unshift(File.dirname(__FILE__) + '/../../lib')

Before do 
  @dir = File.dirname(__FILE__) + '/../../'
end
 
After do
end
 
Given /the control file with dataset at relative path (.*)/ do |controlFilePath|
  controlFilePath = @dir + controlFilePath
  puts "Creating Neo Service Provider ... "
  $service = Java::org.amanzi.neo.core.service.NeoServiceProvider.getProvider().getService();
  file = Java::java.io.File.new(controlFilePath)
  controlFile = Java::org.amanzi.awe.afp.files.ControlFile.new(file)
  $rootName = File.basename(controlFilePath);
  loader = Java::org.amanzi.awe.afp.loaders.AfpLoader.new($rootName, controlFile, $service)
  
  loader.run(Java::org.eclipse.core.runtime.NullProgressMonitor.new);
  
  
end
 
When /the (\w+) function is executed/ do |function|

    
    puts "Finding Afp Root ..."
  afpRoot = Java::org.amanzi.neo.core.utils.NeoUtils.findRootNodeByName($rootName, $service);
  puts "Creating instance of Afp Exporter ... "
  if (afpRoot != nil)
    afpe = Java::org.amanzi.awe.afp.loaders.AfpExporter.new(afpRoot)

    if (function.eql? "createInterferenceFile")
      afpe::createInterferenceFile
    elsif (function.eql? "createNeighboursFile")
      afpe::createNeighboursFile()
    end      
  else
    puts "Error creating Afp root"
    end
end
 
Then /the output interferenceFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path("~/.amanzi/Afptemp/InputInterferenceFile.awe")).should == true
end


