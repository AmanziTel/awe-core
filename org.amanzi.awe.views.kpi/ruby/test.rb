module KPI
  module Default
    module Test
      module Group1
        def Group1.kpi_network(network)
          []
        end

        def Group1.kpi_drive(drive)
          []
        end

        def Group1.kpi_counters(counters)
          []
        end
      end

      module Group2
        def Group2.kpi_network_drive_counters(network,drive,counters)
          []
        end
      end
    end
  end
end