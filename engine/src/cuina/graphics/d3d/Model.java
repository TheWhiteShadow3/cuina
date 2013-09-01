package cuina.graphics.d3d;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import cuina.graphics.AbstractGraphic;
import cuina.graphics.D3D;
import cuina.graphics.Graphic;
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Mesh;
import cuina.graphics.Transformation;
import cuina.util.LoadingException;
import cuina.util.Vector;

import org.lwjgl.opengl.GL11;

public class Model extends AbstractGraphic
{
	private static final long serialVersionUID = -5751146964157635096L;
	
	transient private Image 	image;
	
	private Mesh	mesh;
	private String 	fileName = null;
	private int 	modelID;
	
	public Vector 	pos = new Vector();
	public int 		depth = 0;
	
	public Vector 	angle = new Vector();
	public Vector	size = new Vector(1, 1, 1);
	
	public Model() {}
	
	public Model(Mesh mesh, String fileName)
	{
		this.mesh = mesh;
		this.fileName = fileName;
		
		refresh();
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}

	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}
	
	@Override
	public Image getImage()
	{
		return image;
	}

	@Override
	public void setImage(Image image)
	{
		this.image = image;
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
			this.image = Images.createImage(fileName);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void draw()
	{
		if (image == null) return;
		
		D3D.set3DView(true);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, pos.z);
		glScalef(size.x, size.y, size.z);
		
		glRotatef(angle.x, 0.0f, 0.0f, 1.0f);
		glRotatef(angle.y, 0.0f, 1.0f, 0.0f);
		glRotatef(angle.z, 1.0f, 0.0f, 0.0f);
		
		GL11.glCallList(modelID);
        glPopMatrix();
	}

	@Override
	protected void render(Transformation matrix)
	{
		if (matrix != null) matrix.pushTransformation();
		mesh.render(image);
		if (matrix != null) matrix.popTransformation();
	}

	@Override
	public void dispose()
	{
		image.dispose();
		image = null;
	}
}
