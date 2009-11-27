module Kernel
 private
    def this_method_name(depth=0)
      caller[depth] =~ /`([^']*)'/ and $1
    end
end

class TestSplash < Test::Unit::TestCase
  def header
    "\n" +
    "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
    this_method_name(1) +
    "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n"
  end
  def setup
    begin
      unless @aweProjectName
        puts "setup begin"
        @aweProjectName="TESTPROJECT"
        @rubyProjectName="TESTPROJECT.TESTPROJECT"
        @spreadSheetName="TEST_SPREADSHEET"
        
        Java::org.amanzi.neo.core.NeoCorePlugin.default.initializer.startupTesting
        puts Java::org.amanzi.neo.core.service.NeoServiceProvider.getProvider.getService
        
        puts "projectService begin" 
        @projectService=Java::org.amanzi.neo.core.NeoCorePlugin.getDefault().getProjectService()
        puts "projectService"    
        @spreadsheetService = Java::org.amanzi.splash.ui.SplashPlugin.getDefault().getSpreadsheetService()
        puts "spreadsheetService"
        @rootNode=@projectService.getRootNode();
        puts "setup complete getRootNode()"
        @spreadSheetNode=@projectService.findOrCreateSpreadsheet( @aweProjectName,@rubyProjectName, @spreadSheetName)
        @projectNode=@projectService.findOrCreateAweProject(@aweProjectName)
        rubyProject = @projectService.findOrCreateRubyProject(@projectNode, @rubyProjectName)
        @splashTableModel=Java::org.amanzi.splash.swing.SplashTableModel.new(@spreadSheetNode, rubyProject)
        puts "setup end"
      
        @splashTable = Java::org.amanzi.splash.swing.SplashTable.new(@splashTableModel, true)
      end
    rescue => e
      puts e
    end
  end

  def teardown
    @splashTableModel.teardown
    puts "teardown"
#    aweProjectNode = @projectService.findAweProject(@aweProjectName)
#    @projectService.deleteNode(aweProjectNode);
    puts "teardown end"
  end

  def test00a_sum
    puts header
    @splashTableModel.interpret('<% 1.upto(4){|x| eval "b#{x+1} = #{x}"} %>',0,1)
    @splashTableModel.interpret('= sum([b2,b3,b4,b5])',5,1)
    @splashTableModel.interpret('= sum(b2,b3,b4,b5)',6,1)
    @splashTableModel.interpret('= sum(b2..b5)',7,1)
    puts "Checking b1-b8 - should have '', 1, 2, 3, 4, 10, 10, 10"
    1.upto(8) do |row|
      puts "b#{row} = #{@splashTableModel.getValueAt(row,1).getValue()}"
    end
    assert_equal '',  @splashTableModel.getValueAt(0,1).getValue()
    assert_equal '1', @splashTableModel.getValueAt(1,1).getValue()
    assert_equal '2', @splashTableModel.getValueAt(2,1).getValue()
    assert_equal '3', @splashTableModel.getValueAt(3,1).getValue()
    assert_equal '4', @splashTableModel.getValueAt(4,1).getValue()
    assert_equal '10',@splashTableModel.getValueAt(5,1).getValue()
    assert_equal '10',@splashTableModel.getValueAt(6,1).getValue()
    assert_equal '10',@splashTableModel.getValueAt(7,1).getValue()
  end

  def test00b_complex_formulas
    puts header
    @splashTableModel.interpret('<% 1.upto(4){|x| eval "b#{x+1} = #{x}"} %>',0,1)
    @splashTableModel.interpret('= sum(b2..b5)',5,1)
    @splashTableModel.interpret('= average(b2..b5)',6,1)
    @splashTableModel.interpret('= max(b2..b5)',7,1)
    puts "Checking b1-b8 - should have '', 1, 2, 3, 4, 10, 2, 4"
    1.upto(8) do |row|
      puts "b#{row} = #{@splashTableModel.getValueAt(row,1).getValue()}"
    end
    assert_equal '',  @splashTableModel.getValueAt(0,1).getValue()
    assert_equal '1', @splashTableModel.getValueAt(1,1).getValue()
    assert_equal '2', @splashTableModel.getValueAt(2,1).getValue()
    assert_equal '3', @splashTableModel.getValueAt(3,1).getValue()
    assert_equal '4', @splashTableModel.getValueAt(4,1).getValue()
    assert_equal '10',@splashTableModel.getValueAt(5,1).getValue()
    assert_equal '2', @splashTableModel.getValueAt(6,1).getValue()
    assert_equal '4', @splashTableModel.getValueAt(7,1).getValue()
  end

  def test01_display_plain_text_as_plain_text
    puts header
    @splashTableModel.interpret("PLAINTEXT",0,0)
    puts "test01_plained"
    assert_equal "PLAINTEXT",@splashTableModel.getValueAt(0,0).getValue()
    puts "test01_asserts"
  end

  def test02_simple_style_formula
    puts header
    @splashTableModel.interpret("='Ahmed'",1,0)
    assert_equal "Ahmed",@splashTableModel.getValueAt(1,0).getValue()
  end

  def test03_erb_style_formula
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",2,0)
    assert_equal "Craig",@splashTableModel.getValueAt(2,0).getValue()
  end

  def test04_cell_with_reference_to_other_cells
    puts header
    @splashTableModel.interpret("='Ahmed'",1,0)
    @splashTableModel.interpret("<%= 'Craig' %>",2,0)
    @splashTableModel.interpret("=a2+a3",3,0)
    assert_equal "AhmedCraig",@splashTableModel.getValueAt(3,0).getValue()
  end

  def test04b_cell_with_numerical_reference_to_other_cells
    puts header
    @splashTableModel.interpret("5",1,0)
    @splashTableModel.interpret("<%= 6 %>",2,0)
    @splashTableModel.interpret("=a2+a3",3,0)
    assert_equal "11",@splashTableModel.getValueAt(3,0).getValue()
  end

  def test05_move_row_down
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",5,0)
    @splashTable.moveRowDown(5);
    assert_equal "Craig",@splashTableModel.getValueAt(6,0).getValue()
  end

  def test06_move_row_up
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",8,0)
    @splashTable.moveRowUp(8);
    assert_equal "Craig",@splashTableModel.getValueAt(7,0).getValue()
  end

  def test07_move_column_left
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",1,2)
    @splashTable.moveColumnLeft(2);
    assert_equal "Craig",@splashTableModel.getValueAt(1,1).getValue()
  end

  def test08_move_column_right
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",1,2)
    @splashTable.moveColumnRight(2);
    assert_equal "Craig",@splashTableModel.getValueAt(1,3).getValue()
  end

  def test09_insert_row
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",2,2)
    @splashTable.insertRow(2);
    assert_equal "Craig",@splashTableModel.getValueAt(3,2).getValue()    
  end

  def test10_insert_column
    puts header
    @splashTableModel.interpret("<%= 'Craig' %>",2,2)
    @splashTable.insertColumn(2);
    assert_equal "Craig",@splashTableModel.getValueAt(2,3).getValue()
  end

  def test11_cell_formatting_default_font_name
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    puts cell_format
    testCell=@splashTableModel.getValueAt(8,0);
    puts testCell
    testCell.setCellFormat(cell_format);

    assert_equal "Arial",
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontName()

  end

  def test12_cell_formatting_default_font_size
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTableModel.getValueAt(8,0).setCellFormat(cell_format);

    assert_equal 14,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontSize()
  end

  def test13_cell_formatting_default_h_align
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    puts testCell.getCellFormat().getHorizontalAlignment()
    @splashTableModel.updateCellFormat(testCell)
    assert_equal -2,
    @splashTableModel.getValueAt(8,0).getCellFormat().getHorizontalAlignment()
  end

  def test14_cell_formatting_default_v_align
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getVerticalAlignment()
  end

  def test15_cell_formatting_default_font_color
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(0,0,0),
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontColor()
  end

  def test16_cell_formatting_default_background_color
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(255,255,255),
    @splashTableModel.getValueAt(8,0).getCellFormat().getBackgroundColor()
  end

  def test17_cell_formatting_default_font_style
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontStyle()
  end

  def test18_cell_formatting_change_font_name
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal "Arial",
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontName()

    cell_format.setFontName("Tahoma");

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal "Tahoma",
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontName()

  end

  def test19_cell_formatting_change_font_size
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 14,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontSize()

    cell_format.setFontSize(12);
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal 12,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontSize()
  end

  def test20_cell_formatting_change_h_align
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal -2,
    @splashTableModel.getValueAt(8,0).getCellFormat().getHorizontalAlignment()

    cell_format.setHorizontalAlignment(4)
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 4,
    @splashTableModel.getValueAt(8,0).getCellFormat().getHorizontalAlignment()

  end

  def test21_cell_formatting_change_v_align
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getVerticalAlignment()

    cell_format.setVerticalAlignment(1)
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal 1,
    @splashTableModel.getValueAt(8,0).getCellFormat().getVerticalAlignment()

  end

  def test22_cell_formatting_change_font_color
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(0,0,0),
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontColor()

    cell_format.setFontColor(Java::java.awt.Color.new(255,0,0))
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal Java::java.awt.Color.new(255,0,0),
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontColor()

  end

  def test23_cell_formatting_change_background_color
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(255,255,255),
    @splashTableModel.getValueAt(8,0).getCellFormat().getBackgroundColor()

    cell_format.setBackgroundColor(Java::java.awt.Color.new(0,0,255))
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal Java::java.awt.Color.new(0,0,255),
    @splashTableModel.getValueAt(8,0).getCellFormat().getBackgroundColor()

  end

  def test24_cell_formatting_change_font_style
    puts header

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontStyle()

    cell_format.setFontStyle(1)
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal 1,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontStyle()
  end

  def test25_cell_with_reference_to_other_cells_with_changes
    puts header
    @splashTableModel.interpret("='Ahmed'",1,1)
    puts "test25_ Ahmed"
    @splashTableModel.interpret("<%= 'Craig' %>",2,1)
    puts "test25_interpret Craig"
    @splashTableModel.interpret("=b2+b3","",3,1)
    puts "test25_interpret(a2+a3"
    assert_equal "AhmedCraig",@splashTableModel.getValueAt(3,1).getValue()
    puts "test25_assert_equal AhmedCraig"
    @splashTableModel.interpret("='Someone'","='Ahmed'",1,1)
    puts "test25_interpret Someone"
    assert_equal "SomeoneCraig",@splashTableModel.getValueAt(3,1).getValue()
    puts "test25_ last"
  end

end