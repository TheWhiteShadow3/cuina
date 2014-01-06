# Map-Script-Libary

puts "Map-libary imported"

module Map
	include_package "cuina.map"
	java_import "cuina.object.BaseObject"
	#java_import "cuina.map.GameMap"
	#java_import "cuina.map.Motor"
	java_import "cuina.graphics.WeatherEffects"

	def self.map
		map = GameMap.getInstance
		GameMap.new.init() unless map
		return map
	end

	def self.load_map(map_key)
		map.load(map_key)
	end
	
	def self.get_object(id)
		return map.getObject(id)
  	end
  	
	def self.follow(obj_id, view_id=0)
    	map.follow(obj_id, view_id)
	end
  
	def self.show_animation(id, target_id)
    	# Nur ne billige Test-Implementation (Nie getestet)
    	model = map.getObject(target_id).getModel()
    	model.forceAnimation(id)
	end
  
	def self.remove_object(obj)
		map.removeObject(obj)
	end
  
	def self.move_to_map(map_id, x, y = nil)
		fade_out(0, 30)
		if(map.getID() != map_id)
      		#map.removeObject(player)
      		map.loadMap(map_id)
    	end
    
    	player = get_player()
		if (y == nil)
      		otherObj = map.get_object(x);
      		player.moveToPosition(otherObj.getLogicX(), otherObj.getLogicY())
    	else
      		player.moveToPosition(x, y)
    	end

		fade_in(0, 30)
	end
  
	def self.get_panorama(id)
		return map.getPanorama(id)
	end
  
	def self.create_panorama(id, image, zoomX, zoomY, speedX, speedY, alpha)
		p = cuina.graphics.Panorama.new(image)
  		p.setZoomX(zoomX)
		p.setZoomY(zoomY)
		p.setSpeedX(speedX)
		p.setSpeedY(speedY)
		p.setAlpha(alpha)
		return map.setPanorama(id, p)
	end
  
	def create_weather(type, intensity, wind)
		weather = WeatherEffects.new()
		weather.setEffect(type, intensity)
		weather.setWind(wind)
		Game.getScene().setObject("Weather", weather)
	end
	
	def fade_out(effect, frames)
		GlobalContext["Transition"].fadeOut(effect, frames);
	end
	
	def fade_in(effect, frames)
		GlobalContext["Transition"].fadeIn(effect, frames);
	end
end