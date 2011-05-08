load 'statistics/digitel.rb'
template 'CNDROP stats' do |t|
  t.metadata :type=>:oss
  t.column 'cndrop' do |c|
    c.formula='KPI::Digitel::CNDROP.cndrop'
    c.aggregation=:average
  end
  t.column 'cnrelcong' do |c|
    c.formula='KPI::Digitel::CNDROP.cnrelcong'
    c.aggregation=:average
  end
  t.column 'cdista' do |c|
    c.formula='KPI::Digitel::CNDROP.cdista'
    c.aggregation=:average
  end
    
  t.column 'cdisqa' do |c|
    c.formula='KPI::Digitel::CNDROP.cdisqa'
    c.aggregation=:average
  end
  t.column 'cdisss' do |c|
    c.formula='KPI::Digitel::CNDROP.cdisss'
    c.aggregation=:average
  end
    
  
end