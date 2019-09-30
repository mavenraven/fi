require 'haversine'
Signal.trap("SIGPIPE", "SYSTEM_DEFAULT") 
BEGIN { last = nil }

fields = $_.split

lat_field = ENV["LAT#{n}_FIELD"].to_i
lon_field = ENV["LON#{n}_FIELD"].to_i

if !last
  last = {lat: fields[lat_field}, lon: fields[lon_field]}
end

haversine = Haversine.distance(fields[lat_field], fields[lon_field], last[:lat], last[:lon]) 
last = {lat: fields[lat_field}, lon: fields[lon_field]}
puts "#{$_.strip}#{$;.source}#{haversine}"
