module KPI
  module Geoptima
    include Annotations
    annotation :name=>'Signal  strength'
    def Geoptima.signal_strength(data)
      data[:signal_strength]
    end

    annotation :name=>'browser event', :function=>"sum"

    def Geoptima.browser_event(data)
      data[:browser_url].nil? ? 0:1
    end
  end
end