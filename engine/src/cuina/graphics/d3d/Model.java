package cuina.graphics.d3d;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import cuina.graphics.AbstractGraphic;
import cuina.graphics.GraphicUtil;
import cuina.graphics.GraphicContainer;
import cuina.graphics.Graphics;
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Mesh;
import cuina.graphics.TextureLoader;
import cuina.graphics.transform.Transformation;
import cuina.util.LoadingException;
import cuina.util.Vector;

public class Model extends AbstractGraphic
{
	private static final long serialVersionUID = -5751146964157635096L;
	
	private Mesh	mesh;
	private String 	fileName = null;
	
	public Vector 	pos = new Vector();
	public int 		depth = 0;
	
	public Vector 	angle = new Vector();
	public Vector	size = new Vector(1, 1, 1);
	
	public Model() {}
	
	public Model(Mesh mesh, String fileName, GraphicContainer container)
	{
		this.mesh = mesh;
		this.fileName = fileName;
		container.addGraphic(this);
		
		refresh();
	}
	
	public Model(Mesh mesh, String fileName)
	{
		this(mesh, fileName, Graphics.GraphicManager);
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}

	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}

	public Vector getPos()
	{
		return pos;
	}

	public void setPosition(float x, float y, float z)
	{
		this.pos.set(x, y, z);
	}

	public Vector getAngle()
	{
		return angle;
	}

	public void setAngle(Vector angle)
	{
		this.angle = angle;
	}

	public Vector getSize()
	{
		return size;
	}

	public void setSize(Vector size)
	{
		this.size = size;
	}

	@Override
	public int getDepth()
	{
		return depth;
	}
	
	public Transformation getTransformation()
	{
		return matrix;
	}

	public void setTransformation(Transformation matrix)
	{
		this.matrix = matrix;
	}

	@Override
	public void refresh()
	{
		try
		{
			setImage(Images.createImage(fileName, TextureLoader.ANIOTROPIC));
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void render(Image image)
	{
		GraphicUtil.set3DView(true);
		image.bind();
		
		//TODO: Transform3D Klasse importieren.
		glPushMatrix();
		{
			glTranslatef(pos.x, pos.y, pos.z);
			glScalef(size.x, size.y, size.z);
			
			glRotatef(angle.x, 1.0f, 0.0f, 0.0f);
			glRotatef(angle.y, 0.0f, 1.0f, 0.0f);
			glRotatef(angle.z, 0.0f, 0.0f, 1.0f);
			
			mesh.render(image);
		}
        glPopMatrix();
	}
}
