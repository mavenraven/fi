require 'chronic'
Signal.trap("SIGPIPE", "SYSTEM_DEFAULT") 
BEGIN { last = nil }

fields = $_.split
field_number = ENV['TIME_FN'].to_i

if !last
  last = Chronic.parse(fields[field_number])
end

difference = Chronic.parse(fields[field_number]) - last
last = Chronic.parse(fields[field_number])
puts "#{$_.strip}#{$;.source}#{difference}"
