load 'statistics/digitel.rb'
template 'CONG stats' do |t|
  t.metadata :type=>:oss
  t.column 'tfnrelcong' do |c|
    c.formula='KPI::Digitel::CONG.tfnrelcong'
    c.aggregation=:average
  end
  t.column 'cnrelcong' do |c|
    c.formula='KPI::Digitel::CONG.cnrelcong'
    c.aggregation=:average
  end
  t.column 't_cong' do |c|
    c.formula='KPI::Digitel::CONG.t_cong'
    c.aggregation=:average
  end
  t.column 's_cong' do |c|
    c.formula='KPI::Digitel::CONG.s_cong'
    c.aggregation=:average
  end
  t.column 't_congold' do |c|
    c.formula='KPI::Digitel::CONG.t_congold'
    c.aggregation=:average
  end
  
      
  
end