package cuina.rpg;
 
import cuina.rpg.inventory.Item;

import java.util.HashMap;
 
public class Skill extends Item
{
    private static final long   serialVersionUID    = -1305923021067535516L;
    
    public static final int FLAG_MAGIC = 1;
    
    public static final int SCOPE_NONE  = -1;
    public static final int SCOPE_SELF  = 0;
    public static final int SCOPE_ONE   = 1;
    public static final int SCOPE_ALL   = 2;
    public static final int SCOPE_ALLY  = 4;
    public static final int SCOPE_ENEMY = 8;
    public static final int SCOPE_DEAD  = 16;
    
    public final HashMap<String, Long> costs = new HashMap<String, Long>(4);
    public final HashMap<String, Float> attModifier = new HashMap<String, Float>(8);
    /** Element-Attribute des Skills. */
    public final HashMap<String, Boolean> elements = new HashMap<String, Boolean>(4);
    /** Zustandseffekte des Skills. */
    public final HashMap<String, Boolean> states = new HashMap<String, Boolean>(4);
    public int scope = 9;
    public int flags;
    public float time = 1f;
    public float hit = 1f;

	public Skill()
	{
		super();
	}

	public Skill(String key, String name, int flags, int scope, float time)
	{
		super(key, name, null ,null);
		this.scope = scope;
		this.flags = flags;
		this.time = time;
	}
}