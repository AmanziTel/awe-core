
class Cells #< Java::org.amanzi.splash.jruby.JRubyJavaInterface
  def initialize
    @cells = {'a1' => 'Hello a1'};     
  end
  
#  def method_missing(method_id, *arguments)
#        if method_id.to_s =~ /([a-z]{1,3})([0-9]+)/
#          column_name = $1.to_s
#          row_number = $2.to_i
#          find_cell(column_name,row_number)
#          #JRubyJavaInterface.UpdateCellValueInSpreadsheet("dddd","xxxx");
#        else
#          super.method_missing(method_id, *arguments)
#        end
#      end
#      
#  def find_cell(column_name,row_number)
#     @cells['#{column_name}#{row_number}']   
#  end
  
  def method_missing(meth, *args)
        s = "#{meth}"
        puts "s = " + s
        puts "args.to_s: " + args.to_s 
        s = s.gsub("=","")
        if (args.to_s.eql?"")
          @cells[s]
        else
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
