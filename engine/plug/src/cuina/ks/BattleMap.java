package cuina.ks;

import cuina.Context;
import cuina.Game;
import cuina.database.Database;
import cuina.object.BaseWorld;
import cuina.plugin.ForSession;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.world.CuinaWorld;

/**
 * Update-Relevant.
 */
@ForSession(name = "BattleMap", scenes = { "Battle" })
public class BattleMap extends BaseWorld implements Plugin, LifeCycle
{
	private static final long serialVersionUID = 1140944698278228245L;

	private BattleScene scene;
	private BattleGround ground;
	private Battle battle;

	public void load(String battleKey)
	{
		scene = Database.<BattleScene> get("BattleScene", battleKey);
		ground = new BattleGround(Database.<BattleGroundData> get("BattleGround", scene.battlegroundKey));
		battle.startBattle(scene);
		
        //XXX: Debug-Loop (test nur für den sleep!)
//        while(battle.isRunning() || true)  // Im Battle erfolgt ein System.exit
//        {
//            try
//            {
//                Thread.sleep(1000);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//            
//            FrameTimer.nextFrame();
////            update();
////            Game.window.statusPanel.update();
//        }
		update();
    }

	@Override
	public void init()
	{
		this.battle = Game.getContext(Context.SCENE).<Battle> get("BattleController");
	}

	public static BattleMap getInstance()
	{
		return Game.getContext(Context.SESSION).get(CuinaWorld.INSTANCE_KEY);
	}

	public BattleScene getBattleScene()
	{
		return scene;
	}

	public void setBattleScene(BattleScene scene)
	{
		this.scene = scene;
	}

	public BattleGround getBattleGround()
	{
		return ground;
	}

	public void setBattleGround(BattleGround ground)
	{
		this.ground = ground;
	}

	@Override
	public void update()
	{
		super.update();
		battle.update();
	}

	@Override
	public void dispose()
	{
		ground.dispose();
		super.dispose();
	}

	@Override
	public void postUpdate() {}
}