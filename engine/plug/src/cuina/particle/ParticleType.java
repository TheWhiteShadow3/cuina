package cuina.particle;
 
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.util.LoadingException;

import org.lwjgl.util.ReadableColor;
 
public class ParticleType
{
//  public static final int TYPE_POINT = 0;
//  public static final int TYPE_IMAGE = 1;

	int     minLife = -1;
    int     maxLife = -1;
    float   minSize = 1;
    float	maxSize = 1;
    float	minSpeed;
    float   maxSpeed;
    float   minDir;
    float   maxDir;
    int drawMode	= Image.COMPOSITE_OVERLAY;
    ReadableColor   col1;
    ReadableColor   col2;
    Image image;
    
    public ParticleType()
    {
    }
    
    public void setSize(float min, float max)
    {
        this.minSize = min;
        this.maxSize = max;
    }
    
    public void setSpeed(float min, float max)
    {
        this.minSpeed = min;
        this.maxSpeed = max;
    }
    
    public void setDirection(float min, float max)
    {
        this.minDir = min;
        this.maxDir = max;
    }
    
    public void setLifeTime(int min, int max)
    {
        this.minLife = min;
        this.maxLife = max;
    }
    
    public void setColor(ReadableColor col1, ReadableColor col2)
    {
        this.col1 = col1;
        this.col2 = col2;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }
    
    public void setDrawMode(int drawMode)
	{
		this.drawMode = drawMode;
	}

	public void setImage(String picName)
    {
        try
		{
			this.image = Images.createImage(picName);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
    }
}