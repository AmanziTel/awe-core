require 'formulas'
require 'search'
require 'Neo4j'

module KPI
  module Default
    module Ericsson
      module Accessibility
        def Accessibility.CSSR_SPEECH(counters)
            aggregation=counters.collect("type","moid",
            "pmTotNoRrcConnectReqCsSucc",
            "pmTotNoRrcConnectReqCs",
            "pmNoLoadSharingRrcConnCs",
            "pmNoRabEstablishSuccessSpeech",
            "pmNoRabEstablishAttemptSpeech",
            "pmNoDirRetryAtt").aggregate("moid")
            result=""
            aggregation.each do |obj,rows|
              result+=obj+": "
              formula=""
              rows.each do |row|
                formula="100.0*#{row['pmTotNoRrcConnectReqCsSucc']}/(#{row['pmTotNoRrcConnectReqCs']}-#{row['pmNoLoadSharingRrcConnCs']})*#{row['pmNoRabEstablishSuccessSpeech']}/(#{row['pmNoRabEstablishAttemptSpeech']}-#{row['pmNoDirRetryAtt']})"

                kpi=100.0*row['pmTotNoRrcConnectReqCsSucc']/
                (row['pmTotNoRrcConnectReqCs']-row['pmNoLoadSharingRrcConnCs'])*
                row['pmNoRabEstablishSuccessSpeech']/
                (row['pmNoRabEstablishAttemptSpeech']-row['pmNoDirRetryAtt'])
                result+=kpi.to_s+"\n#{formula}\n"
              end
            end
            result
        end

        def Accessibility.CSSR_CS57
          "Accessibility.CSSR_CS57 called"
        end
      end

      module Retainability
        def Retainability.RRC_DROPRATE_DCH
          "Retainability.RRC_DROPRATE_DCH called"
        end

        def Retainability.RRC_DROPRATE_FACH
          "Retainability.RRC_DROPRATE_FACH called"
        end
      end
    end
  end
end