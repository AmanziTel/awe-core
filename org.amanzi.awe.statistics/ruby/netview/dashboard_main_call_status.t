load 'netview/dashboards_formulas.rb'
template 'Dashboards templates' do |t|
  t.metadata :type=>:dataset,:drive_type=>:geoptima
	  t.column 'mobile originated call' do |c|
	    c.formula='KPI::Dashboards.mobile_originated_call'
	    c.aggregation=:sum
	  end
	 
	  t.column 'mobile terminated call' do |c|
	    c.formula='KPI::Dashboards.mobile_terminated_call'
	    c.aggregation=:sum
	  end
	  
	    t.column 'sms sent' do |c|
	    c.formula='KPI::Dashboards.sms_sent'
	    c.aggregation=:sum
	  end
	  
	    t.column 'sms received' do |c|
	    c.formula='KPI::Dashboards.sms_received'
	    c.aggregation=:sum
	  end
	  
	    t.column 'mms received' do |c|
	    c.formula='KPI::Dashboards.mms_received'
	    c.aggregation=:sum
	  end
	  
	    t.column 'mms sent' do |c|
	    c.formula='KPI::Dashboards.mms_sent'
	    c.aggregation=:sum
	  end
	  
	    t.column 'connected' do |c|
	    c.formula='KPI::Dashboards.data_status_connected'
	    c.aggregation=:sum
	  end
	  
	    t.column 'disconnected' do |c|
	    c.formula='KPI::Dashboards.data_status_disconnected'
	    c.aggregation=:sum
	  end
	  
	    t.column 'suspended' do |c|
	    c.formula='KPI::Dashboards.data_status_suspended'
	    c.aggregation=:sum
	  end
	  
	    t.column 'traffic download' do |c|
	    c.formula='KPI::Dashboards.traffic_download'
	    c.aggregation=:average
	  end
	  
	    t.column 'traffic upload' do |c|
	    c.formula='KPI::Dashboards.traffic_upload'
	    c.aggregation=:average
	  end
end
