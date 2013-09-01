package cuina.particle;

import cuina.particle.ParticleSystem.Particle;
import cuina.util.Vector;

public abstract class AbstractEmitter
{

	private ParticleSystem partSystem;

	public AbstractEmitter(ParticleSystem ps)
	{
		this.partSystem = ps;
		partSystem.addEmitter(this);
	}

	protected Particle createParticle(Vector pos)
	{
		return partSystem.createParticle(this, pos);
	}
	
	public abstract ParticleType getParticleType();
	public abstract ParticlePhysik getParticlePhysik();
	public abstract void update();
	
    public void dispose()
    {
        partSystem.removeEmitter(this);
    }
}
