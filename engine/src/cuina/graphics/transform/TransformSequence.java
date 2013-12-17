package cuina.graphics.transform;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import cuina.graphics.GLCache;
import cuina.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;

public class TransformSequence implements Iterable<Matrix>, Transformation
{
	private final int target;
	private ArrayList<Matrix> sequence = new ArrayList<Matrix>(4);
	
	public TransformSequence(int target)
	{
		this.target = target;
	}
	
	public TransformSequence(int target, Matrix... seq)
	{
		this(target);
		for(int i = 0; i < seq.length; i++)
		{
			sequence.add(seq[i]);
		}
	}
	
	public void add(Matrix t)
	{
		sequence.add(t);
	}

	public int size()
	{
		return sequence.size();
	}

	public int getTarget()
	{
		return target;
	}

	public Matrix get(int index)
	{
		return sequence.get(index);
	}

	public Matrix remove(int index)
	{
		return sequence.remove(index);
	}

	@Override
	public Iterator<Matrix> iterator()
	{
		return sequence.iterator();
	}
	
	@Override
	public void pushTransformation()
	{
        GLCache.setMatrix(target);
        glPushMatrix();
        
		for(int i = 0; i < sequence.size(); i++)
		{
			sequence.get(i).execute();
		}
	}
	
	@Override
	public void popTransformation()
	{
        GLCache.setMatrix(target);
        glPopMatrix();
	}
	
	public TransformSequence translate(Vector v)
	{
		add(new Matrix.Translation(v.x, v.y, v.z));
		return this;
	}
	
	public TransformSequence translate(float x, float y, float z)
	{
		add(new Matrix.Translation(x, y, z));
		return this;
	}
	
	public TransformSequence rotate(float angle, Vector v)
	{
		add(new Matrix.Rotation(angle, v.x, v.y, v.z));
		return this;
	}
	
	public TransformSequence rotate(float angle, float x, float y, float z)
	{
		add(new Matrix.Rotation(angle, x, y, z));
		return this;
	}
	
	public TransformSequence scale(Vector v)
	{
		add(new Matrix.Scale(v.x, v.y, v.z));
		return this;
	}
	
	public TransformSequence scale(float x, float y, float z)
	{
		add(new Matrix.Scale(x, y, z));
		return this;
	}
	
	public static void main(String[] args)
	{
		// teste TransformSequenz
		TransformSequence seq = new TransformSequence(Matrix.MODEL);
		seq.translate(10, 5, 0).rotate(15, new Vector(0, 1, 0)).translate(-10, -5, 0).scale(2, 2, 2);
		
		for (Matrix m : seq)
		{
			System.out.println(m);
		}
	}
}