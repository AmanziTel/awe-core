#
# Ruby Class for Cells of Spreadsheet   
#
class Cells
  #intialize Cells with current Spreadsheet model
  def initialize(model)    
    @model = model
  end

  #this method provides access directly to cells
  def method_missing(meth, *args)
    s = "#{meth}"
    s = s.gsub("=","")    
    puts meth
    if (args.to_s.eql?"")     
      @model.getValue(s)
    else
      @model.setValue(s, args.to_s)
	end
  end  
  
end

#
# Ruby Class for access to Spreadsheet
#
class Spreadsheet
  def initialize(model)
    @cells = Cells.new(model)
    @model = model
  end

  def cells
    @cells
  end
  
  #search for Spreadsheet by it's name and project name
  def self.find(project, name)
    Spreadsheet.new($spreadsheet_manager.getSpreadsheet(project, name))
  end
  
  #returns Active Spreadsheet
  def self.findActive()    
    Spreadsheet.new($spreadsheet_manager.getActiveSpreadsheet())
  end
  
  #saves content of this Spreadsheet
  def save
    @model.save
  end

end

begin
  true 
end
