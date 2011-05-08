module KPI
  module Digitel
    module Drop
      include Annotations
      def Drop.t_dr_ss_bl_avg(data)
        data[:t_dr_ss_bl]
      end
      
      def Drop.t_dr_ss_ul_avg(data)
        data[:t_dr_ss_ul]
      end
      def Drop.t_dr_ss_dl_avg(data)
        data[:t_dr_ss_dl]
      end
      def Drop.t_dr_bq_bl_avg(data)
        data[:t_dr_bq_bl]
      end
      def Drop.t_dr_bq_ul_avg(data)
        data[:t_dr_bq_ul]
      end
      def Drop.t_dr_bq_dl_avg(data)
        data[:t_dr_bq_dl]
      end
      def Drop.t_dr_s_avg(data)
        data[:t_dr_s]
      end
      def Drop.t_dr_ta_avg(data)
        data[:t_dr_ta]
      end
      def Drop.t_dr_oth_avg(data)
        data[:t_dr_oth]
      end
      def Drop.t_dr_sud_avg(data)
        data[:t_dr_sud]
      end
      
      
    end
    module ICH
      include Annotations
      def ICH.ich_1(data)
        data[:ich_1]
      end
      def ICH.ich_2(data)
        data[:ich_2]
      end
      def ICH.ich_3(data)
        data[:ich_3]
      end
      def ICH.ich_4(data)
        data[:ich_4]
      end
      def ICH.ich_5(data)
        data[:ich_5]
      end
    end
    module CNDROP
      include Annotations
      def CNDROP.cndrop(data)
        data[:cndrop]
      end
      def CNDROP.cnrelcong(data)
        data[:cnrelcong]
      end
      def CNDROP.cdista(data)
        data[:cdista]
      end
      def CNDROP.cdisqa(data)
        data[:cdisqa]
      end
      def CNDROP.cdisss(data)
        data[:cdisss]
      end
    end
    module INCOMING_HO
      include Annotations
      def INCOMING_HO.hi_lost(data)
        data[:hi_lost]
      end
      def INCOMING_HO.hi_rev(data)
        data[:hi_rev]
      end
      def INCOMING_HO.hi_suc(data)
        data[:hi_suc]
      end
    end
    module OUTGOING_HO
      include Annotations
      def OUTGOING_HO.ho_lost(data)
        data[:ho_lost]
      end
      def OUTGOING_HO.ho_rev(data)
        data[:ho_rev]
      end
      def OUTGOING_HO.ho_suc(data)
        data[:ho_suc]
      end
    end
    module CSSR
      include Annotations
      def CSSR.t_as_suc(data)
        data[:t_as_suc]
      end
      def CSSR.cssr(data)
        data[:cssr]
      end
      def CSSR.sd_suc(data)
        data[:sd_suc]
      end
    end
    module SDROP
      include Annotations
      def SDROP.s_dr_ss(data)
        data[:s_dr_ss]
      end
      def SDROP.s_dr_bq(data)
        data[:s_dr_bq]
      end
      def SDROP.s_dr_ta(data)
        data[:s_dr_ta]
      end
      def SDROP.s_dr_oth(data)
        data[:s_dr_oth]
      end
      def SDROP.s_dr_c(data)
        data[:s_dr_c]
      end
      def SDROP.s_dr_c_ntc(data)
        data[:s_dr_c_ntc]
      end
    end
    module CONG
      include Annotations
      def CONG.tfnrelcong(data)
        data[:tfnrelcong]
      end
      def CONG.cnrelcong(data)
        data[:cnrelcong]
      end
      def CONG.t_cong(data)
        data[:t_cong]
      end
      def CONG.s_cong(data)
        data[:s_cong]
      end
      def CONG.t_congold(data)
        data[:t_congold]
      end
    end
    module Availability
      include Annotations
      def Availability.t_avail(data)
        data[:t_avail]
      end
      def Availability.t_dwn(data)
        data[:t_dwn]
      end
    end
  end
end