load 'statistics/tems_formulas.rb'
template 'TEMS template' do |t|
  t.metadata :type=>:dataset,:drive_type=>"tems"
  t.column 'No. of samples' do |c|
    c.formula='KPI::Tems.no_samples'
    c.aggregation=:sum
  end
  t.column 'No. of events' do |c|
    c.formula='KPI::Tems.no_events'
    c.aggregation=:sum
  end
  t.column 'Call Attempt' do |c|
    c.formula='KPI::Tems.call_attempt'
    c.aggregation=:sum
  end
  t.column 'Call End' do |c|
    c.formula='KPI::Tems.call_end'
    c.aggregation=:sum
  end
  t.column 'Call Established' do |c|
    c.formula='KPI::Tems.call_established'
    c.aggregation=:sum
  end
  t.column 'Call Setup' do |c|
    c.formula='KPI::Tems.call_setup'
    c.aggregation=:sum
  end
  t.column 'Dedicated Mode' do |c|
    c.formula='KPI::Tems.dedicated_mode'
    c.aggregation=:sum
  end
  t.column 'Handover' do |c|
    c.formula='KPI::Tems.handover'
    c.aggregation=:sum
  end
  t.column 'Handover Failure' do |c|
    c.formula='KPI::Tems.handover_failure'
    c.aggregation=:sum
  end
  t.column 'Handover Intracell' do |c|
    c.formula='KPI::Tems.handover_intracell'
    c.aggregation=:sum
  end
  t.column 'Idle Mode' do |c|
    c.formula='KPI::Tems.idle_mode'
    c.aggregation=:sum
  end
end