module KPI
  module Geoptima
    include Annotations

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
