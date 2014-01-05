package cuina.rpg;

import cuina.animation.Animator;
import cuina.animation.Model;
import cuina.movement.Motor;
import cuina.script.ScriptCallBack;
import cuina.world.CuinaObject;

public class CharacterAnimator implements Animator
{
	private static final long serialVersionUID = -1386279993223782693L;
	
	private CuinaObject      obj;
    private Model			model;
    private ScriptCallBack  aniCallback;
    private float lastX;
    private float lastY;
    private int nextAni = -1;
    private boolean standAni;
    
	/**
	 * Default-Mapping fürs Model.
	 * Die ersten 8 Indizes werden benutzt um aus der Bewegungsrichtung eine Animation auszuwählen.
	 * <p>
	 * Die Indizes entsprechen jeweils 45° gegen den Urzeigersinn beginnend mit der 0 auf der rechten Seite.
	 * <p>
	 */
	public static int[] DEFAULT_ANIMATION_MASK = new int[] {2, 3, 3, 3, 1, 0, 0, 0};
    
    /**
     * Maskierung für die einzelen Animationen.
     * Wenn dieses Array gesetzt wird, wird eine Zuweisung aus dem Index zu dessen Wert versucht.
     * Wenn das Array kleiner ist, als der gewählte Index,
     * oder der gefundene Wert <code>-1</code> ist, gilt die Animation als nicht vorhanden.
     * Die Werte entsprechen den vertikal angeordneten Sprites im Image.
     * <p>
     * Die Länge kann beliebig lang sein, sollte aber mindestens alle Animationen beinhalten.
     * Es wird immer vom Index zum Wert gemappt, daher entspricht die Lägnge des Arrays im besten Fall
     * der Menge aller möglichen Animationen. Das Model muss allerdings nicht alle Animationen beinhalten.
     * Es kann eine alternative Animation gewählt werden, oder <code>-1</code> für keine Animation angegeben werden.
     * </p>
     */
    public int[] ANIMATION_MASK;
    
	@Override
	public void init(Model model)
	{
		this.model 	= model;
        this.obj 	= model.getObject();
        this.lastX 	= obj.getX();
        this.lastY 	= obj.getY();
        ANIMATION_MASK = DEFAULT_ANIMATION_MASK;
	}
    
    @Override
    public void animationFinished()
    {
        if (aniCallback != null)
        {
            aniCallback.call(null);
            aniCallback = null;
        }
        
        if (nextAni != -1)
        {
            setAnimationIndex(nextAni);
            nextAni = -1;
        }
    }
    
    @Override
    public void update()
    {
        float dist = Math.abs(obj.getX() - lastX) + Math.abs(obj.getY() - lastY);
        
        rotate();
        move(dist);
        
        lastX = obj.getX();
        lastY = obj.getY();
    }
    
    private void rotate()
    {
    	Motor motor = (Motor) obj.getExtension("motor");
    	if (motor == null) return;
    	
        if (true)//motor.isDirectionLocked())
        {
            int id = Math.round(motor.getDirection() / 45);
            setAnimationIndex(id);
        }
//        if (motor.isAngleUsed()) model.setAngle(obj.getMotor().getDirection());
    }
    
    private void move(float dist)
    {
        // Bewegungs-Animation ausführen
        if (standAni || nextAni != -1 || dist != 0)
        {
            model.setAnimate(true);
            model.setFrameTime((int) (15 / Math.abs(dist)) );
        }
        else
        {   // Objekt steht
            if (model.isAnimate())
            {
                model.setFrame(0);
                model.setAnimate(false);
            }
            model.resetAnimation();
        }
    }
    
    private void setAnimationIndex(int index)
    {
        int mappedIndex;
        if (ANIMATION_MASK == null)
        {
            mappedIndex = index;
        }
        else
        {
            if (index >= ANIMATION_MASK.length || ANIMATION_MASK[index] == -1) return;
            mappedIndex = ANIMATION_MASK[index];
        }
        model.setAnimationIndex(mappedIndex);
    }
    
    public void forceAnimation(int index)
    {
        forceAnimation(index, model.getAnimationIndex());
    }
    
    public void forceAnimation(int index, int nextAni)
    {
        this.nextAni = nextAni;
        model.setAnimationIndex(index);
        model.resetAnimation();
        model.setAnimate(true);
    }
    
    public void setCallBack(ScriptCallBack callBack)
    {
        this.aniCallback = callBack;
    }
}