package cuina.data;

import java.io.Serializable;

public class Model implements Serializable
{
	private static final long	serialVersionUID	= 750069947673148008L;
	
	public String fileName = null;
	public int frames = 1;
	public int directions = 1;
	public int frame = 0;
	public int direction = 0;
	public boolean standAnimation = false;
	public int ox = 0;
	public int oy = 0;
}