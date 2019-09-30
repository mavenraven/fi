require 'haversine'
require 'bigdecimal'
require 'bigdecimal/util'

Signal.trap("SIGPIPE", "SYSTEM_DEFAULT") 
BEGIN { last = nil ; last_walk = -1 }

fields = $_.split

lat_field = ENV["LAT_FN"].to_i
lon_field = ENV["LON_FN"].to_i
walk_number_field = ENV["WALK_FN"].to_i

walk = fields[walk_number_field]

if !last
  last = {lat: fields[lat_field].to_f, lon: fields[lon_field].to_f}
end

lat = fields[lat_field].to_f
lon = fields[lon_field].to_f

haversine = Haversine.distance(lat, lon, last[:lat], last[:lon]) 
last = {lat: fields[lat_field].to_f, lon: fields[lon_field].to_f}
if last_walk != walk
	puts "#{$_.strip}#{$;.source}0"
        last_walk = walk
else
	puts "#{$_.strip}#{$;.source}" + sprintf("%.2f", haversine.to_ft)
end
