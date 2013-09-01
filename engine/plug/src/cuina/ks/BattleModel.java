package cuina.ks;
 
import cuina.ks.Battle.AnimationType;
import cuina.rpg.actor.Actor;
import cuina.world.CuinaModel;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
 
/**
 * Dummy-Implementation
 */
public class BattleModel implements CuinaModel
{
	private static final long serialVersionUID = 5018057349551140473L;

	private JButton button;
	
    private Battle battle;
    private AnimationType type;
    private AnimationType lastType;
    private Actor actor;
    
    public BattleModel(Battle battle, Actor actor)
    {
        super();
        this.battle = battle;
        this.actor = actor;
        
        button = new JButton();
        
        button.setActionCommand( String.valueOf(actor.getName()) );
//        button.addActionListener(Game.window.menuPanel);
        button.setPreferredSize(new Dimension(160, 120));
        button.setBackground(Color.WHITE);
        
        update();
//        Game.window.addBattleModel(this);
    }
    
    @Override
	public void update()
    {
        StringBuilder builder = new StringBuilder("<html><pre>");
        builder.append("Name:  " + actor.getName() + "<br>");
        builder.append("Level: " + actor.getLevel() + "<br>");
        builder.append("HP:    " + actor.getAttribut("HP") + "<br>");
        builder.append("MP:    " + actor.getAttribut("MP") + "<br>");
        builder.append("</pre>");
        BattleObject battler = battle.getBattler(actor);
        if (battler != null && battler.getAction() != null)
        {
            builder.append(battler.getAction().getName());
            if (battler.getAction().getTargets().size() > 0)
                builder.append(" (OK)");
        }
        builder.append("</html>");
        button.setText(builder.toString());
    }
    
    public void performAnimation(AnimationType type, Battle.ActionResult result)
    {
//      System.out.println("Animate " + actor.getName() + ": " + type);
        this.lastType = this.type;
        this.type = type;
        switch(type)
        {
            case DIE: 
            case HIT:
            	 button.setBackground(Color.RED);
                break;
            case SELECT:
            	 button.setBackground(new Color(0, 255, 0));
                break;
            case ATTACK:
            	 button.setBackground(new Color(0, 128, 255));
                break;
            default:
                if (lastType == AnimationType.SELECT)
                	 button.setBackground(new Color(0, 255, 0));
                else
                	 button.setBackground(Color.WHITE);
        }
        update();
    }
    
    public boolean inAnimation()
    {
        return false;
    }
 
    @Override
    public void refresh() {}
 
//    @Override
//    public void updatePosition() TODO: Auskommentiert wegen Überarbeitungs-Not
//    {
//        if (type == AnimationType.SELECT) return;
//        
//        performAnimation(AnimationType.NONE, null);
//    }
 
    @Override
    public void dispose() {}
 
    @Override
	public float getZ()
    {
        return 0;
    }

	@Override
	public void setPosition(float x, float y, float z)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisible(boolean value)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWidth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getX()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}