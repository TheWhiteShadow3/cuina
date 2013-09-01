package cuina.graphics;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.ArrayList;
import java.util.Iterator;

public class TransformSequence implements Iterable<Matrix>, Transformation
{
	private ArrayList<Matrix> sequence = new ArrayList<Matrix>(4);
	
	public TransformSequence() {}
	
	public TransformSequence(Matrix... seq)
	{
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
        GLCache.setMatrix(Matrix.MODEL);
        glPushMatrix();
        GLCache.setMatrix(Matrix.TEXTURE);
        glPushMatrix();
        
		for(int i = 0; i < sequence.size(); i++)
		{
			sequence.get(i).execute();
		}
	}
	
	@Override
	public void popTransformation()
	{
        GLCache.setMatrix(Matrix.TEXTURE);
        glPopMatrix();
        GLCache.setMatrix(Matrix.MODEL);
        glPopMatrix();
	}
}
