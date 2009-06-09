
class TestSplash < Test::Unit::TestCase
  def setup
    @splashTable = Java::org.amanzi.splash.swing.SplashTable.new(10,10, true)
  end
  
  def test01_display_plain_text_as_plain_text
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test01_display_plain_text_as_plain_text"
    @splashTable.getModel().interpret("PLAINTEXT","",0,0)
    assert_equal "PLAINTEXT",@splashTable.getModel().getValueAt(0,0).getValue()
  end

  def test02_simple_style_formula
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test02_simple_style_formula"
    @splashTable.getModel().interpret("='Ahmed'","",1,0)
    assert_equal "Ahmed",@splashTable.getModel().getValueAt(1,0).getValue()
  end
  
  def test03_erb_style_formula
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test03_erb_style_formula"
    @splashTable.getModel().interpret("<%= 'Craig' %>","",2,0)
    assert_equal "Craig",@splashTable.getModel().getValueAt(2,0).getValue()
  end
  
  def test04_cell_with_reference_to_other_cells
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test04_cell_with_reference_to_other_cells"
    @splashTable.getModel().interpret("='Ahmed'","",1,0)
    @splashTable.getModel().interpret("<%= 'Craig' %>","",2,0)
    @splashTable.getModel().interpret("=a2+a3","",3,0)
    assert_equal "AhmedCraig",@splashTable.getModel().getValueAt(3,0).getValue()
  end
  
  def test05_move_row_down
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test05_move_row_down"
      
    @splashTable.getModel().interpret("<%= 'Craig' %>","",5,0)
    @splashTable.moveRowDown(5);
    assert_equal "Craig",@splashTable.getModel().getValueAt(6,0).getValue()
  end
 
  def test06_move_row_up
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test06_move_row_up"

    @splashTable.getModel().interpret("<%= 'Craig' %>","",8,0)
    @splashTable.moveRowUp(8);
    assert_equal "Craig",@splashTable.getModel().getValueAt(7,0).getValue()
  end
    
  def test07_move_column_left
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test07_move_column_left"

    @splashTable.getModel().interpret("<%= 'Craig' %>","",1,2)
    @splashTable.moveColumnLeft(2);
    assert_equal "Craig",@splashTable.getModel().getValueAt(1,1).getValue()
  end
      
  def test08_move_column_right
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test08_move_column_right"

    @splashTable.getModel().interpret("<%= 'Craig' %>","",1,2)
    @splashTable.moveColumnRight(2);
    assert_equal "Craig",@splashTable.getModel().getValueAt(1,3).getValue()
  end
  
  def test09_insert_row
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test09_insert_row"

    @splashTable.getModel().interpret("<%= 'Craig' %>","",2,2)
      @splashTable.insertRow(2);
      assert_equal "Craig",@splashTable.getModel().getValueAt(3,2).getValue()
  end
  
  def test10_insert_column
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test10_insert_column"

    @splashTable.getModel().interpret("<%= 'Craig' %>","",2,2)
      @splashTable.insertColumn(2);
      assert_equal "Craig",@splashTable.getModel().getValueAt(2,3).getValue()
  end
  
  def test11_cell_formatting_default_font_name
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test11_cell_formatting_default_font_name"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal "Arial",
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontName()
        
        
  end
  
  def test12_cell_formatting_default_font_size
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test12_cell_formatting_default_font_size"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 14,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontSize()
  end
  
  def test13_cell_formatting_default_h_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test13_cell_formatting_default_h_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal -2,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getHorizontalAlignment()
  end
  
  def test14_cell_formatting_default_v_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test14_cell_formatting_default_v_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 0,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getVerticalAlignment()
  end
  
  def test15_cell_formatting_default_font_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test15_cell_formatting_default_font_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
    

    assert_equal Java::java.awt.Color.new(0,0,0),
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontColor()
  end

  def test16_cell_formatting_default_background_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test14_cell_formatting_default_background_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal Java::java.awt.Color.new(255,255,255),
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getBackgroundColor()
  end
  
  def test17_cell_formatting_default_font_style
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test17_cell_formatting_default_font_style"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 0,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontStyle()
  end

  def test18_cell_formatting_change_font_name
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test18_cell_formatting_change_font_name"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal "Arial",
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontName()
    
    cell_format.setFontName("Tahoma");
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
            
    assert_equal "Tahoma",
          @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontName()
          
  end
  
  def test19_cell_formatting_change_font_size
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test19_cell_formatting_change_font_size"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 14,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontSize()
    
    cell_format.setFontSize(12);
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    assert_equal 12,
            @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontSize()
  end
  
  def test20_cell_formatting_change_h_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test20_cell_formatting_change_h_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal -2,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getHorizontalAlignment()
        
    cell_format.setHorizontalAlignment(4)
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);

    assert_equal 4,
      @splashTable.getModel().getValueAt(8,0).getCellFormat().getHorizontalAlignment()
        
  end
  
  def test21_cell_formatting_change_v_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test21_cell_formatting_change_v_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 0,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getVerticalAlignment()
        
     cell_format.setVerticalAlignment(1)
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    assert_equal 1,
          @splashTable.getModel().getValueAt(8,0).getCellFormat().getVerticalAlignment()
      
  end
  
  def test22_cell_formatting_change_font_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test22_cell_formatting_change_font_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal Java::java.awt.Color.new(0,0,0),
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontColor()
        
        cell_format.setFontColor(Java::java.awt.Color.new(255,0,0))
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    assert_equal Java::java.awt.Color.new(255,0,0),
          @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontColor()
      
  end

  def test23_cell_formatting_change_background_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test23_cell_formatting_change_background_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal Java::java.awt.Color.new(255,255,255),
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getBackgroundColor()
        
     cell_format.setBackgroundColor(Java::java.awt.Color.new(0,0,255))
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    assert_equal Java::java.awt.Color.new(0,0,255),
          @splashTable.getModel().getValueAt(8,0).getCellFormat().getBackgroundColor()
      
  end
  
  def test24_cell_formatting_change_font_style
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test24_cell_formatting_change_font_style"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    
      assert_equal 0,
        @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontStyle()
        
        cell_format.setFontStyle(1)
    @splashTable.getModel().getValueAt(8,0).setCellFormat(cell_format);
    assert_equal 1,
            @splashTable.getModel().getValueAt(8,0).getCellFormat().getFontStyle()
  end

      
  def test25_cell_with_reference_to_other_cells_with_changes
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test25_cell_with_reference_to_other_cells_with_changes"
    @splashTable.getModel().interpret("='Ahmed'","",1,0)
    @splashTable.getModel().interpret("<%= 'Craig' %>","",2,0)
    @splashTable.getModel().interpret("=a2+a3","",3,0)
    assert_equal "AhmedCraig",@splashTable.getModel().getValueAt(3,0).getValue()
    @splashTable.getModel().interpret("='Someone'","='Ahmed'",1,0)
    assert_equal "SomeoneCraig",@splashTable.getModel().getValueAt(3,0).getValue()
  end

end