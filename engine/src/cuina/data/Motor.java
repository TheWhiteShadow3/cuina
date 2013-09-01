package cuina.data;

import java.io.Serializable;

public class Motor implements Serializable
{
	private static final long	serialVersionUID	= -1467683216524746055L;
	
	/**
	 * Typ des Motors:
	 * <ol>
	 * <li>Einfacher Motor</li>
	 * <li>Charakter Motor</li>
	 * <li>Spieler Motor</li>
	 * </ol>
	 */
	public int motorType = 0;
	public int moveSpeed = 48;
	public int direction = 270;
	public int moveType = 0;
}
