load 'grid/grid.rb'
template 'IDEN template' do |t|
  t.metadata :type=>:oss
  t.column 'No. of samples' do |c|
    c.formula='KPI::IDEN.no_samples'
    c.aggregation=:sum
  end
  t.column 'Dispatch  voice  call  attempts' do |c|
    c.formula='KPI::IDEN.dispatch_voice_call_attempts'
    c.aggregation=:average
  end
  t.column 'DCCH Blocking Rate' do |c|
    c.formula='KPI::IDEN.dcch_blk_rate'
    c.thresholds do |th|
      th.alert>2
    end
    c.aggregation=:average
  end
  t.column 'Dispatch blocking queue rate' do |c|
    c.formula='KPI::IDEN.dispatch_blocking_queue_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Average  dispatch  call  delay' do |c|
    c.formula='KPI::IDEN.average_dispatch_call_delay'
    c.thresholds do |th|
      th.alert>2
    end
    c.aggregation=:average
  end
  t.column 'Interconnect  attempts' do |c|
    c.formula='KPI::IDEN.interconnect_attempts'
    c.aggregation=:average
  end
  t.column 'Interconnect  blocked  queue  call  rate' do |c|
    c.formula='KPI::IDEN.interconnect_blocked_queue_call_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Interconnect call setup blocking clear rate' do |c|
    c.formula='KPI::IDEN.interconnect_call_setup_blocking_clear_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Total  handover  inquiry blocking  clear  rate' do |c|
    c.formula='KPI::IDEN.total_handover_inquiry_blocking_clear_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Interconnect Resource Request Blocking Clear Rate' do |c|
    c.formula='KPI::IDEN.intr_rsrc_req_blk_clr_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Total Dispatch Minutes of use' do |c|
    c.formula='KPI::IDEN.total_disp_mou'
    c.aggregation=:average
  end
  t.column 'Total Handover Resource Blocking Clear Rate' do |c|
    c.formula='KPI::IDEN.total_hnvr_rsrc_blk_clr_rate'
    c.thresholds do |th|
      th.alert>10
    end
    c.aggregation=:average
  end
  t.column 'Total Handover Threshold Blocking Clear Rate' do |c|
    c.formula='KPI::IDEN.total_hnvr_thres_blk_clr_rate'
    c.thresholds do |th|
      th.alert>10
    end
    c.aggregation=:average
  end
  t.column 'Total Interconnect Blocked Queue Call Rate' do |c|
    c.formula='KPI::IDEN.total_intc_blk_que_rate'
    c.thresholds do |th|
      th.alert>5
    end
    c.aggregation=:average
  end
  t.column 'Total successful interconnect call setups' do |c|
    c.formula='KPI::IDEN.total_successful_intc_call_setups'
    c.aggregation=:average
  end
end