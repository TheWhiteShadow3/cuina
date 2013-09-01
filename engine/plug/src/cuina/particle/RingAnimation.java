package cuina.particle;

import cuina.particle.ParticleSystem.Particle;
import cuina.util.Vector;

public class RingAnimation extends AbstractEmitter
{
	private static final float PI2 = (float) (2 * Math.PI);
	private static final Vector vec = new Vector();

	private ParticleType type;
	private RingPhysik physik;

	public RingAnimation(ParticleSystem ps, ParticleType type)
	{
		super(ps);
		this.type = type;
	}

	public void createRing(Vector pos, int count, float dist, float angleSpeed)
	{
		physik = new RingPhysik(pos, count, dist, angleSpeed);

		float angle = 0f;
		float step = PI2 / count;
		for (int i = 0; i < count; i++)
		{
			vec.set((float) (pos.x + dist * Math.cos(i)), (float) (pos.y - dist * Math.sin(i)), 0f);
			Particle p = createParticle(vec);
			p.angle = angle;
			angle += step;
		}
	}

	private class RingPhysik implements ParticlePhysik
	{
		private Vector pos;
		private int count;
		private float dist;
		private float angleSpeed;

		private RingPhysik(Vector pos, int count, float dist, float angleSpeed)
		{
			this.pos = pos;
			this.count = count;
			this.dist = dist;
			this.angleSpeed = (float) (angleSpeed * Math.PI / 360);
		}

		@Override
		public void update(Particle p)
		{
			p.angle += angleSpeed;
			p.pos.set((float) (pos.x + dist * Math.cos(p.angle)), (float) (pos.y - dist * Math.sin(p.angle)), 0f);
		}
	}

	@Override
	public ParticleType getParticleType()
	{
		return type;
	}

	@Override
	public ParticlePhysik getParticlePhysik()
	{
		return physik;
	}

	/** Leer-Implementation. */
	@Override public void update() {}
}
