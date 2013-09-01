# Yuri-Script-Libary

puts "Yuri-libary imported"

module Yuri
	@@level = "default"

	def self.level=(value)
		@@level = value
	end
	
	def self.level
		return @@level
	end
	
	def self.kiss(m1, m2)
		puts "chuu"
	end
end

class Maid
	attr_reader :name
	
	def initialize(name)
		@name = name
	end
end