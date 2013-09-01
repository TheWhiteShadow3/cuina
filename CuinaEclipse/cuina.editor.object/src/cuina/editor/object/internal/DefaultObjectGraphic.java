package cuina.editor.object.internal;

import cuina.editor.object.ObjectGraphic;
import cuina.gl.Image;
import cuina.resource.ResourceException;

import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.LWJGLException;

public class DefaultObjectGraphic implements ObjectGraphic
{
	private Image image;
	private GLCanvas context;
	
	@Override
	public void setGLCanvas(GLCanvas canvas)
	{
		this.context = canvas;
	}
	
	@Override
	public Image getImage() throws IllegalStateException
	{
		if (context == null) throw new IllegalStateException();
		
		if (image == null) try
		{
			this.image = new Image(context, "");
		}
		catch (ResourceException | LWJGLException e)
		{
			throw new IllegalStateException(e);
		}
		
		return image;
	}

	@Override
	public Rectangle getClipping()
	{
		if (image == null) return null;
		
		return new Rectangle(image.getWidth(), image.getHeight());
	}

	@Override
	public Point getOffset()
	{
		return new Point();
	}

	@Override
	public String getFilename()
	{
		return null;
	}
}
