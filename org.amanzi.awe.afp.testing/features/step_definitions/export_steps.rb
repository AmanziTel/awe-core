include Java

begin 
  require 'rspec/expectations'; 
  rescue LoadError; 
  require 'spec/expectations';
    
  end 
  
$:.unshift(File.dirname(__FILE__) + '/../../lib')

@dir = File.dirname(__FILE__) + '/../../'

Before do  
end
 
After do
end
 
Given /the control file with dataset at relative path (.*)/ do |controlFilePath|
  controlFilePath = @dir + controlFilePath
  puts "Creating Neo Service Provider ... "
  $service = Java::org.amanzi.neo.core.service.NeoServiceProvider.getProvider().getService();
  puts "Neo Service provider created"
  file = Java::java.io.File.new(controlFilePath)
  controlFile = Java::org.amanzi.awe.afp.files.ControlFile.new(file)
  $rootName = File.basename(controlFilePath);
  loader = Java::org.amanzi.awe.afp.loaders.AfpLoader.new($rootName, controlFile, $service)
  
  loader.run(Java::org.eclipse.core.runtime.NullProgressMonitor.new);
  puts "Finding Afp Root ..."
  afpRoot = Java::org.amanzi.neo.core.utils.NeoUtils.findRootNodeByName($rootName, $service);  
  $parameters = Java::java.util.HashMap.new
  afpRoot.getPropertyKeys().each do |key|
  puts key
    if (key.eql? Java::org.amanzi.awe.afp.ControlFileProperties::CARRIERS)
      $parameters.put(key, afpRoot.getProperty(key)[2..-1].gsub("\s", ","))
    else $parameters.put(key, afpRoot.getProperty(key))
    end
    
  end

  puts "Creating Afp Exporter ... "
  if (afpRoot != nil)
    $afpe = Java::org.amanzi.awe.afp.loaders.AfpExporter.new(afpRoot)
  else
    puts "Error creating Afp root"
  end
  
end
 
When /the (\w+) function is executed/ do |function|
   $tmpFolder = $afpe.getTmpFolderPath
    case function
      when "createInterferenceFile" then $afpe::createInterferenceFile
      when "createNeighboursFile" then $afpe::createNeighboursFile
      when "createExceptionFile" then $afpe::createExceptionFile
      when "createForbiddenFile" then $afpe::createForbiddenFile
      when "createCarrierFile" then $afpe::createCarrierFile
      when "createControlFile" then $afpe::createControlFile($parameters)
    end 
  
end
 
Then /the output interferenceFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputInterferenceFile.awe")).should == true
end

Then /the output neighboursFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputNeighboursFile.awe")).should == true
end

Then /the output exceptionFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputExceptionFile.awe")).should == true
end

Then /the output forbiddenFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputForbiddenFile.awe")).should == true
end

Then /the output carrierFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputCellFile.awe")).should == true
end

Then /the output controlFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the files... "
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputControlFile.awe")).should == true
end
