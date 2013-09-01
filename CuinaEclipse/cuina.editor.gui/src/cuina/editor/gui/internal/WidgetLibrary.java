package cuina.editor.gui.internal;

import cuina.widget.Picture;

import java.util.HashMap;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.TextArea;

public class WidgetLibrary
{	
	private HashMap<Class<?>, String> widgetDefinitions = new HashMap<Class<?>, String>();
	
	public WidgetLibrary()
	{
		widgetDefinitions.put(Button.class, "Button");
		widgetDefinitions.put(HtmlArea.class, "HTML area");
		widgetDefinitions.put(Menu.class, "Menu");
		widgetDefinitions.put(Picture.class, "Picture");
		widgetDefinitions.put(RadioCheckBox.class, "Radio button/Check box");
		widgetDefinitions.put(Table.class, "Table");
		widgetDefinitions.put(TextArea.class, "Text area");
		widgetDefinitions.put(TextLine.class, "Text line");
	}
	
	public String getDescription(Class<?> clazz)
	{
		return widgetDefinitions.get(clazz);
	}
	
	public Object[] getWidgets()
	{
		return widgetDefinitions.keySet().toArray();
	}
}
