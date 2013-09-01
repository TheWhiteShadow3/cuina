class Foo
	include cuina.map.MapEvent
	attr_accessor :a
	
	def run(trigger, map, this, other)
		puts(trigger)
		a = 2 * -3
		
		case map
			when 1: puts "1"
			when 2: puts "2"
			else puts "other"
		end
		
		if a > 0
			puts "größer"
		elsif a < 0
			puts "kleiner"
		else
			puts "gleich"
		end
	end
	
	# Ein Testblock
	def Ein
		My.Maid.start(Game.new)
		
		function(this) { |a|
			puts a
		}
		
		while a == true
			puts "Hallo"
		end
		return if a > 0
		a |= if true then break end
	end
	
	def self.function str, puff
		return str("test").size + 1
	end
end