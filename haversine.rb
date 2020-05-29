#!/usr/bin/env ruby -slap
require 'haversine'
require 'bigdecimal'
require 'bigdecimal/util'
Signal.trap("SIGPIPE", "SYSTEM_DEFAULT") 
STDOUT.sync = true

BEGIN { last = nil ; last_walk = -1 }

walk = $F[0]

if !last
  last = {lat: $F[1].to_f, lon: $F[2].to_f}
end

lat = $F[1].to_f
lon = $F[2].to_f

haversine = Haversine.distance(lat, lon, last[:lat], last[:lon]) 
last = {lat: $F[1].to_f, lon: $F[2].to_f}
if last_walk != walk
  $_ = "#{walk} 0"
        last_walk = walk
else
  $_ = "#{walk} " + sprintf("%.2f", haversine.to_ft)
end
