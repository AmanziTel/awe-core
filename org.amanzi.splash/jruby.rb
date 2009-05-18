class Cells
  def initialize
    @cells = {'a1' => ''};     
  end
  
  	def method_missing(meth, *args)
      s = "#{meth}"
      s = s.gsub("=","")
      if (args.to_s.eql?"")
        #puts "value of "+ s + "= " + @cells[s]
        @cells[s]
      else
        @cells[s] = args.to_s
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


