load 'statistics/digitel.rb'
template 'SDROP stats' do |t|
  t.metadata :type=>:oss
  t.column 's_dr_ss' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_ss'
    c.aggregation=:average
  end
  t.column 's_dr_bq' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_bq'
    c.aggregation=:average
  end
  t.column 's_dr_ta' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_ta'
    c.aggregation=:average
  end
  t.column 's_dr_oth' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_oth'
    c.aggregation=:average
  end
  t.column 's_dr_dr_c' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_c'
    c.aggregation=:average
  end
  t.column 's_dr_dr_c_ntc' do |c|
    c.formula='KPI::Digitel::SDROP.s_dr_c_ntc'
    c.aggregation=:average
  end
      
  
end