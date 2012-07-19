module Annotations
 def self.included(mod)
#   puts "included #{mod}"
   mod.extend(self)
   KPI.module_eval {include mod}
   mod.class_eval { 
    
   def self.method_added(meth, *args)
      module_function meth
    end
    def self.singleton_method_added(meth, *args)
      exclude=[:singleton_method_added,:get_annotation,:annotation]
      return if exclude.include? meth
      #~ puts "singleton #{meth} added"
      @@annotations||={}
      @@annotated=false unless defined? @@annotated
      @@annotations[meth]=@@last_annotation if @@annotated
      @@annotated=false
#      puts "singleton #{meth} added, annotations: #{self.get_annotation(meth)}"
    end
    def self.get_annotation(method)
      @@annotations||={}
      @@annotations[method]
    end

    def self.annotation(hash)
      @@last_annotation=hash
      @@annotated=true
    end
   }
 end
  def self.hidden_methods
    ["hide_method","annotation","get_annotation","singleton_method_added","method_added"]
  end
end
