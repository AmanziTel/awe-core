cs_rxlev=color_scheme do
  add "color1", 102,102,102
  add "color2", 255,0,255
  add "color3", 0,0,204
  add "color4", 51,255,0
  add "color5", 255,255,0
  add "color6", 255,0,0
end

puts "cs_rxlev #{cs_rxlev}"
cs_bcch= color_scheme do
  add "color1", 255,0,204
  add "color1", 102,102,102
  add "color1", 51,51,255
  add "color1", 0,204,204
  add "color1", 255,153,153
  add "color1", 255,0,0
  add "color1", 153,255,204
  add "color1", 0,102,153
  add "color1", 255,255,0
  add "color1", 153,0,255
  add "color1", 102,102,0
  add "color1", 0,204,0
  add "color1", 255,153,0
  add "color1", 153,153,255
  end
puts "cs_rxlev #{cs_bcch}"
cs_bcch_text= color_scheme do
  add "blue", 0,0,204
end