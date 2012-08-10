module KPI
  module Geoptima
    include Annotations
    ["Desire HD","GT-I8150","GT-55830","GT-I9100","GT-I9210","GT-N7000","Galaxy Nexus","HTC Desire HD A9191"].each do |e|
     self.module_eval %Q{
      def Geoptima.model_#{e.downcase.gsub(/\s/,"_")}(data)
        model=data[:model]
        !model.nil? and model=e ? 1 :nil
      end
      }
    end
  end
end