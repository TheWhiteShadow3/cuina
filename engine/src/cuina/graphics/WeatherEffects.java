package cuina.graphics;

import java.awt.Rectangle;
import java.util.Random;

import org.lwjgl.opengl.GL11;


public class WeatherEffects implements Graphic
{
	private static final long	serialVersionUID	= 1863093487053313530L;

	transient private Particle[] particles;
	
	Random random = new Random();
	
	public static final int NONE = 0;
	public static final int RAIN = 1;
	public static final int SNOW = 2;
	public static final int SANDSTORM = 3;
	
	private Rectangle effectArea;
	private int depth = 10000;
	private float intensity;
	private int green;
	private float wind = 2;
	private int effect = NONE;
//	private Sprite snowSprite;

	private GraphicContainer container;
	
	// Interne Wetter-Parameter
	// Rain:
	private static int rainLifeTime 	 = 80;		// Lebensdauer eines Regentropfens in Frames
	private static float rainWindDrift 	 = 0.5f;	// Windeinfluss
	private static float rainBaseFall 	 = 6.0f;	// Fallgeschwindigkeit
	private static float rainRandomFall  = 3.0f;	// Fall-Varianz
	// Snow:
	private static int snowLifeTime 	 = 600;		// Lebensdauer einer Schneeflocke in Frames
	private static float snowWindDrift 	 = 0.20f;	// Windeinfluss
	private static float snowBaseFall 	 = 0.10f;	// Fallgeschwindigkeit
	private static float snowRandomFall  = 0.20f;	// Fall-Varianz
	private static float snowBaseDrift 	 = 0.05f;	// Driftgeschwindigkeit
	private static float snowRandomDrift = 0.01f;	// Drift-Varianz
	// Sand:
	
	public WeatherEffects(GraphicManager manager)
	{
		effectArea = new Rectangle(-100, -100, Graphics.getWidth() + 200, Graphics.getHeight() + 200);
		if (manager == null) return;
		manager.addGraphic(this);
	}
	
	public WeatherEffects()
	{
		this(Graphics.GraphicManager);
	}
	
	@Override
	public void setContainer(GraphicContainer container)
	{
		this.container = container;
	}

	@Override
	public GraphicContainer getContainer()
	{
		return container;
	}
	
	public int getEffect()
	{
		return effect;
	}
	
	public void setEffect(int type, int intensity)
	{
		switch (type)
		{
			case RAIN: initRain(intensity); break;
			case SNOW: initSnow(intensity); break;
			case SANDSTORM: initSandstorm(intensity, 0); break;
		}
	}
	
	public void initRain(float intensity)
	{
		if (intensity > 1) intensity = 1f;
		
		particles = new Particle[(int)(2000 * intensity)];
		for(int i=0; i<particles.length; i++)
		{
			particles[i] = new Particle();
		}
		this.intensity = intensity;
		this.effect = RAIN;
		float vectorX = wind * rainWindDrift;
		for(int i = 0; i<particles.length;i++)
		{
			particles[i].function = RAIN;
			particles[i].lifetime = random.nextInt(rainLifeTime); 
			particles[i].x = random.nextFloat() * effectArea.width + effectArea.x;
			particles[i].y = random.nextFloat() * effectArea.height + effectArea.y;
			particles[i].vectorX = vectorX;
			particles[i].vectorY = (random.nextFloat() * rainRandomFall + rainBaseFall) + intensity * 2;
			float rainColor = 128.0f + random.nextFloat() * 127.0f;
			particles[i].alpha = (byte) (50.0f + random.nextFloat() * 205.0f);
			particles[i].blue = (byte) 255;
			particles[i].green = (byte) rainColor;
			particles[i].red = (byte) rainColor;

		}
	}
	
	public void initSnow(float intensity)
	{
		if (intensity > 1) intensity = 1f;
		
		particles = new Particle[(int)(5000 * intensity)];
		for(int i=0; i<particles.length; i++)
		{
			particles[i] = new Particle();
		}
		this.intensity = intensity;
		this.effect = SNOW;
		
//		snowSprite = new Sprite("snow.png");
		for(int i = 0; i<particles.length;i++)
		{
			particles[i].function = SNOW;
			particles[i].lifetime = random.nextInt(snowLifeTime); 
			particles[i].x = random.nextFloat() * effectArea.width + effectArea.x;
			particles[i].y = random.nextFloat() * effectArea.height + effectArea.y;
			particles[i].vectorX = (snowBaseDrift - random.nextFloat() * snowRandomDrift) + wind * snowWindDrift;
			particles[i].vectorY = (snowBaseFall + random.nextFloat() * snowRandomFall) + intensity / 4;
			float snowColor = 205.0f + random.nextFloat() * 50.0f;
			particles[i].alpha = (byte) (50.0f + random.nextFloat() * 205.0f);
			particles[i].blue = (byte) snowColor;
			particles[i].green = (byte) snowColor;
			particles[i].red = (byte) snowColor;
		}
	}
	
	public void initSandstorm(float intensity, int green)
	{
		if (intensity > 1) intensity = 1f;
		
		particles = new Particle[(int)(5000 * intensity)];
		for(int i=0; i<particles.length; i++)
		{
			particles[i] = new Particle();
		}
		this.intensity = intensity;
		this.green = green;
		this.effect = SANDSTORM;
		for(int i = 0; i<particles.length;i++)
		{
			particles[i].function = SANDSTORM;
			particles[i].x = random.nextFloat() * effectArea.width + effectArea.x;
			particles[i].y = random.nextFloat() * effectArea.height + effectArea.y;
			particles[i].vectorX = (4.0f - random.nextFloat()*8.0f) * intensity;
			particles[i].vectorY = (4.0f - random.nextFloat()*8.0f) * intensity;
			particles[i].alpha = (byte) (50.0f + random.nextFloat() * 205.0f);
			particles[i].blue = (byte)(random.nextFloat() * green);
			particles[i].green = (byte)green;
			particles[i].red = (byte) 255;
		}
	}
	
	public void update()
	{
		if (particles == null) return;
		
		for(Particle particle : particles)
		{
			if(particle != null && particle.function != NONE)
			{
				particle.x += particle.vectorX;
				particle.y += particle.vectorY;
				
				if(particle.function == RAIN)
				{
					
					if(particle.y >= effectArea.height + effectArea.y || particle.lifetime <= 0)
					{
						particle.y = random.nextFloat() * effectArea.height + effectArea.y;
						particle.lifetime = random.nextInt(rainLifeTime); 
					}
					if(particle.x >= effectArea.width + effectArea.x)
					{
						particle.x = effectArea.x;
					}
					if(particle.x < effectArea.x)
					{
						particle.x = effectArea.x + effectArea.width;
					}
				}
				if(particle.function == SNOW || particle.function == SANDSTORM)
				{
//					particle.radius += (0.025f - random.nextFloat()*0.05f) * intensity;
					particle.vectorX += (0.02f - random.nextFloat() * 0.04f);
					particle.vectorY += (0.02f - random.nextFloat() * 0.04f);
					if(	particle.y >= effectArea.height + effectArea.y || 
						particle.x >= effectArea.width + effectArea.x ||
						particle.x < effectArea.x ||
						particle.y < effectArea.y ||
						particle.lifetime <= 0
						)
					{
						particle.lifetime = random.nextInt(snowLifeTime);
						particle.x = random.nextFloat() * effectArea.width + effectArea.x;
						particle.y = random.nextFloat() * effectArea.height + effectArea.y;
						particle.vectorX = (snowBaseDrift - random.nextFloat() * snowRandomDrift) + wind * snowWindDrift;
						particle.vectorY = (snowBaseFall + random.nextFloat() * snowRandomFall) + intensity / 4;
					}
				}
				particle.lifetime--;
			}
		}
	}
	
	@Override
	public void draw()
	{
		if (particles == null) return;
		
		for(Particle particle : particles)
		{
			if (particle.function != NONE)
			{
				particle.render();
			}
		}
	}
	
	private class Particle
	{
		private float x, y;
		private byte red, green, blue, alpha;
		private float vectorX, vectorY;
		private int function = NONE;
		private double radius = Math.random();
		private int lifetime;
				
		public void render()
		{
			byte destAlpha = (byte)(this.alpha * alpha);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			if(function == RAIN)
			{
				GL11.glBegin(GL11.GL_LINES);
				{
					GL11.glColor4ub(red, green, blue, (byte)(destAlpha/200));
					GL11.glVertex2f(x - vectorX * 4, y - vectorY * 6);
					GL11.glColor4ub(red, green, blue, destAlpha);
					GL11.glVertex2f(x, y);
				}
				GL11.glEnd();
			}
			else
			{
//				snowSprite.setTransperency(alpha / 128.0);
//				snowSprite.setX((int)x);
//				snowSprite.setY((int)y);
//				snowSprite.draw();
				GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glColor4ub(red, green, blue, destAlpha);
					GL11.glVertex2d(x, y);
					GL11.glVertex2d(x + 2 * radius, y);
					GL11.glVertex2d(x + 2 * radius, y + 2 * radius);
					GL11.glVertex2d(x, y + 2 * radius);
				}
				GL11.glEnd();
			}
				
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}

//	@Override
//	public int getX()
//	{
//		return effectArea.x;
//	}
//
//	@Override
//	public int getY()
//	{
//		return effectArea.y;
//	}
	
	public float getWind()
	{
		return wind;
	}

	public void setWind(float wind)
	{
		this.wind = wind;
	}

	public int getWidth()
	{
		return effectArea.width;
	}

	public int getHeight()
	{
		return effectArea.height;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	@Override
	public int getDepth()
	{
		return depth;
	}

	@Override
	public void refresh()
	{
		random = new Random();
		switch(effect)
		{
			case RAIN: 		initRain(intensity); break;
			case SNOW:		initSnow(intensity); break;
			case SANDSTORM:	initSandstorm(intensity, green); break;
		}
	}

	@Override
	public void dispose()
	{
		particles = null;
	}
	
	public boolean isDisposed()
	{
		return particles == null;
	}
}
