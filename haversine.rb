require 'haversine'
require 'bigdecimal'
require 'bigdecimal/util'

Signal.trap("SIGPIPE", "SYSTEM_DEFAULT") 
BEGIN { last = nil ; last_walk = 0 }

fields = $_.split

lat_field = ENV["LAT_FN"].to_i
lon_field = ENV["LON_FN"].to_i
walk_number_field = ENV["WALK_FN"].to_i

walk = fields[walk_number_field]

if !last
  last = {lat: fields[lat_field].to_d, lon: fields[lon_field].to_d}
end

lat = fields[lat_field].to_d
lon = fields[lon_field].to_d

haversine = Haversine.distance(lat, lon, last[:lat], last[:lon]) 
last = {lat: fields[lat_field].to_d, lon: fields[lon_field].to_d}
if last_walk != walk
	puts "#{$_.strip}#{$;.source}0"
else
	puts "#{$_.strip}#{$;.source}#{haversine.to_mi}"
end
