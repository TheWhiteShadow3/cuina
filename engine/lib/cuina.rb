#file needs to be saved as UTF-8 without BOOM!
require './lib/basic.rb'
#require './lib/Particle.rb'
require './lib/Yuri.rb'
#java_import "java.lang.System"
#java_import "cuina.audio.Audio"
#java_import "cuina.script.Trigger"

module Cuina
	include_package 'cuina'
	include_package 'cuina.database'
	include_package 'cuina.plugin'
	include_package 'java.io'
	
	def self.new_game
		Game.newGame
	end
	
	def self.load_game(filename)
		Game.loadGame(new File(filename))
	end
	
	def self.save_game(filename)
		Game.loadGame(new File(filename))
	end
	
	def self.end_game
		Game.endGame
	end
	
	def self.get_title
		return Game.getTitle
	end
	
	def self.close
		Game.close
	end
	
	def self.ini
		return Game.getIni
	end
	
	def self.new_scene(name)
		Game.newScene(name)
	end
	
	# Gibt die aktuelle Szene zurück
	def self.scene
		return Game.getScene
	end
end

module Graphics
	#include_package 'cuina.graphics'
	@@view = Java::CuinaGraphics::Graphics::VIEWS
	
	def self.view
		return @@view
	end
end

module Script
	include_package 'cuina.script'
	
	def self.init_lifecycle(owner, name=nil)
		slc = ScriptLifeCycle.new(owner)
		name = "JRuby_" + owner.class.to_s unless name
		Cuina::InjectionManager.injectObject(slc, name)
	end
end

class Context
	java_import 'cuina.Game'

	def initialize(type)
		@type = @type
	end
	
	def []=(key, value)
		 Game.getContext(@type).set(key, value)
	end
	
	def [](key)
		return Game.getContext(@type).get(key)
	end
end
# erstelle Wrapper Konstanten für die Kontexte
GlobalContext = Context.new(Java::Cuina::Context::GLOBAL)
SessionContext = Context.new(Java::Cuina::Context::SESSION)
SceneContext = Context.new(Java::Cuina::Context::SCENE)

# Schalterklasse zur einfachen Handhabung von Schaltern
class Switch
  def []=(index, value)
    Game.setSwitch(index, value)
  end
  
  def [](index)
    return Game.getSwitch(index)
  end
end

# Variablenklasse zur einfachen Handhabung von Variablen
class Variable
  def []=(index, value)
    Game.setVar(index, value)
  end
  
  def [](index)
    return Game.getVar(index)
  end
end

$switches = Switch.new
$variables = Variable.new

module Audio
	include_package 'cuina.audio'
	
	def self.play_snd(snd, volume = 0.8)
		AudioSystem.playSound(snd, volume);
	end
	
	def self.play_bgm(index, bgm, volume = 0.8)
		AudioSystem.playBGM(index, bgm, volume);
	end
	
	def self.stop(index)
		AudioSystem.stop(index)
	end
	
	def self.fade_out(index, time)
		AudioSystem.fadeOut(index, time)
	end
	
	def self.fade_out_in(index, next_bgm, time_in, time_out=nil)
		time_out = time_in unless time_out
		AudioSystem.fadeOut(index, next_bgm, time_in, time_out)
	end
	
	def self.set_volume(index, volume)
		AudioSystem.setVolume(index, volume)
	end
	
	def self.set_pitch(index, pitch)
		AudioSystem.setPitch(index, pitch)
	end
	
	def self.set_loop(index, loop)
		AudioSystem.setLoop(index, loop)
	end
end

module TWL
	include_package 'cuina.widget'
	
	def self.create_widget(descriptor)
		WidgetContainer.new descriptor 
	end
	
	def self.create_from_database(key)
      	data = Cuina::Database.get("Widget", key)
      	descriptor = WidgetFactory.createWidgetDescriptor(data)
      	return create_widget(descriptor)
	end
	
	class WidgetAdapter
		include TWL::WidgetEventHandler
		attr_reader :container
		
		def initialize(container)
			@container = container
			@callbacks = {}
		end
		
		def add_handler(widget_key, handler)
			widget = get_widget(widget_key)
			widget.setEventHandler(handler)
		end
		
		def add_method_handler(widget_key, obj, method)
			add_handler(widget_key, self)
			@callbacks[widget_key] = obj.method(method)
		end
		
		def get_widget(widget_key)
			@container.getDescriptor.getWidget(widget_key)
		end
		
		def handleEvent(key, widget, arg)
			m = @callbacks[key]
			return unless m
			m.call(widget, arg)
		end
		
		def dispose
			@container.dispose
			@container = nil
			@callbacks = nil
		end
	end
end
