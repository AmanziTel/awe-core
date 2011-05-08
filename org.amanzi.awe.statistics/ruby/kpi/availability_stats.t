load 'statistics/digitel.rb'
template 'Availability stats' do |t|
  t.metadata :type=>:oss
  t.column 't_avail' do |c|
    c.formula='KPI::Digitel::Availability.t_avail'
    c.aggregation=:average
  end
  
  t.column 't_dwn' do |c|
    c.formula='KPI::Digitel::Availability.t_dwn'
    c.aggregation=:average
  end
end