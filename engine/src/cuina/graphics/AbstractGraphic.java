package cuina.graphics;

import org.lwjgl.opengl.GL11;

@SuppressWarnings("serial")
public abstract class AbstractGraphic implements Graphic
{
	transient Image 	image;
	private int 		depth;
	
	public boolean		visible = true;
	
	protected Transformation matrix;
	
	private int 		modelID;
	public boolean		useGenList = false;
	
	private GraphicContainer container;

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

	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		disposeModel();
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
	
	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	@Override
	public void draw()
	{
		if (!visible) return;
		if (image == null)
		{
			refresh();
			if (image == null) return;
		}

		if (!useGenList) render(matrix);
		else
		{
			if (modelID == 0)
			{
				modelID = GL11.glGenLists(1);
				GL11.glNewList(modelID, GL11.GL_COMPILE_AND_EXECUTE);
				render(matrix);
				GL11.glEndList();
			}
			else
			{
				GL11.glCallList(modelID);
			}
		}
	}
	
	protected void render(Transformation matrix)
	{
		image.draw(matrix);
	}
	
	@Override
	public void dispose()
	{
		if (container != null) container.removeGraphic(this);
		if (image != null) image.dispose();
		image = null;
		disposeModel();
	}
	
	private void disposeModel()
	{
		if (modelID != 0) GL11.glDeleteLists(modelID, 1);
		modelID = 0;
	}
	
	public boolean isDisposed()
	{
		return image == null && modelID == 0;
	}
}
