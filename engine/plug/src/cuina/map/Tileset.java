package cuina.map;

import cuina.database.DatabaseObject;

import java.util.Arrays;
public class Tileset implements DatabaseObject
{
	private static final long serialVersionUID = 2507660333557660858L;

	public static final int 	AUTOTILES_OFFSET	= 30000;
	public static final int 	AUTOTILES_COUNT		= 10;
	
	private String key;
	private String name = "";
	private int tileSize = 32;
	private String tilesetName = "";
	private String backgroundName = "";
	private int BackSpeedX = 0;
	private int BackSpeedY = 0;
	private String[] autotiles = new String[AUTOTILES_COUNT];
	private short[] passages 	= new short[8];
	private byte[]	priorities 	= new byte[8];
	private byte[]	tileFlags 	= new byte[8];
	private byte[]	terrainTags	= new byte[8];

	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Dateiname der Grafik.
	 * @return Tileset-Grafik-Pfad
	 */
	public String getTilesetName()
	{
		return tilesetName;
	}

	public void setTilesetName(String tilesetName)
	{
		this.tilesetName = tilesetName;
	}

	public String getBackgroundName()
	{
		return backgroundName;
	}

	public void setBackgroundName(String backgroundName)
	{
		this.backgroundName = backgroundName;
	}

	public int getBackSpeedX()
	{
		return BackSpeedX;
	}

	public void setBackSpeedX(int backSpeedX)
	{
		BackSpeedX = backSpeedX;
	}

	public int getBackSpeedY()
	{
		return BackSpeedY;
	}

	public void setBackSpeedY(int backSpeedY)
	{
		BackSpeedY = backSpeedY;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public String[] getAutotiles()
	{
		return autotiles;
	}
	
	public void setAutotiles(String[] autotiles)
	{
		this.autotiles = autotiles;
	}

	public short[] getPassages()
	{
		return passages;
	}

	public void setPassages(short[] passages)
	{
		this.passages = passages;
	}

	public byte[] getPriorities()
	{
		return priorities;
	}

	public void setPriorities(byte[] priorities)
	{
		this.priorities = priorities;
	}

	public byte[] getFlags()
	{
		return tileFlags;
	}

	public void setFlags(byte[] tileFlags)
	{
		this.tileFlags = tileFlags;
	}

	public byte[] getTerrainTags()
	{
		return terrainTags;
	}

	public void setTerrainTags(byte[] terrainTags)
	{
		this.terrainTags = terrainTags;
	}
	
	public void resizeTileset(int xSize, int ySize)
	{
		if (passages != null && ySize * xSize + 1 == passages.length) return;
		passages 	= Arrays.copyOf(passages, ySize * xSize + 1);
		priorities 	= Arrays.copyOf(priorities, ySize * xSize + 1);
		tileFlags 	= Arrays.copyOf(tileFlags, ySize * xSize + 1);
		terrainTags = Arrays.copyOf(terrainTags, ySize * xSize + 1);
	}

	@Override
	public String toString()
	{
		return "Tileset [name=" + name + ", tileSize=" + tileSize + "]";
	}
}
