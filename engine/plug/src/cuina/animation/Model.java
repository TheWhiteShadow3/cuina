package cuina.animation;
 
import cuina.Context;
import cuina.Game;
import cuina.graphics.GraphicContainer;
import cuina.graphics.PictureSprite;
import cuina.graphics.Sprite;
import cuina.plugin.LifeCycle;
import cuina.plugin.Priority;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import java.awt.image.BufferedImage;

@Priority(updatePriority=-100)
public class Model implements ModelIF, LifeCycle
{
    private static final long   serialVersionUID    = 1688441863984748766L;
    
    private CuinaObject		object;
    private PictureSprite     sprite;
    private String          fileName;
    private Animator        animator;
    private int frames;
    private int animations;
    private int cw; // Cell-Width
    private int ch; // Cell-Height
 
    private int frameIndex = 0;
    private int animationIndex = 0;
    private int frameTime = 10;
    private int animationTimer = -1;
    private float x;
    private float y;
    private float z;
    private boolean animate;
//  private int ox; // Offset-X
//  private int oy; // Offset-Y
    
    public Model() {}
    
    public Model(String fileName, int frames, int directions, boolean animate)
    {
//      this.object = object;
        this.fileName = fileName;
        this.animate = animate;
        this.frames = frames;
        this.animations = directions;
        this.animate = animate;
        loadImage();
    }
    
    public Model(Model clone)
    {
        this.animator       = clone.animator;
        this.fileName       = clone.fileName;
        this.frames         = clone.frames;
        this.animations     = clone.animations;
        this.frameIndex     = clone.frameIndex;
        this.animationIndex = clone.animationIndex;
        this.x              = clone.x;
        this.y              = clone.y;
        this.z              = clone.z;
        
        loadImage();
    }
    
	@Override
	public void init()
	{
		update();
	}
    
	@Override
	public CuinaObject getObject()
	{
		return object;
	}

	public void setObject(CuinaObject object)
	{
		this.object = object;
	}
	
	@Override
	public BufferedImage getRawImage()
	{
		try
		{
			return ResourceManager.loadImage(fileName);
		}
		catch (LoadingException e)
		{
//			e.printStackTrace();
			return null;
		}
	}

//  public Model(MapObject object, String fileName, int frames, int directions)
//  {
//      this(object, fileName, frames, directions, "");
//  }
    
	@Override
	public Animator getAnimator()
	{
		return animator;
	}

	@Override
	public void setAnimator(Animator animator)
	{
		this.animator = animator;
		if (animator != null) animator.init(this);
	}
 
    @Override
    public void refresh()
    {
        loadImage();
    }
    
    private void loadImage()
    {
        if (frames == 0 || animations == 0)
        {
            int frameStart = fileName.indexOf("+");
            if (frameStart > 0)
            {
                int ul = fileName.lastIndexOf("_");
                int dot = fileName.lastIndexOf(".");
                int frameEnd = (ul == -1) ? dot : ul;
                String countStr = fileName.substring(frameStart + 1, frameEnd);
                frames = Integer.parseInt(countStr.substring(0, countStr.length() / 2));
                animations = Integer.parseInt(countStr.substring(countStr.length() / 2, frameEnd - frameStart - 1));
            }
            else
            {
                frames = 1;
                animations = 1;
            }
        }
        
        try
        {
        	GraphicContainer container = Game.getContext(Context.SESSION).
        			<CuinaWorld>get(CuinaWorld.INSTANCE_KEY).getGraphicContainer();
            sprite = new PictureSprite(fileName, container);
            cw = sprite.getImage().getWidth() / frames;
            ch = sprite.getImage().getHeight() / animations;
            setOffset(cw / 2, ch);
            
//          setPosition();
        }
        catch (LoadingException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public int getWidth()
    {
        return cw;
    }
    
    @Override
    public int getHeight()
    {
        return ch;
    }
 
    @Override
    public int getFrameCount()
    {
        return frames;
    }
 
    @Override
    public int getAnimationCount()
    {
        return animations;
    }
 
    @Override
    public void setFrame(int frame)
    {
        this.frameIndex = frame;
    }
    
    @Override
	public int getFrame()
    {
        return this.frameIndex;
    }
    
    @Override
    public void setAnimationIndex(int index)
    {
        this.animationIndex = index;
    }
    
    @Override
	public int getAnimationIndex()
    {
        return animationIndex;
    }
    
    @Override
	public void setFrameTime(int frameTime)
    {
        this.frameTime = frameTime;
    }
 
    @Override
    public Sprite getSprite()
    {
        return sprite;
    }
    
    @Override
    public boolean isAnimate()
    {
        return animate;
    }
    
    @Override
    public void setAnimate(boolean animate)
    {
        this.animate = animate;
    }
    
    @Override
    public void setOffset(float ox, float oy)
    {
        sprite.setOX(ox);
        sprite.setOY(oy);
    }
 
    @Override
    public void update()
    {
        if (animator != null) animator.update();
        
        if (animate)
        {
            // überbrücke Startverzögerung der Animation
            if (animationTimer == -1)
            {
                animationTimer = frameTime / 2;
                frameIndex = 0;
                return;
            }
            
            animationTimer++;
            if (animationTimer >= frameTime)
            {
                frameIndex++;
                if (frameIndex >= frames)
                {
                    if (animator != null) animator.animationFinished();
                    frameIndex = 0;
                }
                animationTimer = 0;
            }
        }
        setPosition(object.getX(), object.getY(), object.getZ());
    }
    
	@Override
	public void postUpdate()
	{
		setPosition(object.getX(), object.getY(), object.getZ());
	}
    
    /**
     * Setzt die Animations-Zeit zurück.
     */
    @Override
	public void resetAnimation()
    {
        animationTimer = -1;
    }
//  
//  /**
//   * Animiert das Model entsprechend dem Zustand und der bewegten Entfernung.
//   * @param dx Horizontal bewegte Strecke.
//   * @param dy Vertiktal bewegte Strecke.
//   */
//  @Override
//  public void update(float dx, float dy)
//  {
//      // Bewegungs-Animation ausführen
//      if (standAni || nextAni != -1 || Math.abs(dx) + Math.abs(dy) != 0)
//      {
//          stop = false;
//          // überbrücke Startverzögerung der Animation
//          if (animationTimer == -1)
//          {
//              animationTimer = 0;
//              frameIndex = 0;
//              return;
//          }
//          
//          animationTimer++;
//          if (animationTimer >= frameTime)
//          {
//              frameIndex++;
//              if (frameIndex >= frames)
//              {
//                  frameIndex = 0;
//                  if (aniCallback != null)
//                  {
//                      aniCallback.call(null);
//                      aniCallback = null;
//                      // setze diesen Frame aus, damit das Event reagieren kann. 
//                      return;
//                  }
//              }
//              animationTimer = 0;
//              
//              if (nextAni != -1)
//              {
//                  setAnimationIndex(nextAni);
//                  nextAni = -1;
//              }
//          }
//      }
//      else
//      {   // Objekt steht
//          if (!stop)
//          {
//              frameIndex = 0;
//              stop = true;
//          }
//          animationTimer = -1;
//      }
//      
////        Rectangle rect = object.getCollisionMask().getBounds();
////        sprite.image.setColor(new Color(255, 0, 0));
////        sprite.image.drawRect(rect.x, rect.y + 16, rect.width, rect.height, true);
//  }
 
//  public void draw(int x, int y)
//  {
//      sprite.draw();
//  }
    
//  @Override
//  public void updatePosition()
//  {
////        if (object != null)
////        {
////            setPosition(object.getX(), object.getY(), object.getZ());
////        }
//  }

    @Override
	public float getZ()
    {
        return z;
    }
 
    public void setZ(float z)
    {
        this.z = z;
    }
 
    @Override
    public float getX()
    {
        return x;
    }
 
    @Override
    public float getY()
    {
        return y;
    }
 
    @Override
    public void setVisible(boolean value)
    {
        sprite.setVisible(value);
    }

	@Override
	public boolean isVisible()
	{
		return sprite.isVisible();
	}

	@Override
	public void setPosition(float x, float y, float z)
	{
		// Position des Models setzen
		this.x = Math.round(x);
		this.y = Math.round(y);
		this.z = z;
		
		if (Game.contextExists(Context.SESSION))
		{
			CuinaWorld world = Game.getWorld();
			if (world != null)
			{
				this.x -= world.getScrollX();
				this.y -= world.getScrollY();
			}
		}

		if (sprite == null || sprite.getImage() == null) return;
		// Animations-Bild anwählen
		sprite.getImage().setRectangle((frameIndex % frames) * cw, (animationIndex % animations) * ch, cw, ch);

		// Bild-Position (Seperat um Abweichung zu ermöglichen)
		sprite.setX(this.x);
		sprite.setY(this.y);
		float dz = sprite.getY();
		sprite.setDepth((int) (this.z + dz));
	}

	public void setAngle(float angle)
	{
		sprite.setAngle(angle);
	}
    
    @Override
    public void dispose()
    {
        if (sprite != null) sprite.dispose();
    }
 
    @Override
    public String toString()
    {
        return "Model[" + fileName + ", frames=" +
                          frames + ", directions=" +
                          animations + "]";
    }
 
    @Override
    public float getOX()
    {
        return sprite.getOX();
    }
 
    @Override
    public float getOY()
    {
        return sprite.getOY();
    }
}