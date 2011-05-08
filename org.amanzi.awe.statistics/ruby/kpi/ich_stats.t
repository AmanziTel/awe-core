load 'statistics/digitel.rb'
template 'ICH stats' do |t|
  t.metadata :type=>:oss
  t.column 'ich_1' do |c|
    c.formula='KPI::Digitel::ICH.ich_1'
    c.aggregation=:average
  end
  
  t.column 'ich_2' do |c|
    c.formula='KPI::Digitel::ICH.ich_2'
    c.aggregation=:average
  end
  
  t.column 'ich_3' do |c|
    c.formula='KPI::Digitel::ICH.ich_3'
    c.aggregation=:average
  end
  t.column 'ich_4' do |c|
    c.formula='KPI::Digitel::ICH.ich_4'
    c.aggregation=:average
  end
  t.column 'ich_5' do |c|
    c.formula='KPI::Digitel::ICH.ich_5'
    c.aggregation=:average
  end
  
  
end