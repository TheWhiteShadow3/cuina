package cuina.rpg;
 
import cuina.data.NamedItem;
import cuina.rpg.actor.Equippable;
 
public class TestArmor implements Equippable, NamedItem
{
    private String name;
    private long def;
    private long mdef;
    
    public TestArmor(String name, long def, long mdef)
    {
        this.name = name;
        this.def = def;
        this.mdef = mdef;
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    public long getDef()
    {
        return def;
    }
    
    public long getMDef()
    {
        return mdef;
    }
 
    @Override
    public long getAttValue(String name)
    {
        if ("DEF".equals(name))
            return getDef();
        else if ("MDEF".equals(name))
            return getMDef();
        else return 0;
    }
}