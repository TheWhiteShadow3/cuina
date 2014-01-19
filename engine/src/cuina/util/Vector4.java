package cuina.util;

public class Vector4 extends Vector
{
	private static final long serialVersionUID = -8064363425651339115L;
	
	private float w;
	
	public Vector4()
	{
		this(0f, 0f, 0f, 0f);
	}
	
	public Vector4(float x, float y, float z, float w)
	{
		set(x, y, z, w);
	}
	
	public Vector4(Vector4 v)
	{
		set(v);
	}
	
	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(Vector4 v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}

	@Override
	public float lenght()
	{
		return (float)Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	@Override
	public void normalize()
	{
		div(lenght());
	}
	
	@Override
	public void negate()
	{
		x = -x;
		y = -y;
		z = -z;
		w = -w;
	}
	
	public float scalar(Vector4 v)
	{
		return x*v.x + y*v.y + z*v.z + w*v.w;
	}
	
	@Override
	public float angle(Vector v)
	{
		return scalar(v) / lenght() * v.lenght();
	}
	
	@Override
	public float dist(Vector v)
	{
		float x = this.x - v.x;
		float y = this.y - v.y;
		float z = this.z - v.z;
		float w = this.w;
		return (float)Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	public float dist(Vector4 v)
	{
		float x = this.x - v.x;
		float y = this.y - v.y;
		float z = this.z - v.z;
		float w = this.w - v.w;
		return (float)Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	public void add(Vector4 v)
	{
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
	}
	
	public void sub(Vector4 v)
	{
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
	}
	
	public void mul(Vector4 v)
	{
		x *= v.x;
		y *= v.y;
		z *= v.z;
		w *= v.w;
	}
	
	public void div(Vector4 v)
	{
		x /= v.x;
		y /= v.y;
		z /= v.z;
		w /= v.w;
	}
	
	@Override
	public void add(float f)
	{
		super.add(f);
		w += f;
	}
	
	@Override
	public void sub(float f)
	{
		super.sub(f);
		w -= f;
	}
	
	@Override
	public void mul(float f)
	{
		super.mul(f);
		w *= f;
	}
	
	@Override
	public void div(float f)
	{
		super.div(f);
		w /= f;
	}

	@Override
	public String toString()
	{
		return "V(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
