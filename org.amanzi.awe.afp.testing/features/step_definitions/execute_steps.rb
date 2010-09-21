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
 
Given /the afp engine executable at relative path (.*)/ do |afpEngine|
  @engine = @dir + afpEngine
end

Given /dataset with control file at relative path (.*)/ do |paramFile|
  
  @param = @dir + paramFile
end

Given /the actual output file will be at relative path (.*)/ do |outputFile|
  @outputFile = @dir + outputFile
  FileUtils.rm @outputFile, :force =>true
end
 
When /the engine is executed/ do
  command= @engine + " " + @param;
  bb = IO.popen(command)
  b = bb.readlines
  puts b.join
end
 
Then /the output file should match the expected file at relative path (.*)/ do |expectedFile|
  FileUtils.compare_file(@dir + expectedFile, @outputFile).should == true
end

Then /the output file should not be generated/ do
  File.exist?(@outputFile).should == false
end


