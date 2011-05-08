load 'statistics/digitel.rb'
template 'Incoming HO stats' do |t|
  t.metadata :type=>:oss
  t.column 'hi_lost' do |c|
    c.formula='KPI::Digitel::INCOMING_HO.hi_lost'
    c.aggregation=:average
  end
  t.column 'hi_rev' do |c|
    c.formula='KPI::Digitel::INCOMING_HO.hi_rev'
    c.aggregation=:average
  end
  t.column 'hi_suc' do |c|
    c.formula='KPI::Digitel::INCOMING_HO.hi_suc'
    c.aggregation=:average
  end
  
  
end