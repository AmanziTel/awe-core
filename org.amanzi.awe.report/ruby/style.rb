require 'java'

include_class java.awt.Color

class ColorScheme
  attr_accessor :colors
  def initialize(name)
    @name=name
    @colors=[]
  end

  def add(red,green,blue)
    @colors<<java.awt.Color.new(red,green,blue)
  end

  def setup(&block)
    self.instance_eval &block
    self
  end
end

class Styles
  attr_accessor :styles
  def initialize(name)
    @name=name
    @styles=Hash.new
  end
  def setup(&block)
    self.instance_eval &block
    self
  end
    
  def add(shape, color_scheme)
    style=Java::org.amanzi.awe.neostyle.ShapeType.value_of(shape.to_s.upcase)
    @styles[style]=color_scheme
  end

  def color_scheme(name="default",&block)
    ColorScheme.new(name).setup(&block)
  end
end

def styles(name,&block)
  Styles.new(name).setup(&block)
end

def color_scheme(name="default",&block)
    ColorScheme.new(name).setup(&block)
end