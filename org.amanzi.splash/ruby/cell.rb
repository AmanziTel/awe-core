require 'java'

include_class org.amanzi.splash.swing.Cell
include_class com.eteks.openjeks.format.CellFormat

#
# Java class Cell with additional methods to support operations between Cells
#
class Cell
  
  #
  # Handles operation or method for Cell
  #
  def method_missing(method_id, *args)
    #gets a value of this Cell
    first_value = get_typed_value
    
    #gets a value of other Cell
    other_value = get_cell_value(args[0])
    
    #updates type of other Cell
    other_value = update_type(first_value, other_value)
    
    #replaces Cell in args array with it's value
    args[0] = other_value
    
    #call a method for values 
    result = first_value.send(method_id, *args)
        
    Cell.new(row, column, definition, result, CellFormat.new)
  end
  
  #
  # Operation +
  #
  def + (other_value)
    #gets a value of this Cell
    first_value = get_typed_value
    
    #gets a value of other Cell
    other_value = get_cell_value(other_value)
    
    #updates type of other Cell
    other_value = update_type(first_value, other_value)
    
    result = first_value + other_value
    
    Cell.new(row, column, definition, result, CellFormat.new)
  end
  
  #
  # Return a Value of Cell by it's type
  #
  def get_typed_value
    #If type was DEFAULT than it can be String, Integer or Float
    if (cell_format.format.nil?)
      begin 
        result = Integer(value)
      rescue
        begin 
          result = Float(value)
        rescue
          result = value
        end
      end
    else
      result = value
    end
    result
  end
  
  def coerce(n)
    result= [n, get_typed_value]
  end  


  #
  # Converts Cell to String
  #
  def to_str
    value.to_s
  end
  
  def to_s
    value.to_s
  end
  
  #
  # Returns a succesor of Cell (only single row and sinle column rahges are supported now)
  #
  def succ
    #    puts "Cell.succ is called for R#{row}C#{column}"
    #    puts "Last compared: R#{@lastCompared.row}C#{@lastCompared.column}"
    rows=@lastCompared.row-self.row
    columns=@lastCompared.column-self.column
    if rows!=0
      if columns==0
        #single column
        $tableModel.getValueAt(row+1,column)
      else
        #TODO
      end
    else
      if columns!=0
        #single row
        $tableModel.getValueAt(row,column+1)
      end
    end
  end

  #
  # Compares two cells (only single row and sinle column rahges are supported now)
  #
  def <=>(value)
    #    puts "Compare: R#{self.row}C#{self.column}(self)<=>R#{value.row}C#{value.column}(other value)"
    @lastCompared=value
    if self.row!=value.row
      #      puts "self.row<=>value.row"
      self.row<=>value.row
    else
      #        puts "self.column<=>value.column"
      self.column<=>value.column
    end
  end
  
  private
  
  #
  # Updates type of other_value in case if they have different types
  # For example if first_value is String and other_value is Integer than other_value should be converted to String
  #
  # Not implemented yet.
  #
  def update_type(first_value, other_value)    
    other_value
  end
  
  #
  # Returns a value of Cell (in case if other_value is Cell)
  #
  def get_cell_value(other_value)
    if other_value.instance_of? Cell
      other_value.get_typed_value
    else
      other_value
    end    
  end
 
end