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
    result = first_value.send(method_id, *args).to_s    
    
    Cell.new(row, column, definition, result, CellFormat.new)
  end
  
  #
  # ERB.result method returns not Cell object but Cell.to_s
  # so we should correct method to_s to have a value of Cell
  #
  def to_s
    value
  end
  
  #
  # Return a Value of Cell by it's type
  #
  def get_typed_value
    case cell_data_type      
      when Cell::CellDataType::DEFAULT
        #If type was DEFAULT than it can be String, Integer or Float
        begin 
          result = Integer(value)
        rescue
          begin 
            result = Float(value)
          rescue
            result = value
          end
        end
        result
      when Cell::CellDataType::STRING     
        result
      when Cell::CellDataType::INTEGER
        Integer(value)
      when Cell::CellDataType::FLOAT
        Float(value)
      else
        raise "Not supported type"
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