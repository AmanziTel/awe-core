include Java

begin 
  require 'rspec/expectations'; 
  rescue LoadError; 
  require 'spec/expectations';
    
  end 
  
$:.unshift(File.dirname(__FILE__) + '/../../lib')

@dir = File.dirname(__FILE__) + '/../../'
$databaseDir = File.expand_path("~/.amanzi/afp-test")
$service = Java::org.neo4j.kernel.EmbeddedGraphDatabase.new($databaseDir)
Java::org.amanzi.neo.core.service.NeoServiceProvider.initProvider($service)

# Cleanup
at_exit{
  $service.shutdown();
  FileUtils.remove_dir($databaseDir)
}
Before do  
end
 
After do
end
 
Given /the control file with dataset at relative path (.*)/ do |controlFilePath|
  controlFilePath = @dir + controlFilePath
  file = Java::java.io.File.new(controlFilePath)
  controlFile = Java::org.amanzi.awe.afp.files.ControlFile.new(file)
  $rootName = File.basename(controlFilePath);
  loader = Java::org.amanzi.awe.afp.loaders.AfpLoader.new($rootName, controlFile, $service)
  
  loader.runAfpLoader(Java::org.eclipse.core.runtime.NullProgressMonitor.new, "AWE_PROJECT");
  puts "Finding Afp Root ..."
  afpRoot = Java::org.amanzi.neo.core.utils.NeoUtils.findRootNodeByName($rootName, $service);  
  puts afpRoot
  $parameters = Java::java.util.HashMap.new
  afpRoot.getPropertyKeys().each do |key|
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
  puts "Comparing the interference files... "
  
  cell = String.new
  hash_expected = Hash.new
  File.read(@dir + expectedFile).each do |line|
    line = line.strip
    if (line.split("\s")[0].eql?("SUBCELL"))
      cell = line
      hash_expected.store(cell, 0)
    elsif(line.split("\s")[0].eql?("INT"))
      hash_expected[cell] += 1;
    end
  end
  
  hash_actual = Hash.new
  File.read(@dir + expectedFile).each do |line|
    line = line.strip
    if (line.split("\s")[0].eql?("SUBCELL"))
      cell = line
      hash_actual.store(cell, 0)
    elsif(line.split("\s")[0].eql?("INT"))
      hash_actual[cell] += 1;
    end
  end
  
  (hash_actual == hash_expected).should ==true
  
end


Then /the output neighboursFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the neighbour files... "
  cell = String.new
  hash_expected = Hash.new
  File.read(@dir + expectedFile).each do |line|
    line = line.strip
    if (line.split("\s")[0].eql?("CELL"))
      cell = line
      hash_expected.store(cell, 0)
    else
      hash_expected[cell] += 1;
    end
  end
  
  hash_actual = Hash.new
  File.read(@dir + expectedFile).each do |line|
    line = line.strip
    if (line.split("\s")[0].eql?("CELL"))
      cell = line
      hash_actual.store(cell, 0)
    else
      hash_actual[cell] += 1;
    end
  end
  
  (hash_actual == hash_expected).should ==true


end

Then /the output exceptionFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the exception files... "
  
  arr_expected = Array.new
  File.read(@dir + expectedFile).each do |line|
    line= line.strip
    arr_expected.push(line)
  end
  
  arr_actual = Array.new
  File.read(File.expand_path($tmpFolder + "InputExceptionFile.awe")).each do |line|
    line = line.strip
    arr_actual.push(line)
  end
  
  #Check if both arrays are equal after sorting
  arr_actual.sort.eql?(arr_expected.sort).should == true
end

Then /the output forbiddenFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the forbidden files... "
  
  arr_expected = Array.new
  File.read(@dir + expectedFile).each do |line|
    line= line.strip
    arr_expected.push(line)
  end
  
  arr_actual = Array.new
  File.read(File.expand_path($tmpFolder + "InputForbiddenFile.awe")).each do |line|
  line = line.strip
    arr_actual.push(line)
  end
  
  #Check if both arrays are equal after sorting
  arr_actual.sort.eql?(arr_expected.sort).should == true
end


Then /the output carrierFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the cell files... "
    arr_expected = Array.new
  File.read(@dir + expectedFile).each do |line|
    line= line.strip
    arr_expected.push(line)
  end
  
  arr_actual = Array.new
  File.read(File.expand_path($tmpFolder + "InputCellFile.awe")).each do |line|
  line = line.strip
    arr_actual.push(line)
  end
  
  #Check if both arrays are equal after sorting
  arr_actual.sort.eql?(arr_expected.sort).should == true

end


Then /the output controlFile should match the expected file at relative path (.*)/ do |expectedFile|
  puts "Comparing the control files... "
  #Check the control files as they are, as order of parameters should also be same
  FileUtils.compare_file(@dir + expectedFile, File.expand_path($tmpFolder + "InputControlFile.awe")).should == true
end