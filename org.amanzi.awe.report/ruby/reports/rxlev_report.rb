cs_rxlev=color_scheme do
  add "color1", 102,102,102
  add "color2", 255,0,255
  add "color3", 0,0,204
  add "color4", 51,255,0
  add "color5", 255,255,0
  add "color6", 255,0,0
end
cs_bcch_text= color_scheme do
  add "blue", 0,0,204
end
Style=styles "O2 style" do
  add :circle, cs_rxlev
  add :text, cs_bcch_text
end

Property="gsm_server_report_rxlev_full_dbm_2"

Group_filter=filters  Property do |f|
  f11=filter :condition=>:eq, :value=>-110
  f12=filter :condition=>:lt, :value=>-92
  add f11 & f12

  f21=filter :condition=>:ge, :value=>-92
  f22=filter :condition=>:lt, :value=>-87
  add f21 & f22

  f21=filter :condition=>:ge, :value=>-87
  f22=filter :condition=>:lt, :value=>-81
  add f21 & f22

  f21=filter :condition=>:ge, :value=>-81
  f22=filter :condition=>:lt, :value=>-75
  add f21 & f22

  f21=filter :condition=>:ge, :value=>-75
  f22=filter :condition=>:lt, :value=>-69
  add f21 & f22

  f21=filter :condition=>:ge, :value=>-69
  f22=filter :condition=>:lt, :value=>0
  add f21 & f22
end

$map=GIS.maps.first.copy

load "ruby/samples/map_template.rb"
#report "#{property}" do |r|
#  r.map "Drive map", :map => map, :width => 500, :height => 400 do |m|
#    layer = m.layers.find(:type => 'drive').first
#    layer.filter=group_filter
#    layer.style=style
#  end
#end