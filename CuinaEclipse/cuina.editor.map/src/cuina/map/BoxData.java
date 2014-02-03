package cuina.map;

import java.io.Serializable;


public class BoxData implements Serializable
{
	private static final long serialVersionUID = 7842295701110370417L;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public boolean through;
	public int alphaMask = 1;
}
