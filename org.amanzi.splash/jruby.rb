require 'java'
require $jrubyPath + "/lib/ruby/1.8/erb"

awe_console_plugin = Java::org.eclipse.core.runtime.Platform.getBundle("org.amanzi.awe.script.jirb").getEntry("/")
awe_console_path = Java::org.eclipse.core.runtime.FileLocator.resolve(awe_console_plugin).getFile
require awe_console_path + 'neoSetup.rb'

def method_missing(method_id, *args)  
  if method_id.to_s =~ /([a-z]{1,3})([0-9]+)/      
    #if method_missing was called with ID of Cell than put this ID to array
    $idArray << method_id
    find_cell(method_id)
  else
    super.method_missing(method_id.to_s, *args)
  end
end

#
# Returns value of Cell by given ID
#
def find_cell(cell_id)    
  cell = $tableModel.getCellByID(cell_id.to_s)    
  cell.getValue
end

def max(*args)
  args[0].respond_to?('max') ? args[0].max : args.max
end

def min(*args)
  args[0].respond_to?('min') ? args[0].min : args.min
end

def base_value(value)
  value.is_a?(String) && '' ||
  value.is_a?(Float) && 0.0 ||
  0
end

def sum(*args)
  if args[0].respond_to? 'sum'
    args[0].send 'sum'
  elsif args[0].respond_to? 'inject'
    args[0].inject(base_value(args[0].first)){|a,x| a+=x;a}
  else
    sum(args)  # recurse in and use the 'inject' option
  end
end

def count(*args)
  args[0].respond_to?('length') && args[0].length ||
  args[0].respond_to?('count') && args[0].count ||
  args.length
end

def average(*args)
  (count_value = count(*args).to_i)>0 ? (sum(*args).to_f / count_value.to_f) : 0.0
end

def avg(*args)
  average(*args)
end

def update(currentCellId, formula)      
  #idArray contains IDs of referenced Cells
  $idArray = []
  if formula[0] == '='[0]    
    formula = formula[1..formula.length]
    display = ERB.new("<%= #{formula} %>").result
  else        
    display = ERB.new(formula).result
  end
    
  #if the formula was interpreted than update References of Cell
  $tableModel.updateCellReferences(currentCellId.to_s, $idArray)
  display    
end

class Charts
	def initialize
	
	end
	
	def self.create
		puts "chart has been created..."
		frame = javax.swing.JFrame.new("Chart");
		frame.setDefaultCloseOperation javax.swing.JFrame::EXIT_ON_CLOSE
		frame.content_pane.add(panel=javax.swing.JPanel.new)
		panel.setPreferredSize(java.awt.Dimension.new(700,450))
		
		series = "series"
		dataset = org.jfree.data.category.DefaulyCategory.new
		(0..32).each{|i| dataset.setValue(0,series,i)}
		chart = org.jfree.chart.ChartFactory.createBarChart3D(nil, "neighbors", "servers", dataset, org.jfree.chart.plot.PlotOrientation::VERTICAL, true, true, false)
		
		panel.add org.jfree.chart.ChartPanel.new(chart)
		frame.pack
		frame.setVisible true
	end
end

