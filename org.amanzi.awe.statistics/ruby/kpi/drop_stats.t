load 'statistics/digitel.rb'
template 'drop stats' do |t|
  t.metadata :type=>:oss
  t.column 't_dr_ss_bl' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_ss_bl_avg'
    c.aggregation=:average
  end
  t.column 't_dr_ss_ul' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_ss_ul_avg'
    c.aggregation=:average
  end
  t.column 't_dr_ss_dl' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_ss_dl_avg'
    c.aggregation=:average
  end
  t.column 't_dr_bq_bl' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_bq_bl_avg'
    c.aggregation=:average
  end
  t.column 't_dr_bq_ul' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_bq_ul_avg'
    c.aggregation=:average
  end
  t.column 't_dr_bq_dl' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_bq_dl_avg'
    c.aggregation=:average
  end
  t.column 't_dr_s' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_s_avg'
    c.aggregation=:average
  end
  t.column 't_dr_ta' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_ta_avg'
    c.aggregation=:average
  end
  t.column 't_dr_oth' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_oth_avg'
    c.aggregation=:average
  end
  t.column 't_dr_sud' do |c|
    c.formula='KPI::Digitel::Drop.t_dr_sud_avg'
    c.aggregation=:average
  end
  
  
  
end