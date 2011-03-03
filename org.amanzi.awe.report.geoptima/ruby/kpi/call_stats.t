load 'geoptima/formulas.rb'
template 'Geoptima calls template' do |t|
t.metadata :type=>:dataset,:drive_type=>:tems

t.column 'call events' do |c|
  c.formula='KPI::Geoptima.call_events'
  c.aggregation=:sum
end
t.column 'call in progress' do |c|
  c.formula='KPI::Geoptima.call_in_progress'
  c.aggregation=:sum
end
t.column 'idle' do |c|
  c.formula='KPI::Geoptima.idle'
  c.aggregation=:sum
end
t.column 'ringing' do |c|
  c.formula='KPI::Geoptima.ringing'
  c.aggregation=:sum
end

t.column 'missed call' do |c|
  c.formula='KPI::Geoptima.missed_call'
  c.aggregation=:sum
end
t.column 'mobile originated call' do |c|
  c.formula='KPI::Geoptima.mobile_originated_call'
  c.aggregation=:sum
end
t.column 'mobile terminated call' do |c|
  c.formula='KPI::Geoptima.mobile_terminated_call'
  c.aggregation=:sum
end
t.column 'mt call connected' do |c|
  c.formula='KPI::Geoptima.mt_call_connected'
  c.aggregation=:sum
end

end