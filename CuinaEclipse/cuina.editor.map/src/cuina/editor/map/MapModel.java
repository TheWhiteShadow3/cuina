package cuina.editor.map;

import cuina.editor.map.internal.layers.TilemapUtil;
import cuina.editor.map.util.MapOperation;
import cuina.editor.map.util.MapSavePoint;
import cuina.map.Map;

import java.util.Arrays;

/** UNBENUTZT! */
public class MapModel
{
	private Map map;
	private ITerrainEditor editor;
	private MapSavePoint savePoint;
	

	public MapModel(ITerrainEditor editor, Map map)
	{
		this.editor = editor;
		this.map = map;
	}
	
	public void setTiles(int x0, int y0, int layer, TileSelection tiles)
	{
		if (savePoint == null) savePoint = createTerrainBackup();
		
		for(int x = 0; x < tiles.getWidth(); x++)
		for(int y = 0; y < tiles.getHeight(); y++)
		{
			short id = tiles.get(x, y);
			if (id == -1) continue;
			
			map.data[x0 + x][y0 + y][layer] = id;
		}
		TilemapUtil.updateAutotiles(map, tiles, x0, y0, layer);
		addSavePoint();
	}
	
	public void setTile(int x, int y, int layer, short id)
	{
		setTiles(x, y, layer, new TileSelection(x, y, id+1));
	}
	
	private void addSavePoint()
	{
		MapSavePoint newSavePoint = createTerrainBackup();
		editor.addOperation(new MapOperation("Change Tiles", savePoint, newSavePoint));
		savePoint = newSavePoint;
	}
	
	/**
	 * Erstellt eine Backup-Aktion mit allen Terrain-Daten der Map.
	 * @return Wiederherstellungs-Aktion
	 */
	private MapSavePoint createTerrainBackup()
	{
		MapSavePoint action = new MapSavePoint()
		{
			short[][][] aData = copyData(map.data);
			
			@Override
			public void apply()
			{
				map.data = copyData(aData);
				savePoint = this;
			}
		};
		return action;
	}
	
	/**
	 * Gibt eine Kopie der aktuellen Mapdaten zurück.
	 * Diese Methode ist wichtig für die Erstellung eines Backups.
	 * @return Kopie der Map-Daten.
	 */
	private short[][][] copyData(final short[][][] data)
	{
		int width = data.length;
		int height = data[0].length;
		short[][][] copy = new short[width][height][];
		
		for(int x = 0; x < width; x++)
		for(int y = 0; y < height; y++)
		{
			copy[x][y] = Arrays.copyOf(data[x][y], data[0][0].length);
		}
		return copy;
	}
}
