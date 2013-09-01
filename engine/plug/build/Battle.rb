module Battle
	def self.battlemap
		map = BattleMap.getInstance
		GameMap.new.init() unless map
		return map
	end
	
	def self.start_battle(battle_key)
		puts "setze Battle-Szene #{battle_key}"
		Game.newScene("Battle")
		puts "starte Kampf"
		SessionContext["BattleMap"].load(battle_key)
		#Game::Session.get("Battle").startBattle(troop, filed_id)
	end
	
	def self.get_troop(id)
		return Database.get("EnemyTroop", id)
	end
end