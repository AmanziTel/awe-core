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
      !event_name.nil? and event_name=="Call"? 1:0
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