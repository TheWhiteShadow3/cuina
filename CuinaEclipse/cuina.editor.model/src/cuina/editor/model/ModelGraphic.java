package cuina.editor.model;

import cuina.animation.ModelData;
import cuina.editor.object.ObjectAdapter;
import cuina.editor.object.ObjectGraphic;
import cuina.gl.Image;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.LWJGLException;

public class ModelGraphic implements ObjectGraphic
{
	private ObjectAdapter objectAdapter;
	private ModelData model;
	private Image image;
	private GLCanvas context;
	
	public ModelGraphic(ObjectAdapter objectAdapter)
	{
		this.objectAdapter = objectAdapter;
	}
	
	@Override
	public void setGLCanvas(GLCanvas canvas)
	{
		this.context = canvas;
	}
	
	@Override
	public Image getImage() throws IllegalStateException
	{
		if (context == null) throw new IllegalStateException();
		
		if (image == null)
		{
			this.model = (ModelData) objectAdapter.getExtension("model");
			if (model == null) return null;
			
			ResourceProvider rp = ResourceManager.getResourceProvider(objectAdapter.getProject());
			try
			{
				Resource res = rp.getResource(ResourceManager.KEY_GRAPHICS, model.fileName);
				image = new Image(context, res.getPath().toString());
			}
			catch (ResourceException | LWJGLException e)
			{
				e.printStackTrace();
			}
		}
		
		return image;
	}

	@Override
	public Rectangle getClipping()
	{
		if (image == null) getImage();
		if (image == null) return null;
		
		int cw = image.getWidth()  / model.frames;
		int ch = image.getHeight()  / model.animations;
		return new Rectangle(cw * model.frame, ch * model.animation, cw, ch);
	}

	@Override
	public Point getOffset()
	{
		if (image == null) getImage();
		if (image == null) return null;
		
		return new Point(-model.ox, -model.oy);
	}

	@Override
	public String getFilename()
	{
		if (model == null) getImage();
		if (model == null) return null;
		
		return model.fileName;
	}
}
