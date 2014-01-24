package cuina.widget;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.renderer.Image;

public class TextArea extends CuinaWidget
{
//	private TextAreaModel model;
	private GElement graphicRoot;

	@Override
	protected void layout()
	{
		if (getInnerWidth() != graphicRoot.width)
		{
//			Element root = model.getRoot();
		}
	}

	abstract class GElement
	{
		int x;
		int y;
        int width;
        int height;
        
		void draw() {}
		void dispose() {}
	}
	
	class GText extends GElement
	{
		Font font;
		Color color;
		String text;
        int start;
        int end;
		FontCache cache;
		
		public GText(Font font, Color color, String text, int start, int end)
		{
            this.font = font;
            this.text = text;
            this.start = start;
            this.end = end;
			cache = font.cacheText(null, text, start, end);
		}
		
		@Override
		public void draw()
		{
			cache.draw(null, x, y);
		}
	}
	
	class GImage extends GElement
	{
		Image image;
		
		@Override
		public void draw()
		{
			image.draw(null, x, y, width, height);
		}
	}
}
