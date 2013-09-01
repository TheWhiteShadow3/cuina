package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

public abstract class Matrix
{
	public static final int PROJECTION = GL_PROJECTION;
	public static final int MODEL = GL_MODELVIEW;
	public static final int TEXTURE = GL_TEXTURE;
	
	public int target;
	
	public float angle;
	public float x;
	public float y;
	public float z;
	
	public Matrix(int target, float x, float y, float z)
	{
		this.target = target;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static class Translation extends Matrix
	{
		public Translation(int target, float x, float y)
		{
			super(target, x, y, 1);
		}
		
		public Translation(int target, float x, float y, float z)
		{
			super(target, x, y, z);
		}

		@Override
		public void execute()
		{
			GLCache.setMatrix(target);
			glTranslatef(x, y, z);
		}
	}
	
	public static class Rotation extends Matrix
	{
		public Rotation(int target, float angle)
		{
			super(target, 0, 0, 1);
			this.angle = angle;
		}
		
		public Rotation(int target, float x, float y, float z, float angle)
		{
			super(target, x, y, z);
			this.angle = angle;
		}

		@Override
		public void execute()
		{
			GLCache.setMatrix(target);
			glRotatef(angle, x, y, z);
		}
	}
	
	public static class Scale extends Matrix
	{
		public Scale(int target, float x, float y)
		{
			super(target, x, y, 1);
		}
		
		public Scale(int target, float x, float y, float z)
		{
			super(target, x, y, z);
		}

		@Override
		public void execute()
		{
			GLCache.setMatrix(target);
			glScalef(x, y, z);
		}
	}
	
	public abstract void execute();
}
