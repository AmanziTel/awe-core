load 'geoptima/formulas.rb'
template 'Geoptima template' do |t|
  t.metadata :type=>:dataset,:drive_type=>:tems
  t.column 'signal strength' do |c|
    c.formula='KPI::Geoptima.signal_strength'
    c.aggregation=:average
    c.thresholds do |th|
      th.alert<-102
    end
  end
  t.column 'browser events' do |c|
    c.formula='KPI::Geoptima.browser_events'
    c.aggregation=:sum
  end
  t.column 'call events' do |c|
    c.formula='KPI::Geoptima.call_events'
    c.aggregation=:sum
  end
  t.column 'GPS events' do |c|
    c.formula='KPI::Geoptima.gps_events'
    c.aggregation=:sum
  end
  t.column 'signal events' do |c|
    c.formula='KPI::Geoptima.signal_events'
    c.aggregation=:sum
  end
end