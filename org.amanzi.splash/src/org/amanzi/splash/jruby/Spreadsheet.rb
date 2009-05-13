class Cells
  def initialize
    @cells = {'a1' => 'Hello'};     
  end
  
    def method_missing(meth, *args)
      s = "#{meth}"
      s = s.gsub("=","")
      if (args.to_s.eql?"")
        @cells[s]
      else
        @cells[s] = args.to_s
      end
    end
end

class Spreadsheet
  def initialize
    @cells = Cells.new
  end
  
  def cells
    @cells
  end
end





