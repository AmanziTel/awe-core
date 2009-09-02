class TestSplash < Test::Unit::TestCase
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
    puts "teardown"
#    aweProjectNode = @projectService.findAweProject(@aweProjectName)
#    @projectService.deleteNode(aweProjectNode);
    puts "teardown end"
  end

  def test01_display_plain_text_as_plain_text
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test01_display_plain_text_as_plain_text"
    @splashTableModel.interpret("PLAINTEXT","",0,0)
    puts "test01_plained"
    assert_equal "PLAINTEXT",@splashTableModel.getValueAt(0,0).getValue()
    puts "test01_asserts"
  end

  def test02_simple_style_formula
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test02_simple_style_formula"
    @splashTableModel.interpret("='Ahmed'","",1,0)
    assert_equal "Ahmed",@splashTableModel.getValueAt(1,0).getValue()
  end

  def test03_erb_style_formula
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test03_erb_style_formula"
    @splashTableModel.interpret("<%= 'Craig' %>","",2,0)
    assert_equal "Craig",@splashTableModel.getValueAt(2,0).getValue()
  end

  def test04_cell_with_reference_to_other_cells
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test04_cell_with_reference_to_other_cells"
    @splashTableModel.interpret("='Ahmed'","",1,0)
    @splashTableModel.interpret("<%= 'Craig' %>","",2,0)
    @splashTableModel.interpret("=a2+a3","",3,0)
    assert_equal "AhmedCraig",@splashTableModel.getValueAt(3,0).getValue()
  end

  def test05_move_row_down
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test05_move_row_down"

    @splashTableModel.interpret("<%= 'Craig' %>","",5,0)
    @splashTable.moveRowDown(5);
    assert_equal "Craig",@splashTableModel.getValueAt(6,0).getValue()
  end

  def test06_move_row_up
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test06_move_row_up"

    @splashTableModel.interpret("<%= 'Craig' %>","",8,0)
    @splashTable.moveRowUp(8);
    assert_equal "Craig",@splashTableModel.getValueAt(7,0).getValue()
  end

  def test07_move_column_left
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test07_move_column_left"

    @splashTableModel.interpret("<%= 'Craig' %>","",1,2)
    @splashTable.moveColumnLeft(2);
    assert_equal "Craig",@splashTableModel.getValueAt(1,1).getValue()
  end

  def test08_move_column_right
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test08_move_column_right"

    @splashTableModel.interpret("<%= 'Craig' %>","",1,2)
    @splashTable.moveColumnRight(2);
    assert_equal "Craig",@splashTableModel.getValueAt(1,3).getValue()
  end

  def test09_insert_row
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test09_insert_row"

    @splashTableModel.interpret("<%= 'Craig' %>","",2,2)
    @splashTable.insertRow(2);
    assert_equal "Craig",@splashTableModel.getValueAt(3,2).getValue()
  end

  def test10_insert_column
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test10_insert_column"

    @splashTableModel.interpret("<%= 'Craig' %>","",2,2)
    @splashTable.insertColumn(2);
    assert_equal "Craig",@splashTableModel.getValueAt(2,3).getValue()
  end

  def test11_cell_formatting_default_font_name
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test11_cell_formatting_default_font_name"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    puts cell_format
    testCell=@splashTableModel.getValueAt(8,0);
    puts testCell
    testCell.setCellFormat(cell_format);

    assert_equal "Arial",
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontName()

  end

  def test12_cell_formatting_default_font_size
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test12_cell_formatting_default_font_size"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    @splashTableModel.getValueAt(8,0).setCellFormat(cell_format);

    assert_equal 14,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontSize()
  end

  def test13_cell_formatting_default_h_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test13_cell_formatting_default_h_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new
    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    puts testCell.getCellFormat().getHorizontalAlignment()
    @splashTableModel.updateCellFormat(testCell)
    assert_equal -2,
    @splashTableModel.getValueAt(8,0).getCellFormat().getHorizontalAlignment()
  end

  def test14_cell_formatting_default_v_align
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test14_cell_formatting_default_v_align"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)
    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getVerticalAlignment()
  end

  def test15_cell_formatting_default_font_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test15_cell_formatting_default_font_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(0,0,0),
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontColor()
  end

  def test16_cell_formatting_default_background_color
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test14_cell_formatting_default_background_color"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal Java::java.awt.Color.new(255,255,255),
    @splashTableModel.getValueAt(8,0).getCellFormat().getBackgroundColor()
  end

  def test17_cell_formatting_default_font_style
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test17_cell_formatting_default_font_style"

    cell_format = Java::com.eteks.openjeks.format.CellFormat.new

    testCell=@splashTableModel.getValueAt(8,0);
    testCell.setCellFormat(cell_format);
    @splashTableModel.updateCellFormat(testCell)

    assert_equal 0,
    @splashTableModel.getValueAt(8,0).getCellFormat().getFontStyle()
  end

  def test18_cell_formatting_change_font_name
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test18_cell_formatting_change_font_name"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test19_cell_formatting_change_font_size"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test20_cell_formatting_change_h_align"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test21_cell_formatting_change_v_align"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test22_cell_formatting_change_font_color"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test23_cell_formatting_change_background_color"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test24_cell_formatting_change_font_style"

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
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test25_cell_with_reference_to_other_cells_with_changes"
    @splashTableModel.interpret("='Ahmed'","",1,1)
    puts "test25_ Ahmed"
    @splashTableModel.interpret("<%= 'Craig' %>","",2,1)
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