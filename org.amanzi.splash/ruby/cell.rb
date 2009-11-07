require 'java'
require 'ruby/demo'

include_class org.amanzi.splash.swing.Cell
include_class com.eteks.openjeks.format.CellFormat

#
# Java class Cell with additional methods to support operations between Cells
#
class Cell
  extend Splash::Formulas::Demo
  attr_writer :beginRange
  attr_writer :endRange
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
  # Returns a succesor of a Cell
  #
  def succ
    if self.row<@endRange.row
      cell=$tableModel.getValueAt(row+1,column)
      cell.beginRange=@beginRange
      cell.endRange=@endRange
      cell
    else
        cell=$tableModel.getValueAt(@beginRange.row,column+1)
        cell.beginRange=@beginRange
        cell.endRange=@endRange
        cell
    end
  end
  

#
  # Compares two cells
  #
  def <=>(value)
    if @beginRange.nil?
      @beginRange=self 
    end
    if @endRange.nil?
      @endRange=value
    end 
    if self.row!=value.row
      self.row<=>value.row
    else
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