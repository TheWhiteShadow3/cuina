package cuina.particle;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import cuina.FrameTimer;
import cuina.graphics.D3D;
import cuina.graphics.GLCache;
import cuina.graphics.Graphic;
import cuina.graphics.GraphicContainer;
import cuina.graphics.Graphics;
import cuina.graphics.Image;
import cuina.util.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class ParticleSystem implements Graphic
{
	private static final long serialVersionUID = -6242311692051798432L;

	private final ArrayList<AbstractEmitter> emitterList = new ArrayList<AbstractEmitter>();

	transient private Particle[] particles;
	private int partCount;
	private int size;
	private int addPos; // Index, an dem neue Partikel hinzugefügt werden.
	private ThreadAnimator animator;
	private GraphicContainer container;

	public ParticleSystem(int count)
	{
		this(count, Graphics.GraphicManager);
	}

	public ParticleSystem(int count, GraphicContainer container)
	{
		this.partCount = count;
		if (container != null) container.addGraphic(this);
		refresh();
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

	protected void addEmitter(AbstractEmitter em)
	{
		emitterList.add(em);
	}

	protected void removeEmitter(AbstractEmitter em)
	{
		emitterList.remove(em);
	}

    protected synchronized Particle createParticle(AbstractEmitter emitter, Vector pos)
    {
        ParticleType type = emitter.getParticleType();
        ParticlePhysik physik = emitter.getParticlePhysik();
        
        Particle particle = particles[addPos];
        if (particle == null)
            particle = new Particle(type, physik, pos);
        else
            particle.init(type, physik, pos);
        
        float speed = setValue(type.minSpeed, type.maxSpeed);
        float dir   = (float) (setValue(type.minDir, type.maxDir) * Math.PI / 180d);
        particle.speed.set(speed * (float)Math.cos(dir), speed * (float)-Math.sin(dir), 0);
        
//      speed   = setValue(type.minGravity, type.maxGravity);
//      dir     = (float) (setValue(type.minGDir, type.maxGDir) * Math.PI / 180d);
//      particle.gravity.set(speed * (float)Math.cos(dir), speed * (float)-Math.sin(dir), 0);
        
        particle.size = setValue(type.minSize, type.maxSize);
        particle.maxLifeTime = setValue(type.minLife, type.maxLife);
        particle.lifeTime = particle.maxLifeTime;
        
        particles[addPos++] = particle;
        if (addPos >= particles.length) addPos = 0;
        if (size < particles.length) size++;
        
        return particle;
    }
    
    private int setValue(int min, int max)
    {
        return (int) (min + (max - min) * Math.random());
    }
    
    private float setValue(float min, float max)
    {
        return (float) (min + (max - min) * Math.random());
    }
 
    @Override
    public int getDepth()
    {
        return 1000;
    }
 
    @Override
    public void refresh()
    {
        particles = new Particle[partCount];
        size = 0;
        addPos = 0;
        
        animator = new ThreadAnimator();
        animator.start();
    }
    
    public void update()
    {
        if (animator == null) update0();
    }
    
    private synchronized void update0()
    {
        for(int i = 0; i < emitterList.size(); i++)
        {
            emitterList.get(i).update();
        }
        
        for(int i = 0; i < size; i++)
        {
            if (particles[i] != null && particles[i].lifeTime != 0)
                particles[i].update();
        }
    }
 
    int id;
    private FloatBuffer verticData;
//    Mesh mesh;
//    Shader shader = new Shader("part");
    
    @Override
    public void draw()
    {
//        shader.bind();
        D3D.set3DView(false);
        
        GLCache.setColor(ReadableColor.WHITE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        Particle p;
        for(int i = addPos; i < size + addPos; i++)
        {
            p = particles[i % size];
            if (p.lifeTime == 0) continue;
            p.type.image.getTexture().bind();
            GLCache.setBlendMode(p.type.drawMode);
            
            glPushMatrix();
            glTranslatef(p.pos.x, p.pos.y, 0);
            
            float width =  p.type.image.getWidth() * p.size / 2;
            float heght =  p.type.image.getHeight() * p.size / 2;
            glScalef(width, heght, 1);
//          glRotatef(angle, 0, 0, 1);
            
            if (id != 0)
            {
    //          glCallList(id);
                
                glBindBuffer(GL_ARRAY_BUFFER, id);
                glTexCoordPointer(2, GL_FLOAT, 5 * 4, 0);
                glVertexPointer(3, GL_FLOAT, 5 * 4, 2 * 4);
            
    //          EXTDrawInstanced.glDrawArraysInstancedEXT(GL_QUADS, 0, verticData.limit() / 4, 1);
    //          GLAR.glDrawArraysInstanced(GL_QUADS, 0, verticData.limit() / 4, 1);
                glDrawArrays(GL_QUADS, 0, verticData.limit() / 4);
            }
            else
            {
            	Image img = p.type.image;
            	float texUse = img.getWidth() / (float) img.getTexture().getWidth();
                float[] vertices = new float[] {0, 0,           -1, -1, 0,
                                                0, texUse,      -1, 1, 0,
                                                texUse, texUse, 1, 1, 0,
                                                texUse, 0,      1, -1, 0};
                
                verticData = ByteBuffer.allocateDirect(4 * vertices.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
                verticData.put(vertices);
                verticData.flip();
                
    //          id = glGenLists(1);
    //          glNewList(id, GL_COMPILE_AND_EXECUTE);
    //          
    //          glInterleavedArrays(GL_T2F_V3F, 0, verticData);
    //          glDrawArrays(GL_QUADS, 0, verticData.limit() / 4);
    //          glEndList();
                
                id = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, id);
                glBufferData(GL_ARRAY_BUFFER, verticData, GL_STATIC_DRAW);
                
                glEnableClientState(GL_VERTEX_ARRAY);
                glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            }
            glPopMatrix();
        }
//      for(int i = 0; i < size; i++)
//      {
//          Particle p = particles[i];
//          if (p == null) continue;
//          if (p.maxLifeTime <= p.lifeTime++)
//          {
//              particles[i] = null;
//              continue;
//          }
//          
//          if (p.type.image != null)
//          {
//              p.type.image.setColor(getParticleColor(p));
//              p.type.image.draw(p.pos.x, p.pos.y, (float)p.size, (float)p.size);
//          }
//          else
//          {
//              p.type.image = new Image(p.size, p.size);
//              p.type.image.clear(Color.WHITE);
//          }
//      }
//        Shader.unbind();
    }
    
    private Color getParticleColor(Particle p)
    {
        mixColor(p.color, p.type.col1, p.type.col2, p.lifeTime / (float)p.maxLifeTime);
        return p.color;
    }
    
    private void mixColor(Color dst, ReadableColor s1, ReadableColor s2, float value)
    {
        dst.set( (int) (s1.getRed() * (1-value)     + s2.getRed() * value),
                 (int) (s1.getGreen() * (1-value)   + s2.getGreen() * value),
                 (int) (s1.getBlue() * (1-value)    + s2.getBlue() * value),
                 (int) (s1.getAlpha() * (1-value)   + s2.getAlpha() * value));
    }
    
    public class Particle
    {
        private ParticleType type;
        private ParticlePhysik physik;
        private int lifeTime;
        public float size;
        public Vector pos;
        public Vector speed;
        public float angle;
        public int maxLifeTime;
        public int blendMode;
        private Color color;
        
        protected Particle(ParticleType type, ParticlePhysik physik, Vector pos)
        {
            init(type, physik, pos);
        }
        
        public void init(ParticleType type, ParticlePhysik physik, Vector pos)
        {
            this.type = type;
            this.physik = physik;
            this.lifeTime = 0;
            
            this.pos = new Vector(pos);
            if (this.speed == null) this.speed = new Vector();
            if (this.color == null) this.color = new Color();
        }
 
        public ParticleType getParticleType()
        {
            return type;
        }
        
        public ParticlePhysik getParticlePhysik()
        {
            return physik;
        }
 
        public ParticleSystem getSystem()
        {
            return ParticleSystem.this;
        }
        
        private void update()
        {
            if (lifeTime > 0) lifeTime--;
            physik.update(this);
        }
        
        private void dispose()
        {
        	
        }
    }
    
    public class ThreadAnimator extends Thread
    {
        public ThreadAnimator()
        {
            setName("Particle-Animator");
            setDaemon(true);
        }
        
        @Override
        public void run()
        {
            try
            {
                long time;
                while(true)
                {
                    time = System.nanoTime();
                    update0();
                    
                    long wait = 1000 / FrameTimer.getTargetFPS() - (System.nanoTime() - time) / 1000000000;
                    Thread.sleep(wait);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

	@Override
	public void dispose()
	{
		if (container != null) container.removeGraphic(this);
		for (int i = 0; i < emitterList.size(); i++)
		{
			emitterList.get(i).dispose();
		}
		emitterList.clear();

		for (int i = 0; i < size; i++)
		{
			if (particles[i] != null) particles[i].dispose();
		}
		particles = null;
		size = 0;
	}
}