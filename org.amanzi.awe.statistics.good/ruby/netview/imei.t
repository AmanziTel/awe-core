load 'netview/geoptima_formulas.rb'
template 'Geoptima IMEI template' do |t|
  t.metadata :type=>:dataset,:drive_type=>:geoptima
  $MODELS.each_with_index do |e,i|
    t.column e do |c|
      c.formula="KPI::Geoptima.model_#{i}"
      c.aggregation=:sum
    end
  end
  t.column "No value" do |c|
    c.formula="KPI::Geoptima.model_no_value"
    c.aggregation=:sum
  end
end
