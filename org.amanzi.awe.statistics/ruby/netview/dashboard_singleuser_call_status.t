load 'netview/dashboards_formulas.rb'
template 'Dashboard single user template' do |t|
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
	  
	    
	  
	  t.column 'delay wifi' do |c|
    	c.formula='KPI::Dashboards.delay_wifi'
    	c.aggregation=:average
  	  end
  	    	  
  	  t.column 'speed wifi' do |c|
    	c.formula='KPI::Dashboards.speed_wifi'
    	c.aggregation=:average
  	  end
  	  
  	  t.column 'delay wifi-mobile' do |c|
    	c.formula='KPI::Dashboards.delay_wifi_mobile'
    	c.aggregation=:average
  	  end
  	    	  
  	  t.column 'speed wifi-mobile' do |c|
    	c.formula='KPI::Dashboards.speed_wifi_mobile'
    	c.aggregation=:average
  	  end
  	  
  	  t.column 'delay all' do |c|
    	c.formula='KPI::Dashboards.delay_all'
    	c.aggregation=:average
  	  end
  	    	  
  	  t.column 'speed all' do |c|
    	c.formula='KPI::Dashboards.speed_all'
    	c.aggregation=:average
  	  end
  	  
	  t.column 'delay mobile' do |c|
    	c.formula='KPI::Dashboards.delay_mobile'
    	c.aggregation=:average
  	  end
  	  
	  t.column 'speed mobile' do |c|
    	c.formula='KPI::Dashboards.speed_mobile'
    	c.aggregation=:average
  	  end
  	  
  	  
  	  t.column 'data upload mobile' do |c|
    	c.formula='KPI::Dashboards.tx_count_mobile'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data upload wifi' do |c|
    	c.formula='KPI::Dashboards.tx_count_wifi'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data upload wifi-mobile' do |c|
    	c.formula='KPI::Dashboards.tx_count_wifi_mobile'
    	c.aggregation=:sum
  	  end
  	  
  	   t.column 'data upload all' do |c|
    	c.formula='KPI::Dashboards.tx_count_all'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data download mobile' do |c|
    	c.formula='KPI::Dashboards.rx_count_mobile'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data download wifi' do |c|
    	c.formula='KPI::Dashboards.rx_count_wifi'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data download wifi-mobile' do |c|
    	c.formula='KPI::Dashboards.rx_count_wifi_mobile'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'data download all' do |c|
    	c.formula='KPI::Dashboards.rx_count_all'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'application' do |c|
    	c.formula='KPI::Dashboards.application'
    	c.aggregation=:sum
  	  end
  	  
  	  t.column 'browser url' do |c|
    	c.formula='KPI::Dashboards.browser_url'
    	c.aggregation=:sum
  	  end
end
