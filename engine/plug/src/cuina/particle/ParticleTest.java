package cuina.particle;

import cuina.graphics.Images;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycleAdapter;
import cuina.plugin.Plugin;
import cuina.util.LoadingException;
import cuina.util.Vector;

@ForScene(name="PS_Test", scenes={"Title"})
public class ParticleTest extends LifeCycleAdapter implements Plugin
{
	private static final long serialVersionUID = 1L;
	
	ParticleSystem partSystem;
	
	@Override
	public void init()
	{
		try
		{
			setup();
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setup() throws LoadingException
	{
		partSystem = new ParticleSystem(1000);
		
//		ParticlePhysik physik = new NewtonPhysik(new Vector(0, 0.2f, 0));
		
//		ParticleType type = new ParticleType();
//		type.setImage(Images.createImage("pictures/Partikel.png"));
//		type.setSize(0.5f, 0.5f);
//		type.setLifeTime(200, 200);
//		type.setSpeed(12, 14);
//		type.setDirection(82, 98);
		
		ParticleType type2 = new ParticleType();
		type2.setImage(Images.createImage("pictures/Partikel.png"));
		type2.setSize(0.25f, 0.25f);
		type2.setLifeTime(-1, -1);
//		type2.setSpeed(12, 14);
//		type2.setDirection(82, 98);
		
//		Emitter em = new Emitter(partSystem, type, physik);
//		em.setRegion(new Rectangle(320, 490, 1, 1));
//		em.createStream(0.5f);
		
		RingAnimation ring = new RingAnimation(partSystem, type2);
		ring.createRing(new Vector(320, 228, 0), 24, 200, 0.5f);
		
		System.out.println("[ParticleTest] Test initialisiert");
	}

	@Override
	public void update()
	{
		partSystem.update();
	}

	@Override
	public void dispose()
	{
		partSystem.dispose();
		partSystem = null;
	}
}
