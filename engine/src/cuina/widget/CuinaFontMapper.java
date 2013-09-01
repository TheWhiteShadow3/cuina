package cuina.widget;

import cuina.Logger;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;
import cuina.util.ResourceManager.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontMapper;
import de.matthiasmann.twl.renderer.FontParameter;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.utils.StateSelect;
import de.matthiasmann.twl.utils.StringList;

public class CuinaFontMapper implements FontMapper
{	
	private LWJGLRenderer renderer;
	private String defaultFontName;
	private final HashMap<FontData, Font> cache = new HashMap<FontData, Font>();
	
	public CuinaFontMapper(LWJGLRenderer renderer)
	{
		this.renderer = renderer;
	}

	@Override
	public void destroy()
	{
		renderer = null;
	}

	@Override
	public Font getFont(StringList families, int size, int style, StateSelect select, FontParameter... params)
	{
		return findFont(families.getValue(), style, select, params);
	}
	
	private Font findFont(String fontName, int style, StateSelect select, FontParameter... params)
	{
		FontData fd = new FontData(fontName, style);
		Font font = cache.get(fd);
		if (font == null) try
		{
			if ("default".equals(fontName) && defaultFontName != null) return findFont(defaultFontName, style, select, params);
			
			font = renderer.loadFont(findResource(fontName, style).getURL(), select, params);
			if (font != null) cache.put(fd, font);
			// Nimm die erste Font als Default
			if (defaultFontName == null) defaultFontName = fontName;
		}
		catch (IOException e)
		{
			Logger.log(CuinaFontMapper.class, Logger.WARNING, e);
		}
		return font;
	}

	@Override
	public boolean registerFont(String fontFamily, int style, URL url)
	{
		return false;
	}

	@Override
	public boolean registerFont(String fontFamily, URL url) throws IOException
	{
		return registerFont(fontFamily, STYLE_NORMAL, url);
	}

	private Resource findResource(final String fontFamily, int style) throws LoadingException
	{
		String resName = fontFamily.toLowerCase();
		switch(style)
		{
			case STYLE_ITALIC: resName += 'i'; break;
			case STYLE_BOLD: resName += 'b'; break;
			case STYLE_ITALIC + STYLE_BOLD: resName += 'z'; break;
		}
		resName += ".fnt";
		if (ResourceManager.resourceExists("cuina.twl.path", resName))
			return ResourceManager.getResource("cuina.twl.path", resName);
		else if (style != STYLE_NORMAL)
			return findResource(fontFamily, STYLE_NORMAL);
		else
			return null;
	}
	
	private static class FontData
	{
		public final String family;
		public final int style;
		
		public FontData(String family, int style)
		{
			if (family == null) throw new NullPointerException();
			this.family = family;
			this.style = style;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + family.hashCode();
			result = prime * result + style;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			FontData other = (FontData) obj;
			if (!family.equals(other.family)) return false;
			if (style != other.style) return false;
			return true;
		}
	}
}
