package cuina.ks;

import cuina.Context;
import cuina.Game;
import cuina.database.Database;
import cuina.map.GameMap;
import cuina.object.BaseWorld;
import cuina.plugin.ForSession;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.world.CuinaObject;
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
 
    @Override
	public void load(String battleKey)
    {
        scene = Database.<BattleScene>get("BattleScene", battleKey);
        ground = new BattleGround(Database.<BattleGroundData>get("BattleGround", scene.battlegroundKey));
        

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
//            FrameTimer.pseudoUpdate();
//            update();
//            Game.window.statusPanel.update();
//        }
//      update();
    }

	@Override
	public void init()
	{
        battle = Game.getContext(Context.SCENE).<Battle>get("BattleController");
        battle.startBattle(scene);
	}
	
	public static GameMap getInstance()
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
    
    private int aviableID = 1;
    
    @Override
    public synchronized int addObject(CuinaObject obj)
    {
        if (obj.getID() == -1)
            obj.setID(aviableID++);
        else
            aviableID = Math.min(aviableID, obj.getID() + 1);
        return super.addObject(obj);
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