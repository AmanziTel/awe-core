module KPI
  module Dashboards
    include Annotations

    annotation :name=>'mobile originated call', :function=>"sum"
    def Dashboards.mobile_originated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="mobile originated call"? 1:nil
    end

    annotation :name=>'mobile terminated call', :function=>"sum"

    def Dashboards.mobile_terminated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="mobile terminated call"? 1:nil
    end

    annotation :name=>'sms sent', :function=>"sum"

    def Dashboards.sms_sent(data)
      sms_status=data[:sms_status]
      !sms_status.nil? and sms_status.downcase=="sent"? 1:nil
    end

    annotation :name=>'sms received', :function=>"sum"

    def Dashboards.sms_received(data)
      sms_status=data[:sms_status]
      !sms_status.nil? and sms_status.downcase=="received"? 1:nil
    end

    annotation :name=>'mms received', :function=>"sum"

    def Dashboards.mms_received(data)
      mms_status=data[:mms_status]
      !mms_status.nil? and mms_status.downcase=="received"? 1:nil
    end

    annotation :name=>'mms sent', :function=>"sum"

    def Dashboards.mms_sent(data)
      mms_status=data[:mms_status]
      !mms_status.nil? and mms_status.downcase=="sent"? 1:nil
    end

    annotation :name=>'connected', :function=>"sum"

    def Dashboards.data_status_connected(data)
      data_status=data[:data_status]
      !data_status.nil? and data_status.downcase=="connected"? 1:nil
    end

    annotation :name=>'disconnected', :function=>"sum"

    def Dashboards.data_status_disconnected(data)
      data_status=data[:data_status]
      !data_status.nil? and data_status.downcase=="disconnected"? 1:nil
    end

    annotation :name=>'suspended', :function=>"sum"

    def Dashboards.data_status_suspended(data)
      data_status=data[:data_status]
      !data_status.nil? and data_status.downcase=="suspended"? 1:nil
    end

    annotation :name=>'traffic upload', :function=>"sum"

    def Dashboards.traffic_upload(data)
      speed=data[:trafficspeed]||data[:trafficspeed_speed]
      direction=data[:trafficspeeddirection]||data[:trafficspeed_direction]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !speed.nil? and !direction.nil? and direction=="UPLOAD" and interface=="MOBILE"? speed :nil
    end

    annotation :name=>'traffic download', :function=>"sum"

    def Dashboards.traffic_download(data)
      speed=data[:trafficspeed]||data[:trafficspeed_speed]
      direction=data[:trafficspeeddirection]||data[:trafficspeed_direction]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !speed.nil? and !direction.nil? and direction=="DOWNLOAD" and interface=="MOBILE" ? speed :nil
    end

    annotation :name=>'delay wifi'

    def Dashboards.delay_wifi(data)
      delay=data[:trafficspeeddelay]||data[:trafficspeed_delay]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !delay.nil? and interface=='WIFI'? delay :nil
    end

    annotation :name=>'speed wifi'

    def Dashboards.speed_wifi(data)
      throughput=data[:trafficspeed]||data[:trafficspeed_speed]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !throughput.nil? and interface=='WIFI'? throughput :nil
    end

    annotation :name=>'delay wifi-mobile'

    def Dashboards.delay_wifi_mobile(data)
      delay=data[:trafficspeeddelay]||data[:trafficspeed_delay]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !delay.nil? and interface=='WIFI/MOBILE'? delay :nil
    end

    annotation :name=>'speed wifi-mobile'

    def Dashboards.speed_wifi_mobile(data)
      throughput=data[:trafficspeed]||data[:trafficspeed_speed]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !throughput.nil? and interface=='WIFI/MOBILE'? throughput :nil
    end

    annotation :name=>'delay mobile'

    def Dashboards.delay_mobile(data)
      delay=data[:trafficspeeddelay]||data[:trafficspeed_delay]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !delay.nil? and interface=='MOBILE'? delay :nil
    end

    annotation :name=>'speed mobile'

    def Dashboards.speed_mobile(data)
      throughput=data[:trafficspeed]||data[:trafficspeed_speed]
      interface=data[:trafficspeedinterface]||data[:trafficspeed_interface]
      interface=interface.nil? ?nil:interface.upcase
      !throughput.nil? and interface=='MOBILE'? throughput :nil
    end

    annotation :name=>'delay all'

    def Dashboards.delay_all(data)
      delay=data[:trafficspeeddelay]||data[:trafficspeed_delay]
      ti = data[:trafficspeedinterface]||data[:trafficspeed_interface]
      ti = ti.nil? ? nil:ti.upcase
      !delay.nil? and (ti=='MOBILE' or ti=='WIFI' or ti=='WIFI/MOBILE')? delay :nil
    end

    annotation :name=>'speed all'

    def Dashboards.speed_all(data)
      throughput=data[:trafficspeed]||data[:trafficspeed_speed]
      ti = data[:trafficspeedinterface]||data[:trafficspeed_interface]
      ti = ti.nil? ? nil:ti.upcase
      !throughput.nil? and (ti=='MOBILE' or ti=='WIFI' or ti=='WIFI/MOBILE')? throughput :nil
    end

    annotation :name=>'data upload mobile'

    def Dashboards.tx_count_mobile(data)
      txBytes=data[:trafficcounttxbytes]||data[:trafficcount_txbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !txBytes.nil? and interface=='MOBILE'? txBytes/1048576 :nil
    end

    annotation :name=>'data upload wifi'

    def Dashboards.tx_count_wifi(data)
      txBytes=data[:trafficcounttxbytes]||data[:trafficcount_txbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !txBytes.nil? and interface=='WIFI'? txBytes/1048576 :nil
    end

    annotation :name=>'data upload wifi-mobile'

    def Dashboards.tx_count_wifi_mobile(data)
      txBytes=data[:trafficcounttxbytes]||data[:trafficcount_txbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !txBytes.nil? and interface=='WIFI/MOBILE'? txBytes/1048576 :nil
    end

    annotation :name=>'data upload all'

    def Dashboards.tx_count_all(data)
      txBytes=data[:trafficcounttxbytes]||data[:trafficcount_txbytes]
      ti = data[:trafficcountinterface]||data[:trafficcount_interface]
      ti = ti.nil? ? nil:ti.upcase
      !txBytes.nil? and (ti=='MOBILE' or ti=='WIFI' or ti=='WIFI/MOBILE')? txBytes/1048576 :nil
    end

    annotation :name=>'data download mobile'

    def Dashboards.rx_count_mobile(data)
      rxBytes=data[:trafficcountrxbytes]||data[:trafficcount_rxbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !rxBytes.nil? and interface=='MOBILE'? rxBytes/1048576 :nil
    end

    annotation :name=>'data download wifi'

    def Dashboards.rx_count_wifi(data)
      rxBytes=data[:trafficcountrxbytes]||data[:trafficcount_rxbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !rxBytes.nil? and interface=='WIFI'? rxBytes/1048576 :nil
    end

    annotation :name=>'data download wifi-mobile'

    def Dashboards.rx_count_wifi_mobile(data)
      rxBytes=data[:trafficcountrxbytes]||data[:trafficcount_rxbytes]
      interface=data[:trafficcountinterface]||data[:trafficcount_interface]
      interface=interface.nil? ?nil:interface.upcase
      !rxBytes.nil? and interface=='WIFI/MOBILE'? rxBytes/1048576 :nil
    end

    annotation :name=>'data download all'

    def Dashboards.rx_count_all(data)
      rxBytes=data[:trafficcountrxbytes]||data[:trafficcount_rxbytes]
      ti = data[:trafficcountinterface]||data[:trafficcount_interface]
      ti = ti.nil? ? nil:ti.upcase
      !rxBytes.nil? and (ti=='MOBILE' or ti=='WIFI' or ti=='WIFI/MOBILE')? rxBytes/1048576 :nil
    end

    annotation :name=>'application', :function=>"sum"

    def Dashboards.application(data)
      appname=data[:appname]||data[:runningapps_appname]
      state=data[:appstate]||data[:runningapps_state]
      !appname.nil? and state=="STARTED"? 1:nil
    end

    annotation :name=>'browser url', :function=>"sum"

    def Dashboards.browser_url(data)
      url=data[:browser_url]
      count=data[:browser_visits]||data[:browser_num_visits]
      !url.nil? and !count.nil? ? count:nil
    end

  end
end
