package cuina.graphics.transform;

import static org.lwjgl.opengl.GL11.*;
import cuina.util.Vector;

public abstract class Matrix
{
	public static final int PROJECTION = GL_PROJECTION;
	public static final int MODEL = GL_MODELVIEW;
	public static final int TEXTURE = GL_TEXTURE;
	
	public float angle;
	public float x;
	public float y;
	public float z;
	
	public Matrix(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static class Translation extends Matrix
	{
		public Translation(float x, float y)
		{
			super(x, y, 0);
		}
		
		public Translation(float x, float y, float z)
		{
			super(x, y, z);
		}

		@Override
		public void execute()
		{
			glTranslatef(x, y, z);
		}
		
		@Override
		public String toString()
		{
			return "Matrix Translation: " + new Vector(x, y, z);
		}
	}
	
	public static class Rotation extends Matrix
	{
		public Rotation(float angle)
		{
			super(0, 0, 1);
			this.angle = angle;
		}
		
		public Rotation(float angle, float x, float y, float z)
		{
			super(x, y, z);
			this.angle = angle;
		}

		@Override
		public void execute()
		{
			glRotatef(angle, x, y, z);
		}
		
		@Override
		public String toString()
		{
			return "Matrix Rotation: " + angle + "° " + new Vector(x, y, z);
		}
	}
	
	public static class Scale extends Matrix
	{
		public Scale(float x, float y)
		{
			super(x, y, 1);
		}
		
		public Scale(float x, float y, float z)
		{
			super(x, y, z);
		}

		@Override
		public void execute()
		{
			glScalef(x, y, z);
		}
		
		@Override
		public String toString()
		{
			return "Matrix Scale: " + new Vector(x, y, z);
		}
	}
	
	public abstract void execute();
}