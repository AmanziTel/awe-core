module KPI
  module Tems
    include Annotations

    annotation :name=>'No. of samples'
    def Tems.no_samples(data)
      1
    end
    annotation :name=>'No. of events'
    def Tems.no_events(data)
      !data[:event].nil? ? 1:nil
    end
    annotation :name=>'Call Attempt'
    def Tems.call_attempt(data)
      event=data[:event]
      !event.nil? and event=="Call Attempt"? 1:nil
    end
    annotation :name=>'Call End'

    def Tems.call_end(data)
      event=data[:event]
      !event.nil? and event=="Call End"? 1:nil
    end
    annotation :name=>'Call Setup'

    def Tems.call_setup(data)
      event=data[:event]
      !event.nil? and event=="Call Setup"? 1:nil
    end
    annotation :name=>'Call Established'

    def Tems.call_established(data)
      event=data[:event]
      !event.nil? and event=="Call Established"? 1:nil
    end
    annotation :name=>'Dedicated Mode'

    def Tems.dedicated_mode(data)
      event=data[:event]
      !event.nil? and event=="Dedicated Mode"? 1:nil
    end
    annotation :name=>'Handover'

    def Tems.handover(data)
      event=data[:event]
      !event.nil? and event=="Handover"? 1:nil
    end
    annotation :name=>'Handover Failure'

    def Tems.handover_failure(data)
      event=data[:event]
      !event.nil? and event=="Handover Failure"? 1:nil
    end
    annotation :name=>'Handover Intracell'

    def Tems.handover_intracell(data)
      event=data[:event]
      !event.nil? and event=="Handover Intracell"? 1:nil
    end
    annotation :name=>'Idle Mode'

    def Tems.idle_mode(data)
      event=data[:event]
      !event.nil? and event=="Idle Mode"? 1:nil
    end

  end
end