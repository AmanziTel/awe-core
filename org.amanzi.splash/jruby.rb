class Cells
  def initialize
    @cells = {'a1' => ''};     
  end
  
  	def method_missing(meth, *args)
      s = "#{meth}"
      s = s.gsub("=","")
      puts "s = " + s
      a = args.to_s
      puts "a = " + a
      
      if (a =~ /\d+/ and a.include?"." and (not a.include?"/[a-z]/"))
      	puts "Float detected"
      	b = a.to_f
      elsif (a =~ /\d+/ and (not a =~ /[a-z]/))
      	puts "Integer detected"
      	b = a.to_i
      else
      	puts "String detected"
      	b = a
      end	
      
      if (args.to_s.eql?"")
        puts "value of "+ s + "= " + @cells[s]
        puts "I'm here 1"
        @cells[s]
      else
      	puts "I'm here 2"
        @cells[s] = b
	  end
	end
	puts "I'm here 3"
	
end

	

class Spreadsheet
  def initialize
    @cells = Cells.new
    puts "Spreadsheet has been initialized !!"
  end
  
  def cells
    @cells
  end
  
  
end


