package cuina.particle;
 
import cuina.particle.ParticleSystem.Particle;
import cuina.util.Vector;
 
/**
 * Eine Newtonphysik animiert Partikel in Abhängigkeit eines Schwerefeldes,
 * welches wahlweise als Richtung oder Punktförmig eingestellt werden kann.
 * @author TheWhiteShadow
 */
public class NewtonPhysik implements ParticlePhysik
{
	/** Stellt eine punktuelle Gravitation da. */
	public static final int LOCATION = 0;
	/** Stellt eine gerichtete Gravitation da. */
	public static final int DIRECTION = 1;
    // Temporärer Vector für Zwischenberechnungen
    private static Vector vec = new Vector();
    
    private Vector vector;
    private float force;
    private int gravityMode;
 
    /**
     * Erstellt eine neue NewtonPhysik.
     * @param gravity Vektor der Gravitation.
     */
    public NewtonPhysik(Vector gravity)
    {
        this.vector = gravity;
        gravityMode = DIRECTION;
    }
    
    public NewtonPhysik(Vector point, float force)
    {
        this.vector = point;
        this.force = force;
        gravityMode = LOCATION;
    }
    
    @Override
    public void update(Particle p)
    {
        if (gravityMode == DIRECTION)
        {
        	if (vector != null)
        		p.speed.add(vector);
        }
        else
        {
            vec.set(vector);
            vec.sub(p.pos);
            vec.normalize();
            vec.mul(force);
            p.speed.add(vec);
        }
        p.pos.add(p.speed);
    }
}