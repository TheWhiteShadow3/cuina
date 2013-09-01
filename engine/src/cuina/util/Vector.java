package cuina.util;

import java.io.Serializable;

/**
 * Ein 3D-Vektor mit Fließkommazahlen
 * @author TheWhiteShadow
 */
public class Vector implements Serializable
{
	private static final long serialVersionUID = -1404303485015573688L;
	
	public float x;
	public float y;
	public float z;
	
	public Vector()
	{
		this(0f, 0f, 0f);
	}
	
	public Vector(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(Vector v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public float lenght()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize()
	{
		div(lenght());
	}
	
	public void negate()
	{
		x = -x;
		y = -y;
		z = -z;
	}
	
	public float scalar(Vector v)
	{
		return x*v.x + y*v.y + z*v.z;
	}
	
	public float angle(Vector v)
	{
		return scalar(v) / lenght() * v.lenght();
	}
	
	public float dist(Vector v)
	{
		float x = this.x - v.x;
		float y = this.y - v.y;
		float z = this.z - v.z;
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public Vector cross(Vector v)
	{
		return new Vector(y*v.z - z*v.y,
						  z*v.x - x*v.z,
						  x*v.y - y*v.x);
	}
	
	public void add(Vector v)
	{
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public void sub(Vector v)
	{
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	public void mul(Vector v)
	{
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}
	
	public void div(Vector v)
	{
		x /= v.x;
		y /= v.y;
		z /= v.z;
	}
	
	public void add(float f)
	{
		x += f;
		y += f;
		z += f;
	}
	
	public void sub(float f)
	{
		x -= f;
		y -= f;
		z -= f;
	}
	
	public void mul(float f)
	{
		x *= f;
		y *= f;
		z *= f;
	}
	
	public void div(float f)
	{
		x /= f;
		y /= f;
		z /= f;
	}
//	public void rotate(Vector v, float angle)
//	{
//		float sinA = (float)Math.sin(angle);
//		float cosA = (float)Math.cos(angle);
//		Vector n = new Vector(v);
//		n.normalize();
//		GL11.glm
//		x = x * (n.x*n.x * (1 - cosA) + cosA) + n.x*n.y * (1- cosA)
//	}
}
