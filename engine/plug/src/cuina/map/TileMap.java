/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.map;

import cuina.Game;
import cuina.Logger;
import cuina.graphics.GraphicContainer;
import cuina.graphics.ImageSet;
import cuina.graphics.Sprite;
import cuina.graphics.Texture;
import cuina.graphics.TextureLoader;
import cuina.map.TileFactory.AutotileSet;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;
import cuina.util.ResourceManager.Resource;
import cuina.world.CuinaWorld;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class TileMap implements Serializable
{
	private static final long	serialVersionUID	= -6640646679566822976L;
	
	private String fileName;
	private int tileSize;
	private String[] autotileNames;
	
	private ImageSet imageSet;
	private Sprite[][][] sprites;
	private ImageSet[][] autotileSets;
	
	private short[][][] data;
	private byte[] priorities;
	private int x;
	private int y;
	private int atSpeed = 10;
	private int atTime;
	private int atFrame;
	private boolean visible = true;

//	private GraphicContainer container;
	
	public TileMap(String fileName, String[] autotileNames, int tileSize)
	{
		this.fileName = fileName;
		this.autotileNames = autotileNames;
		this.tileSize = tileSize;
	}

	public short[][][] getData()
	{
		return data;
	}

	public void setData(short[][][] data)
	{
		this.data = data;
		sprites = new Sprite[data.length][data[0].length][data[0][0].length];
		
		// Zähle benötigte Sprites
//		int count = 0;
//		for(int x = 0; x < data.length; x++)
//		for(int y = 0; y < data[0].length; y++)
//		for(int z = 0; z < data[0][0].length; z++)
//			if (data[x][y][z] > 0) count++;
//		Graphics.GraphicManager.ensureCapacity(count);
		
		refresh();
	}

	public byte[] getPriorities()
	{
		return priorities;
	}

	public void setPriorities(byte[] priorities)
	{
		this.priorities = priorities;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
	
	public ImageSet getImageSet()
	{
		return imageSet;
	}
	
	public ImageSet getAutotileImage(int index, int frame)
	{
		return autotileSets[index][frame];
	}
	
	public void refresh()
	{
		if (sprites == null) return;
		
		try
		{
			if (imageSet == null)
			{
				BufferedImage image = ResourceManager.loadImage(fileName);
				int xt = image.getWidth() / tileSize;
				int yt = image.getHeight() / tileSize;
				imageSet = new ImageSet(fileName, xt, yt);
			}
			else imageSet.refresh();
			createAutotilesImageSets(autotileNames);
		}
		catch (LoadingException e)
		{
			Logger.log(TileMap.class, Logger.ERROR, e);
			return;
		}
		
		GraphicContainer container = GameMap.getInstance().getGraphicContainer();
		
		int id;
		Sprite sprite;
		for(int x = 0; x < data.length; x++)
		for(int y = 0; y < data[0].length; y++)
		for(int z = 0; z < data[0][0].length; z++)
		{
			id = data[x][y][z];
			if (id > 0)
			{
				sprite = sprites[x][y][z];
				if(id < 30000)
				{
					sprite = imageSet.createSprite(
							(id-1) % imageSet.getXCount(), (id-1) / imageSet.getXCount(), container);
				}
				else
				{
					int autotileID = (id - 30000);
					// Prüfe, ob zur ID auch ein AutotileSet existiert.
					if (autotileSets[autotileID / 48] == null) continue;
					sprite = autotileSets[autotileID / 48][atFrame].
						createSprite((autotileID % 48) % imageSet.getXCount(),
									 (autotileID % 48) / imageSet.getXCount(),
									 container);
				}
				sprite.setX(x * tileSize);
				sprite.setY(y * tileSize);
				if (priorities != null && priorities.length > id)
				{
					sprite.setDepth((int) sprite.getY() + priorities[id] * (tileSize + 1));
				}
				sprites[x][y][z] = sprite;
			}
		}
	}
	
	public void setVisible(boolean value)
	{
		visible = value;
		if (sprites == null) return;
		
		for(int x = 0; x < data.length; x++)
		{
			for(int y = 0; y < data[0].length; y++)
			{
				for(int z = 0; z < data[0][0].length; z++)
				{
					sprites[x][y][z].setVisible(visible);
				}
			}
		}
	}
	
	public void update()
	{
		CuinaWorld world = Game.getWorld();
		if (sprites == null || world == null) return;
		
		int id;
		Sprite sprite;
		for(int x = 0; x < data.length; x++)
		for(int y = 0; y < data[0].length; y++)
		for(int z = 0; z < data[0][0].length; z++)
		{
			id = data[x][y][z];
			sprite = sprites[x][y][z];
			if (sprite != null && id > 0)
			{
//				sprite.setX(x * tileSize);
//				sprite.setY(y * tileSize);
				if (id < 30000) // TODO: Implementiere Priorität für Autotiles
				{
					sprite.setDepth((int)sprite.getY() + priorities[id] * (tileSize + 1));
				}
				else
				{
					int autotileID = (id - 30000);
					int frame = atFrame % autotileSets[autotileID / 48].length;
					ImageSet.setImage(sprite, autotileSets[autotileID / 48][frame],
							(autotileID % 48) % imageSet.getXCount(),
							(autotileID % 48) / imageSet.getXCount());
					sprite.setDepth((int)sprite.getY());
				}
			}
		}
		atTime += atSpeed;
		if (atTime > 100)
		{
			atTime -= 100;
			atFrame += 1;
		}
	}

	public void dispose()
	{
		sprites = null;
		data = null;
		priorities = null;
		if (imageSet != null) imageSet.dispose();
		imageSet = null;
	}
	
	private void createAutotilesImageSets(String[] autotiles)
	{
		if(autotiles != null)
		{
			autotileSets = new ImageSet[autotiles.length][];

			for(int i = 0; i < autotiles.length; i++)
			{
				if (autotiles[i] == null) continue;
				try
				{
					autotileSets[i] = AutotileImageSet.createSetArray(autotiles[i], tileSize);
				}
				catch (LoadingException e)
				{
					Logger.log(TileMap.class, Logger.ERROR, e);
				}
			}
		}
	}
	
	private static class AutotileSetData implements Serializable
	{
		private static final long serialVersionUID = 4917247883220029606L;
		
		final String autotileName;
		final int tileSize;
		
		transient AutotileSet set;

		public AutotileSetData(String autotileName, int tileSize)
		{
			this.autotileName = autotileName;
			this.tileSize = tileSize;
		}
	}
	
	private static class AutotileImageSet extends ImageSet
	{
		private static final long serialVersionUID = 8939791093383139741L;
		
		private AutotileSetData data;
		private int index;
		
		private AutotileImageSet(AutotileSetData data, int index) throws LoadingException
		{
			super(8, 6);
			this.data = data;
			this.index = index;
			refresh();
		}
		
		@Override
		public void refresh()
		{
			try
			{
				if (data.set == null)
				{
					Resource res = ResourceManager.getResource(ResourceManager.KEY_GRAPHICS, data.autotileName);
					data.set = TileFactory.createAutotileSet(res, data.tileSize);
				}
					
				Texture texture = getTexture(data);
				createImages(texture);
			}
			catch (LoadingException e)
			{
				e.printStackTrace();
			}
		}
		
		private Texture getTexture(AutotileSetData data)
		{
			return TextureLoader.getInstance().getTexture(
					data.set.getFrame(index), 0, data.autotileName + Integer.toString(index));
		}

		public static ImageSet[] createSetArray(String autotileName, int tileSize)
				throws LoadingException
		{
			AutotileSetData sharedDataHolder = new AutotileSetData(autotileName, tileSize);
			Resource res = ResourceManager.getResource(ResourceManager.KEY_GRAPHICS, autotileName);
			sharedDataHolder.set = TileFactory.createAutotileSet(res, tileSize);
			
			AutotileImageSet[] sets = new AutotileImageSet[4];
			
			for (int i = 0; i < sharedDataHolder.set.frameCount(); i++)
			{
				sets[i] = new AutotileImageSet(sharedDataHolder, i);
			}
			return sets;
		}
	}
}
