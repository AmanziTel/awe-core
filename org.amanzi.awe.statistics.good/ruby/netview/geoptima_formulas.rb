module KPI
  module Geoptima
    include Annotations
    annotation :name=>'No. of samples'
    def Geoptima.no_samples(data)
      1
    end
    annotation :name=>'signal_strength'

    def Geoptima.signal_strength(data)
      signal=data[:signal_strength]
      !signal.nil? and signal<0 and signal!=-256 ? signal :nil
    end
    annotation :name=>'no coverage'

    def Geoptima.no_coverage(data)
      signal=data[:signal_strength]
      !signal.nil? and signal==-256 ? 1 :nil
    end
    annotation :name=>'mode_signal_strength'

    def Geoptima.mode_signal_strength(data)
      signal=data[:modestrength]||data[:mode_strength]
      !signal.nil? and signal<0 ? signal :nil
    end
    annotation :name=>'Lost signal'

    def Geoptima.rssi_positive(data)
      signal=data[:signal_strength]
      !signal.nil? and signal>=0 ? 1 : nil
    end
    annotation :name=>'dominant', :function=>"sum"

    def Geoptima.dominant(data)
      is_dominant=data[:dominant]
      !is_dominant.nil? and is_dominant.to_s=='true' ? 1:nil
    end
    annotation :name=>'neighbour', :function=>"sum"

    def Geoptima.neighbour(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="neighbor_signal"? 1:nil
    end
    annotation :name=>'delta rssi'

    def Geoptima.delta_rssi(data)
      data[:delta_rssi]
    end
    annotation :name=>'browser events', :function=>"sum"

    def Geoptima.browser_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="browser"? 1:nil
    end
    annotation :name=>'call events', :function=>"sum"

    def Geoptima.call_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="call"? 1:nil
    end

    annotation :name=>'answering', :function=>"sum"

    def Geoptima.answering(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="answering"? 1:nil
    end
    annotation :name=>'connected', :function=>"sum"

    def Geoptima.connected(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="connected"? 1:nil
    end
    annotation :name=>'connecting', :function=>"sum"

    def Geoptima.connecting(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="connecting"? 1:nil
    end
    annotation :name=>'disconnecting', :function=>"sum"

    def Geoptima.disconnecting(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="disconnecting"? 1:nil
    end
    annotation :name=>'dialling', :function=>"sum"

    def Geoptima.dialling(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="dialling"? 1:nil
    end
    annotation :name=>'call in progress', :function=>"sum"

    def Geoptima.call_in_progress(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="call in progress"? 1:nil
    end

    annotation :name=>'idle', :function=>"sum"

    def Geoptima.idle(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="idle"? 1:nil
    end
    annotation :name=>'ringing', :function=>"sum"

    def Geoptima.ringing(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="ringing"? 1:nil
    end

    annotation :name=>'call ended', :function=>"sum"

    def Geoptima.call_ended(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="call ended"? 1:nil
    end

    annotation :name=>'missed call', :function=>"sum"

    def Geoptima.missed_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="missed call"? 1:nil
    end

    annotation :name=>'mobile originated call', :function=>"sum"

    def Geoptima.mobile_originated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="mobile originated call"? 1:nil
    end

    annotation :name=>'mobile terminated call', :function=>"sum"

    def Geoptima.mobile_terminated_call(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="mobile terminated call"? 1:nil
    end

    annotation :name=>'mt call connected', :function=>"sum"

    def Geoptima.mt_call_connected(data)
      call_status=data[:call_status]
      !call_status.nil? and call_status.downcase=="mt call connected"? 1:nil
    end

    annotation :name=>'GPS events', :function=>"sum"

    def Geoptima.gps_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="gps"? 1:nil
    end
    annotation :name=>'signal events', :function=>"sum"

    def Geoptima.signal_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="signal"? 1:nil
    end
    annotation :name=>'mode events', :function=>"sum"

    def Geoptima.mode_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and event_name=="mode"? 1:nil
    end
    annotation :name=>'mode & signal events', :function=>"sum"

    def Geoptima.mode_signal_events(data)
      event_name=data[:event_name]||data[:event]
      !event_name.nil? and (event_name=="mode" or event_name=="signal")? 1:nil
    end
    annotation :name=>'latency'

    def Geoptima.latency(data)
      delay=data[:trafficspeeddelay]||data[:trafficspeed_delay]
      !delay.nil? ? delay :nil
    end
    annotation :name=>'throughput'

    def Geoptima.throughput(data)
      throughput=data[:trafficspeed]||data[:trafficspeed_speed]
      !throughput.nil? ? throughput :nil
    end
    annotation :name=>'traffic_sent'

    def Geoptima.traffic_sent(data)
      traffic=data[:trafficcounttxbytes]||data[:trafficcount_txbytes]
      !traffic.nil? ? traffic :nil
    end
    annotation :name=>'traffic_received'

    def Geoptima.traffic_received(data)
      traffic=data[:trafficcountrxbytes]||data[:trafficcount_rxbytes]
      !traffic.nil? ? traffic :nil
    end
    annotation :name=>'Roundtrip.cold (ms)'

    def Geoptima.roundtrip_cold(data)
      roundtriptime=data[:roundtrip_roundtriptime]
      roundtriptype=data[:roundtrip_type]
      !roundtriptime.nil? and roundtriptype=="COLD" ? roundtriptime : nil
    end

    annotation :name=>'Roundtrip.warm (ms)'

    def Geoptima.roundtrip_warm(data)
      roundtriptime=data[:roundtrip_roundtriptime]
      roundtriptype=data[:roundtrip_type]
      !roundtriptime.nil? and roundtriptype=="WARM" ? roundtriptime : nil
    end
    annotation :name=>'httpRequest.facebook.delay (ms)'

    def Geoptima.httpRequest_facebook_delay(data)
      httprequest_delay=data[:httprequest_delay]
      httprequest_address=data[:httprequest_address]
      !httprequest_delay.nil? and httprequest_address=="www.facebook.com" ? httprequest_delay : nil
    end
    annotation :name=>'httpRequest.google.delay (ms)'

    def Geoptima.httpRequest_google_delay(data)
      httprequest_delay=data[:httprequest_delay]
      httprequest_address=data[:httprequest_address]
      !httprequest_delay.nil? and httprequest_address=="www.google.com" ? httprequest_delay : nil
    end
    annotation :name=>'httpRequest.yahoo.delay (ms)'

    def Geoptima.httpRequest_yahoo_delay(data)
      httprequest_delay=data[:httprequest_delay]
      httprequest_address=data[:httprequest_address]
      !httprequest_delay.nil? and httprequest_address=="www.yahoo.com" ? httprequest_delay : nil
    end
    annotation :name=>'httpRequest.youtube.delay (ms)'

    def Geoptima.httpRequest_youtube_delay(data)
      httprequest_delay=data[:httprequest_delay]
      httprequest_address=data[:httprequest_address]
      !httprequest_delay.nil? and httprequest_address=="www.youtube.com" ? httprequest_delay : nil
    end

    $MODELS=["Desire HD","NX-A899","GT-P1000","GT-S5360","GT-I8150","GT-55830","GT-I9100","GT-I9210","GT-N7000","Galaxy Nexus","HTC Desire HD A9191"]

    $MODELS.each_with_index do |e,i|
      Geoptima.module_eval %Q{
      def self.model_#{i}(data)
        model=data[:model]
        !model.nil? and model==\"#{e}\" ? 1 : nil
      end
      }
    end

    def Geoptima.model_no_value(data)
      model=data[:model]
      model.nil? ? 1 : nil
    end

  end
end