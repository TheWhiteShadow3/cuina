package cuina.editor.gui.internal;

import cuina.widget.Menu;
import cuina.widget.Picture;
import cuina.widget.data.ButtonNode;
import cuina.widget.data.EditFieldNode;
import cuina.widget.data.FrameNode;
import cuina.widget.data.MenuNode;
import cuina.widget.data.PictureNode;
import cuina.widget.data.WidgetNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;

public class WidgetLibrary
{
	public static final String WIDGET_TXPE_EXTENTION = "cuina.widget.types";

	private final List<WidgetType> types = new ArrayList<WidgetType>();

	public WidgetLibrary()
	{
		/*
		 * TODO: Typen dynamisch erstellen. Dieser Schritt muss mit der Engine
		 * synchronisiert werden, um dort ebenfalls dynamische Typen erstellen
		 * zu können.
		 */
// getConfigurationElementsFor(WIDGET_TXPE_EXTENTION);
//
// for (IConfigurationElement conf : elements)

		types.add(new WidgetType("Fenster", "frame.png", FrameNode.class, ResizableFrame.class));
		types.add(new WidgetType("Button", "button.png", ButtonNode.class, Button.class));
		types.add(new WidgetType("Textfeld", "textfield.png", EditFieldNode.class, TextArea.class));
		types.add(new WidgetType("Menü", "menu.png", MenuNode.class, Menu.class));
		types.add(new WidgetType("Tabelle", "table.png", FrameNode.class, Table.class));
		types.add(new WidgetType("Bild", "picture.png", PictureNode.class, Picture.class));
	}

	public List<WidgetType> getWidgetTypes()
	{
		return Collections.unmodifiableList(types);
	}

	public static class WidgetType
	{
		private String name;
		private String iconName;
		private Class<? extends WidgetNode> nodeClass;
		private Class<? extends Widget> widgetClass;

		public WidgetType(
				String name,
				String iconName,
				Class<? extends WidgetNode> nodeClass,
				Class<? extends Widget> widgetClass)
		{
			this.name = name;
			this.iconName = iconName;
			this.nodeClass = nodeClass;
			this.widgetClass = widgetClass;
		}

		public String getName()
		{
			return name;
		}

		public Class<? extends WidgetNode> getNodeClass()
		{
			return nodeClass;
		}

		public Class<? extends Widget> getWidgetClass()
		{
			return widgetClass;
		}

		public ImageDescriptor getImageDescriptor()
		{
			if (iconName == null) return null;
			
			return Activator.getImageDescriptor(iconName);
		}
	}
}
