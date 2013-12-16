package cuina.graphics;

import java.awt.Font;
import java.util.HashMap;

import cuina.util.BitmapFontRenderer;
import cuina.util.LoadingException;

public class BitmapFont
{
	private static HashMap<Font, BitmapFont> fontCache = new HashMap<Font, BitmapFont>();
	
	private final Font font;
	private int[] widths;
	private int nativeWidth;
	
	private ImageSet imageSet;
	
	public static BitmapFont getBitmapFont(Font font) throws LoadingException
	{
		if (font == null) throw new NullPointerException();
		BitmapFont bf = fontCache.get(font);
		if (bf == null)
		{
			bf = new BitmapFont(font);
			fontCache.put(font, bf);
		}
		return bf;
	}
	
	private BitmapFont(Font font) throws LoadingException
	{
		if (font == null) throw new NullPointerException();
		System.out.println("[BitmapFont] create font: " + font);
		this.font = font;
		widths = new int[256];
		Texture tex = TextureLoader.getInstance().getTexture(BitmapFontRenderer.renderFont(font, true, widths), 0, null);
		imageSet = new ImageSet(tex, 16, 16);
		nativeWidth = imageSet.getHeight() / imageSet.getYCount();
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public int getHeight()
	{
		return imageSet.getHeight() / imageSet.getYCount();
	}
	
	public int getWidth(char c)
	{
		return getWidth(c, false);
	}
	
	public int getWidth(char c, boolean monospace)
	{
		return monospace ? nativeWidth : widths[c];
	}
	
	public int getWidth(String text)
	{
		return getWidth(text, 0, false);
	}
	
	public int getWidth(String text, int space, boolean monospace)
	{
		if (text == null) return 0;
		if (monospace)
		{
			return (nativeWidth + space) * text.length();
		}
		
		int lenght = 0;
		for(int i = 0; i < text.length(); i++)
		{
			lenght += widths[text.charAt(i)] + space;
		}
		return lenght;
	}
	
	public Image getCharacterImage(char c)
	{
		return imageSet.getImage(c % 16, c / 16);
	}
}
