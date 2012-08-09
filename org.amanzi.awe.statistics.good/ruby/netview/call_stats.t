load 'netview/geoptima_formulas.rb'
template 'Geoptima calls template' do |t|
  t.metadata :type=>:drive,:drive_type=>:geoptima
  
  t.column 'call events' do |c|
    c.formula='KPI::Geoptima.call_events'
    c.aggregation=:sum
  end
  t.column 'answering' do |c|
    c.formula='KPI::Geoptima.answering'
    c.aggregation=:sum
  end
  t.column 'connecting' do |c|
    c.formula='KPI::Geoptima.connecting'
    c.aggregation=:sum
  end
  t.column 'connected' do |c|
    c.formula='KPI::Geoptima.connected'
    c.aggregation=:sum
  end
  t.column 'disconnecting' do |c|
    c.formula='KPI::Geoptima.disconnecting'
    c.aggregation=:sum
  end
  t.column 'call_ended' do |c|
    c.formula='KPI::Geoptima.call_ended'
    c.aggregation=:sum
  end
  t.column 'dialling' do |c|
    c.formula='KPI::Geoptima.dialling'
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
