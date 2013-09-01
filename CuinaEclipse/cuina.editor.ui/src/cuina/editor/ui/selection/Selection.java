package cuina.editor.ui.selection;
 
import java.awt.Dimension;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
 
/**
 * Stellt das Auswahlrechteck auf der Karte da.
 * @author TheWhiteShadow
 */
public class Selection
{
	private java.awt.Rectangle	rect	= new java.awt.Rectangle();
	private java.awt.Rectangle	oldRect = new java.awt.Rectangle();
	    
	private java.awt.Rectangle  view	= new java.awt.Rectangle();
	private Image				image;
	private boolean				disposed;
	private Object				data;

	public Selection() {}

	public Selection(int x, int y, int width, int height)
	{
		setBounds(x, y, width, height);
	}

	public Selection(Rectangle rect)
	{
		setBounds(rect);
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	public void dispose()
	{
		data = null;
		this.disposed = true;
	}

	public void refresh()
	{
		oldRect.setBounds(rect);
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}
 
    public Rectangle getBounds()
    {
        return new Rectangle(rect.x, rect.y, rect.width, rect.height);
    }
 
    public int getX()
    {
        return rect.x;
    }
    
    public int getY()
    {
        return rect.y;
    }
    
    public int getWidth()
    {
        return rect.width;
    }
    
    public int getHeight()
    {
        return rect.height;
    }
 
    /**
     * Gibt die relative Position der Auswahl zur Dragposition zurück.
     * Die Position entspricht der oberen linken Ecke des Auswahlrechtecks.
     * @return Position der Auswahl.
     */
    public Point getLocation()
    {
        return new Point(rect.x, rect.y);
    }
 
    /**
     * Setzt die relative Position der Auswahl zur Dragposition.
     * Die Position entspricht der oberen linken Ecke des Auswahlrechtecks.
     * @param x X-Position
     * @param y Y-Position
     * @see #getLocation()
     */
    public void setLocation(int x, int y)
    {
        rect.x = x;
        rect.y = y;
    }
    
    public void setLocation(int x, int y, int rasterSize, int rOx, int rOy)
    {
        if (rasterSize <= 1)
        {
            rect.x = x;
            rect.y = y;
        }
        else
        {
            rect.x = ((x - rOx) / rasterSize * rasterSize) + rOx;
            rect.y = ((y - rOy) / rasterSize * rasterSize) + rOy;
        }
    }
    
    public Dimension getSize()
    {
        return rect.getSize();
    }
    
    public void add(Point point)
    {
        rect.add(point.x, point.y);
    }
 
    public void setBounds(Rectangle rect)
    {
        setBounds(rect.x, rect.y, rect.width, rect.height);
    }

	public void setBounds(int x, int y, int width, int height)
	{
		rect.setBounds(x, y, width, height);
	}

    public void setBounds(int x, int y, int width, int height, int rasterSize, int rOx, int rOy)
    {
        if (rasterSize <= 1)
        {
        	rect.setBounds(x, y, width, height);
        }
        else
        {
            rect.x = ((x - rOx) / rasterSize * rasterSize) + rOx;
            rect.y = ((y - rOy) / rasterSize * rasterSize) + rOy;
            rect.width  = width  / rasterSize * rasterSize;
            rect.height = height / rasterSize * rasterSize;
        }
    }

	public void setSize(int width, int height)
	{
		rect.setSize(width, height);
	}

	public boolean contains(int x, int y)
	{
		return rect.contains(x, y);
	}

	public void setImage(Image image)
	{
		if (image == null)
		{
			this.image = null;
			setImageView(null);
		}
		else
		{
			setImage(image, new Rectangle(0, 0, image.getImageData().width, image.getImageData().height));
		}
	}

	public void setImage(Image image, Rectangle view)
	{
		this.image = image;
		setImageView(view);
	}

	public void setImageView(Rectangle view)
	{
		if (view == null)
		{
			this.view.setSize(0, 0);
		}
		else
		{
			this.view.setBounds(view.x, view.y, view.width, view.height);
			rect.setSize(view.width, view.height);
		}
	}

    public Image getImage()
    {
        return image;
    }
    
    public Rectangle getImageView()
    {
        return new Rectangle(view.x, view.y, view.width, view.height);
    }
 
    public boolean needRefresh()
    {
        return !oldRect.equals(rect);
    }
    
    /**
     * Gibt die Vereinigung aus alter und neuer Hülle zurück.
     * @return Modifikations-Hülle.
     */
    protected Rectangle getModificationBounds()
    {
        java.awt.Rectangle result = new java.awt.Rectangle(rect);
        result.add(oldRect);
        return new Rectangle(result.x, result.y, result.width, result.height);
    }
    
    public void clear()
    {
        rect.setSize(0, 0);
        view.setSize(0, 0);
        data = null;
        image = null;
    }
 
    @Override
    public String toString()
    {
        return "Auswahl: " + rect.toString();
    }
}