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
      if (a =~ /\d+/ and a.include?".")
      	puts "FLOAT DETECTED"
      	b = a.to_f
      elsif (a =~ /\d+/)
      	puts "INTEGER DETECTED"
      	b = a.to_i
      else
      	b = a
      end
      
      if (args.to_s.eql?"")
        #puts "value of "+ s + "= " + @cells[s]
        @cells[s]
      else
        @cells[s] = b #args[0].to_i #.to_s
	  end
	end
	
	
end

	

class Spreadsheet
  def initialize
    @cells = Cells.new
    #puts "Spreadsheet has been initialized !!"
  end
  
  def cells
    @cells
  end
  
  
end


