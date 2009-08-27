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
  
class Cells < Java::org.amanzi.splash.common.Cells
  def initialize
    @cells = {'a1' => 'Hello a1'};     
  end
  
    def method_missing(meth, *args)
      s = "#{meth}"
      s = s.gsub("=","")
      if (args.to_s.eql?"")
        # obtain value from spreadsheet and update ruby model
        @cells[s] = Cells.UpdateCellValueFromSpreadsheet(s)
        # return ruby model
        @cells[s]
      else
        # update spreadsheet with new value
        Cells.UpdateCellValueInSpreadsheet(s, args.to_s)
        # update ruby model with new value
        @cells[s] = args.to_s
      end
    end
end

class Spreadsheet #<Java::JavaLang::org.amanzi.splash.core
  def initialize
    @cells = Cells.new
    puts "Spreadsheet has been initialized !!"
    
  end
  
  def cells
    @cells
  end
end 



