package ks;

import static utils.TestStartupManager.DATABASE;
import static utils.TestStartupManager.GRAPHIC;
import static utils.TestStartupManager.PLUGINS;
import static utils.TestStartupManager.SESSION;
import static utils.TestStartupManager.setupTests;

import cuina.Context;
import cuina.FrameTimer;
import cuina.Game;
import cuina.InjectionManager;
import cuina.ks.ActorFactory;
import cuina.ks.AutoMenu;
import cuina.ks.Battle;
import cuina.ks.BattleMap;
import cuina.rpg.actor.ActorGroup;

public class BattleAnalyseTool
{
	static Battle battle;
	
	public static void main(String[] args)
	{
		setupTests(DATABASE | PLUGINS | SESSION | GRAPHIC);
		InjectionManager.loadContextObjects(Context.GLOBAL);
		
		Context sc = Game.getContext(Context.SESSION);
		
		ActorGroup party = new ActorGroup();
		party.addActor(ActorFactory.createActor("Lisa", 3, ActorFactory.ARMOR_MEDIUM));
		sc.set(Battle.PARTY, party);
		
		AutoMenu menu = new AutoMenu();
		sc.set(Battle.BATTLE_MENU, menu);
		
		Game.newScene("Battle");
		InjectionManager.loadContextObjects(Context.SCENE);
		
		FrameTimer.nextFrame();
		Game.getContext(Context.SCENE).<Battle>get("BattleController").setKI(null);
		BattleMap.getInstance().load("test");
		FrameTimer.run();
//		BattleMap.getInstance().
		
////		ActorGroup enemies = new ActorGroup();
//		
//		BattleScene scene = new BattleScene();
//		scene.battlegroundKey = "gras";
//		scene.troop = new EnemyTroop();
//		scene.troop.enemies = new String[] {"Monster"};
//		
//		battle = new Battle();
//		sc.set("BattleController", battle);
	}
}
