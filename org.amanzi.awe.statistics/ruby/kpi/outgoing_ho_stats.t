load 'statistics/digitel.rb'
template 'Outgoing HO stats' do |t|
  t.metadata :type=>:oss
  t.column 'ho_lost' do |c|
    c.formula='KPI::Digitel::OUTGOING_HO.ho_lost'
    c.aggregation=:average
  end
  t.column 'ho_rev' do |c|
    c.formula='KPI::Digitel::OUTGOING_HO.ho_rev'
    c.aggregation=:average
  end
  t.column 'ho_suc' do |c|
    c.formula='KPI::Digitel::OUTGOING_HO.ho_suc'
    c.aggregation=:average
  end
  
  
end