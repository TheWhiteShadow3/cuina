package cuina.widget.model;

import cuina.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class TextAreaModel
{
//	private Parser parser;
	private Element root = new Element();
	
	
	
	public Element parse()
	{
		Element text = new TextElement("blub");
		
		root.children.add(text);
		
		return root;
	}
	
	public Element getRoot()
	{
		return root;
	}
	
	public static enum Position
	{
		ABSOLUTE,
		RELATIVE,
		
	}
	
	public class Element extends Rectangle
	{
		public int position;
		public Rectangle margin;
		public Rectangle padding;
		public final List<Element> children = new ArrayList<Element>(8);
	}
	
	public class TextElement extends Element
	{
		public String fontName;
		public String text;
		
		public TextElement(String text)
		{
			this(null, text);
		}

		public TextElement(String fontName, String text)
		{
			this.fontName = fontName;
			this.text = text;
		}
	}
	
	public class ImageElement extends Element
	{
		public String imageName;
	}
}
