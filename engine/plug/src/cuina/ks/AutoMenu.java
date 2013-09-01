package cuina.ks;

/**
 * Simuliert Benutzereingaben.
 * @author TheWhiteShadow
 */
public class AutoMenu implements BattleMenu
{
	@Override
	public void requestInput(Battle battle, BattleObject battler)
	{
		BattleAction action = new BattleAction(battler.getActor(), "Angriff", 1f);
		action.addTarget(battle.getEnemies().getActor(0));
		
		battler.setAction(action);
		battle.menuCallback(battler);
	}

	@Override
	public void cancelRequest(Battle battle, BattleObject battler)
	{
		
	}

	@Override
	public void setVisible(boolean value)
	{
		
	}

	@Override
	public void update()
	{
		
	}
}
