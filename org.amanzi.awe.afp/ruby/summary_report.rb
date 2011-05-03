# parameter: dataset_name
# parameter: plan_name

include NodeUtils

def format(value)
  (value*10000).round/10000.0
end

report "AFP summary report for #{dataset_name}:#{plan_name}\n" do
  ds_root=dataset(dataset_name,"network")
  frq_root=find_first(ds_root,{"name"=>plan_name},:FREQUENCY_ROOT)
  impact=find_first(frq_root,{"type"=>"root_proxy","node2node"=>"IMPACT"},:SOURCE)
  text "Number of Sectors in Optimization set: #{impact["sectors_opt_set"]}"
  text "Number of TRXs in Optimization set: #{impact["trx_opt_set"]}"
  text "Number of Sectors in Surrounding set: ?"
  text "Number of TRXs in Surrounding set: ?"

  n=impact["names"].size
  co_total=impact["co total details"]
  adj_total=impact["adj total details"]
  violations=impact["violations"]
  violations_total=0
  table "Violated Constraints Break-Down:",:width=>500,:height=>100 do |t|
    t.headers(["Source",   "# Violations",  "Violated Co",   "Violated Adj",  "Total Impact"])
    t.row ["Co-Site", violations[n],format(co_total[n]),format(adj_total[n]),format(co_total[n]+adj_total[n])]
    t.row ["Co-Sector", violations[n+1],format(co_total[n+1]),format(adj_total[n+1]),format(co_total[n+1]+adj_total[n+1])]
    impact["names"].each_with_index do |name,i|
      violations_total=violations_total+violations[i]
      t.row [name, violations[i],format(co_total[i]),format(adj_total[i]),format(co_total[i]+adj_total[i])]
    end
    t.row ["Total", violations_total,format(impact["co total"]),format(impact["adj total"]),format(impact["co total"]+impact["adj total"])]
  end

end