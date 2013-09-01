# Message-Script-Libary

puts "Message-libary imported"

module Message
	include_package "cuina.message"

	MESSAGE_KEY = "Message"

	def self.show_message(text)
		SceneContext[MESSAGE_KEY].showMessage(text)
	end
	
	def self.show_choice(a, b, c, d)
		SceneContext[MESSAGE_KEY].showChoice(a, b, c, d);
	end
	
	def self.set_name(name)
		SceneContext[MESSAGE_KEY].setName(name)
	end
	
	def self.set_face(face)
		SceneContext[MESSAGE_KEY].setFaceImage(face)
	end
end
