package cuina.graphics;

import cuina.graphics.transform.Transformation;

import org.lwjgl.opengl.GL11;

/**
 * Abstrakte Basis-Implementierung der Klasse {@link Graphic}.
 * 
 * @author TheWhiteShadow
 */
@SuppressWarnings("serial")
public abstract class AbstractGraphic implements Graphic
{
	transient Image 	image;
	private int 		depth;
	
	private boolean		visible = true;
	
	protected Transformation matrix;
	
	private int 		modelID;
	private boolean		cached = false;
	
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

	public boolean isCached()
	{
		return cached;
	}

	public void setCached(boolean cached)
	{
		this.cached = cached;
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

		if (!cached) transformAndRender(matrix);
		else
		{
			if (modelID == 0)
			{
				modelID = GL11.glGenLists(1);
				GL11.glNewList(modelID, GL11.GL_COMPILE_AND_EXECUTE);
				transformAndRender(matrix);
				GL11.glEndList();
			}
			else
			{
				GL11.glCallList(modelID);
			}
		}
	}
	
	/**
	 * Transformiert die Grafik mit der angegebenen Matrix-Transformation.
	 * @param image Das Image.
	 * @param matrix Die Transformation.
	 */
	protected void transformAndRender(Transformation matrix)
	{
		if (matrix != null) matrix.pushTransformation();
		render(image);
		if (matrix != null) matrix.popTransformation();
	}
	
	/**
	 * Rendert die Grafik mit dem angegebenen Image.
	 * <p>
	 * Die Default-Implementation zeichnet das Image in nativer Auflösung auf dem Bildschirm.
	 * </p>
	 * @param image Das Image.
	 * @param matrix Die Transformation.
	 */
	protected void render(Image image)
	{
		Image.renderImage(image);
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
