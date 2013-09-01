package cuina.ks;

import cuina.rpg.TestArmor;
import cuina.rpg.TestWeapon;
import cuina.rpg.actor.Actor;
import cuina.rpg.actor.Attribut;

 
//XXX: Debug-Klasse um schnell mal nen Helden zu erstellen
public class ActorFactory
{
    public static final int ARMOR_NONE = 0;
    public static final int ARMOR_LOW = 1;
    public static final int ARMOR_MEDIUM = 2;
    public static final int ARMOR_HIGH = 3;
    
    public static Actor createActor(String name, int level, int armorLevel)
    {
        Actor actor = new Actor();
        initActor(actor, name, level);
        setParameters(actor, level, armorLevel);
        return actor;
    }
    
    public static Enemy createEnemy(String name, int level, int armorLevel, KiSetting kiSetting)
    {
        Enemy enemy = new Enemy();
        initActor(enemy, name, level);
        enemy.setKiSetting(kiSetting);
        setParameters(enemy, level, armorLevel);
        return enemy;
    }
    
    private static void initActor(Actor actor, String name, int level)
    {
        actor.setKey(name);
        actor.setName(name);
        actor.setLevel(level);
    }
    
    private static void setParameters(Actor actor, int level, int armorLevel)
    {
        // veränderbare Attribute
        actor.addAttribut(new Attribut("HP", new float[]{75f, 2.5f, 1.2f}, false));
        actor.addAttribut(new Attribut("MP", new float[]{40f, 1.8f, 1.1f}, false));
        // fixe Attribute
        actor.addAttribut(new Attribut("ATK", 0));
        actor.addAttribut(new Attribut("STR", new float[]{25f, 1.3f, 1.06f}, true));
        actor.addAttribut(new Attribut("DEF", new float[]{22f, 1.2f, 1.05f}, true));
        actor.addAttribut(new Attribut("RES", new float[]{18f, 1.2f, 1.02f}, true));
        actor.addAttribut(new Attribut("HIT", new float[]{24f, 1.3f, 1.05f}, true));
        actor.addAttribut(new Attribut("SPD", new float[]{18f, 1.4f, 1.04f}, true));
        actor.addAttribut(new Attribut("INT", new float[]{19f, 1.2f, 1.04f}, true));
        // Testwaffe:
        actor.setEquipment(0, new TestWeapon("Schwert", level * 2 + 6));
        
        switch(armorLevel)
        {
            case ARMOR_LOW:
                actor.setEquipment(0, new TestArmor("Stoffrüstung", level * 2 + 10, level * 2 + 10));
                break;
            case ARMOR_MEDIUM: 
                actor.setEquipment(0, new TestArmor("Lederrüstung", level * 4 + 20, level * 4 + 20));
                break;
            case ARMOR_HIGH: 
                actor.setEquipment(0, new TestArmor("Metallrüstung", level * 6 + 30, level * 6 + 30));
                break;
        }
        
        actor.setElementResistance("feuer", 0.75f);
        actor.setElementResistance("eis", 1.25f);
    }
}