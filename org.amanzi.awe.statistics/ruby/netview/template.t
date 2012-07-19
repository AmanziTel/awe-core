load 'netview/geoptima_formulas.rb'
template 'Geoptima template' do |t|
  t.metadata :type=>:dataset,:drive_type=>:geoptima
  t.column 'No. of samples' do |c|
      c.formula='KPI::Geoptima.no_samples'
      c.aggregation=:sum
    end
  t.column 'signal strength' do |c|
    c.formula='KPI::Geoptima.signal_strength'
    c.aggregation=:average
    c.thresholds do |th|
      th.alert<-102
    end
  end
  t.column 'no coverage' do |c|
    c.formula='KPI::Geoptima.no_coverage'
    c.aggregation=:sum
  end
  t.column 'RSSI>0' do |c|
    c.formula='KPI::Geoptima.rssi_positive'
    c.aggregation=:sum
  end
  t.column 'neighbour events' do |c|
    c.formula='KPI::Geoptima.neighbour'
    c.aggregation=:sum
  end
  t.column 'dominant' do |c|
    c.formula='KPI::Geoptima.dominant'
    c.aggregation=:sum
  end
  t.column 'delta rssi' do |c|
    c.formula='KPI::Geoptima.delta_rssi'
    c.aggregation=:average
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
  t.column 'signal & mode events' do |c|
    c.formula='KPI::Geoptima.mode_signal_events'
    c.aggregation=:sum
  end
  t.column 'mode events' do |c|
    c.formula='KPI::Geoptima.mode_events'
    c.aggregation=:sum
  end
  t.column 'mode strength' do |c|
    c.formula='KPI::Geoptima.mode_signal_strength'
    c.aggregation=:average
    c.thresholds do |th|
      th.alert<-102
    end
  end
  t.column 'latency' do |c|
    c.formula='KPI::Geoptima.latency'
    c.aggregation=:average
  end
  t.column 'throughput' do |c|
    c.formula='KPI::Geoptima.throughput'
    c.aggregation=:average
  end
  t.column 'traffic_sent' do |c|
    c.formula='KPI::Geoptima.traffic_sent'
    c.aggregation=:sum
  end
  t.column 'traffic_received' do |c|
    c.formula='KPI::Geoptima.traffic_received'
    c.aggregation=:sum
  end
end
