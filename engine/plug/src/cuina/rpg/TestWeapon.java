package cuina.rpg;
 
import cuina.database.NamedItem;
import cuina.rpg.actor.Equippable;
 
public class TestWeapon implements Equippable, NamedItem
{
    private String name;
    private long atk;
    
    public TestWeapon(String name, long atk)
    {
        this.name = name;
        this.atk = atk;
    }
 
    @Override
    public String getName()
    {
        return name;
    }
 
    public long getAtk()
    {
        return atk;
    }
 
    @Override
    public long getAttValue(String name)
    {
        return "ATK".equals(name) ? getAtk() : 0;
    }
}