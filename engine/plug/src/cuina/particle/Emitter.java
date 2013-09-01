package cuina.particle;
 
import cuina.util.Vector;

import java.awt.Rectangle;
import java.util.Random;
 
 
public class Emitter extends AbstractEmitter
{
    private ParticlePhysik physik;
    private ParticleType type;
    Rectangle region;
    float streamCount;
    float bufferCount;
    
    public Emitter(ParticleSystem ps, ParticleType type, ParticlePhysik physik)
    {
    	super(ps);
        this.type = type;
        this.physik = physik;
    }
    
    public void setRegion(int x, int y, int width, int height)
    {
        region = new Rectangle(x, y, width, height);
    }
    
    public void setRegion(Rectangle rect)
    {
        region = new Rectangle(rect);
    }
    
    public Rectangle getRegion()
    {
        return region;
    }
 

	public void createBurst(int count)
	{
		Random rnd = new Random();
		float x, y;
		for (int i = 0; i < count; i++)
		{
			x = region.x + rnd.nextInt(region.width);
			y = region.y + rnd.nextInt(region.height);
			// z = rnd.nextFloat();
			createParticle(new Vector(x, y, 0));
		}
	}
    
    /**
     * Erzeugt einen Stream, der jeden Frame Partikel erzeugt.
     * Die Zahl der erzeugten Partikel pro Frame schwankt um den angegebenenMittelwert.
     * @param count Anzahl der Partikel pro Frame.
     */
    public void createStream(float count)
    {
        this.streamCount = count;
        update();
    }
    
    @Override
    public ParticleType getParticleType()
    {
        return type;
    }
 
    public void setParticleType(ParticleType type)
    {
        this.type = type;
    }
    
    @Override
	public void update()
	{
	    bufferCount += streamCount;
	    if (bufferCount >= 1)
	    {
	    	createBurst((int)bufferCount);
	    	bufferCount = 0;
	    }
	}

	@Override
	public ParticlePhysik getParticlePhysik()
	{
		return physik;
	}

	void setParticlePhysik(ParticlePhysik physik)
	{
		this.physik = physik;
	}
	
	
}