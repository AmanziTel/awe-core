report "#{Property}" do |r|
  r.map "Drive map", :map => $map, :width => 500, :height => 400 do |m|
    layer = m.layers.find(:type => 'drive').first
    layer.filter=Group_filter
    layer.style=Style
  end
end