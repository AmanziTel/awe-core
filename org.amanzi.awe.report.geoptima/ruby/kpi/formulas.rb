module KPI
  module Geoptima
    include Annotations
    annotation :name=>'signal strength'
    def Geoptima.signal_strength(data)
      data[:signal_strength]
    end
    
    annotation :name=>'browser events', :function=>"sum"
    
    def Geoptima.browser_events(data)
      event_name=data[:event_name]
      !event_name.nil? and event_name=="browser"? 1:0
    end
    annotation :name=>'call events', :function=>"sum"
    
    def Geoptima.call_events(data)
      event_name=data[:event_name]
      !event_name.nil? and event_name=="call"? 1:0
    end
    
    annotation :name=>'call in progress', :function=>"sum"
    def Geoptima.call_in_progress(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="call in progress"? 1:0
    end
    
    annotation :name=>'idle', :function=>"sum"
    def Geoptima.idle(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="idle"? 1:0
    end
    annotation :name=>'ringing', :function=>"sum"
    def Geoptima.ringing(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="ringing"? 1:0
    end
    
    annotation :name=>'call ended', :function=>"sum"
    def Geoptima.call_ended(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="call ended"? 1:0
    end
    
    annotation :name=>'missed call', :function=>"sum"
    def Geoptima.missed_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="missed call"? 1:0
    end
    
    annotation :name=>'mobile originated call', :function=>"sum"
    def Geoptima.mobile_originated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="mobile originated call"? 1:0
    end
    
    annotation :name=>'mobile terminated call', :function=>"sum"
    def Geoptima.mobile_terminated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="mobile terminated call"? 1:0
    end
    
    annotation :name=>'mt call connected', :function=>"sum"
    def Geoptima.mt_call_connected(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status=="mt call connected"? 1:0
    end
    
    annotation :name=>'GPS events', :function=>"sum"
    
    def Geoptima.gps_events(data)
      event_name=data[:event_name]
      !event_name.nil? and event_name=="gps"? 1:0
    end
    annotation :name=>'signal events', :function=>"sum"
    
    def Geoptima.signal_events(data)
      event_name=data[:event_name]
      !event_name.nil? and event_name=="signal"? 1:0
    end
  end
end