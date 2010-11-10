module KPI
  module IDEN
    include Annotations

    annotation :name=>'Dispatch  voice  call  attempts'
    def self.dispatch_voice_call_attempts(data)
      data[:dis_tch_requests]
    end

    annotation :name=>'DCCH Blocking Rate',:unit=>:percent, :threshold=>5

    def self.dcch_blk_rate(data)
      data[:tel_dcch_requests]==0?0.0:data[:tel_dcch_failed]*100.0/data[:tel_dcch_requests]
    end

    annotation :name=>'Dispatch blocking queue rate',:unit=>:percent, :threshold=>5

    def self.dispatch_blocking_queue_rate(data)
      data[:dis_tch_requests]==0?0.0:data[:dis_tch_queued]*100.0/data[:dis_tch_requests]
    end

    annotation :name=>'Average  dispatch  call  delay'

    def self.average_dispatch_call_delay(data)
      data[:dis_tch_requests]==0?0.0:data[:dis_queued_time_sum].quo(data[:dis_tch_requests])
    end

    annotation :name=>'Interconnect  attempts'

    def self.interconnect_attempts(data)
      (data[:tel_tch_requests]-(data[:ho_chan_allocated] + data[:ho_failed_rsrc] +
      data[:ho_failed_thres] + data[:ho_failed_cr_6]))+(data[:tel_tch_requests3]-
      (data[:ho_chan_allocated3] + data[:ho_failed_rsrc3] +
      data[:ho_failed_thres3] + data[:ho_failed_cr_3]))+
      (data[:tel_tch_requests_s3]-(data[:ho_chan_allocated_s3] + data[:ho_failed_rsrc_s3] +
      data[:ho_failed_thres_s3] + data[:ho_failed_cr_s3]))
    end
    annotation :name=>'Interconnect  blocked  queue  call  rate'

    def self.interconnect_blocked_queue_call_rate(data)
      a=(data[:tel_tch_queued]+data[:tel_tch_queued3]+data[:tel_tch_queued_s3])
      b=(data[:tel_tch_requests]-
      (data[:ho_failed_rsrc]+data[:ho_failed_thres]+
      data[:ho_chan_allocated]+data[:ho_failed_cr_6]))+
      (data[:tel_tch_requests3]-
      (data[:ho_failed_rsrc3]+data[:ho_failed_thres3]+
      data[:ho_chan_allocated3]+data[:ho_failed_cr_3]))+
      (data[:tel_tch_requests_s3]-
      (data[:ho_failed_rsrc_s3]+data[:ho_failed_thres_s3]+
      data[:ho_chan_allocated_s3]+data[:ho_failed_cr_s3]))
      b==0?0.0:a*100.0/b
    end
    annotation :name=>'Interconnect  call  setup blocking  clear  rate'

    def self.interconnect_call_setup_blocking_clear_rate(data)
      a=((data[:tel_tch_failed]-(data[:ho_failed_rsrc]+data[:ho_failed_thres]+
      data[:ho_failed_cr_6]))+
      (data[:tel_tch_failed3]-(data[:ho_failed_rsrc3]+data[:ho_failed_thres3]+
      data[:ho_failed_cr_3]))+
      (data[:tel_tch_failed_s3]-(data[:ho_failed_rsrc_s3]+data[:ho_failed_thres_s3]+
      data[:ho_failed_cr_s3])))
      b=(data[:tel_tch_requests]-
      (data[:ho_failed_rsrc]+data[:ho_failed_thres]+
      data[:ho_chan_allocated]+data[:ho_failed_cr_6]))+
      (data[:tel_tch_requests3]-
      (data[:ho_failed_rsrc3]+data[:ho_failed_thres3]+
      data[:ho_chan_allocated3]+data[:ho_failed_cr_3]))+
      (data[:tel_tch_requests_s3]-
      (data[:ho_failed_rsrc_s3]+data[:ho_failed_thres_s3]+
      data[:ho_chan_allocated_s3]+data[:ho_failed_cr_s3]))
      b==0?0.0:a*100.0/b
    end
    annotation :name=>'Total  handover  inquiry blocking  clear  rate'

    def self.total_handover_inquiry_blocking_clear_rate(data)
      a=data[:ho_failed_rsrc]+data[:ho_failed_rsrc3]+data[:ho_failed_rsrc_s3]+
      data[:ho_failed_thres]+data[:ho_failed_thres3]+data[:ho_failed_thres_s3]+
      data[:ho_failed_cr_6]+data[:ho_failed_cr_3]+data[:ho_failed_cr_s3]
      b=data[:ho_chan_allocated]+data[:ho_chan_allocated3]+data[:ho_chan_allocated_s3]+a
      b==0?0.0:a*100.0/b
    end
    annotation :name=>'Interconnect Resource Request Blocking Clear Rate'

    def self.intr_rsrc_req_blk_clr_rate(data)
      a=(data[:tel_tch_failed3]-data[:ho_failed_thres3])
      b=data[:tel_tch_requests3]-data[:ho_failed_thres3]
      b==0?0.0:a*100.0/b
    end

    annotation :name=>'Total Dispatch Minutes of use'

    def  self.total_disp_mou(data)
      data[:dis_i6_tch_c_secs].to_f/6000
    end

    annotation :name=>'Total Handover Resource Blocking Clear Rate'

    def  self.total_hnvr_rsrc_blk_clr_rate(data)
      a=data.ho_failed_rsrc + data.ho_failed_rsrc3 + data.ho_failed_rsrc_s3
      b= data[:ho_chan_allocated] + data[:ho_chan_allocated3] + data[:ho_chan_allocated_s3] +
      a +
      data[:ho_failed_thres] + data[:ho_failed_thres3] + data[:ho_failed_thres_s3]+
      data[:ho_failed_cr_6] + data[:ho_failed_cr_3] + data[:ho_failed_cr_s3]
      b==0?0.0:a*100.0/b
    end

    annotation :name=>'Total Handover Threshold Blocking Clear Rate'

    def self.total_hnvr_thres_blk_clr_rate(data)
      a= data[:ho_failed_thres] + data[:ho_failed_thres3] + data[:ho_failed_thres_s3]
      b= data[:ho_chan_allocated] + data[:ho_chan_allocated3] + data[:ho_chan_allocated_s3] +
      data[:ho_failed_rsrc] + data[:ho_failed_rsrc3] + data[:ho_failed_rsrc_s3] +
      a +
      data[:ho_failed_cr_6] + data[:ho_failed_cr_3] + data[:ho_failed_cr_s3]
      b==0?0.0:a*100.0/b
    end

    annotation :name=>'Total Interconnect Blocked Queue Call Rate'

    def self.total_intc_blk_que_rate(data)
      a= data[:tel_tch_queued] + data[:tel_tch_queued3]+ data[:tel_tch_queued_s3]
      b=(data[:tel_tch_requests]-
      (data[:ho_failed_rsrc]+data[:ho_failed_thres]+
      data[:ho_chan_allocated]+data[:ho_failed_cr_6]))+
      (data[:tel_tch_requests3]-
      (data[:ho_failed_rsrc3]+data[:ho_failed_thres3]+
      data[:ho_chan_allocated3]+data[:ho_failed_cr_3]))+
      (data[:tel_tch_requests_s3]-
      (data[:ho_failed_rsrc_s3]+data[:ho_failed_thres_s3]+
      data[:ho_chan_allocated_s3]+data[:ho_failed_cr_s3]))
      b==0?0.0:a*100.0/b
    end

    annotation :name=>'Total successful interconnect call setups'

    def self.total_successful_intc_call_setups(data)
      ( data[:tel_tch_hold6_sum] - data[:ho_chan_allocated] ) +
      ( data[:tel_tch_hold3_sum] - data[:ho_chan_allocated3] ) +
      ( data[:tel_tch_hold_s3_sum] - data[:ho_chan_allocated_s3] )
    end

  end
end

