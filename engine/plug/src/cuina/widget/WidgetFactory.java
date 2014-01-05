package cuina.widget;

import cuina.Game;
import cuina.Logger;
import cuina.util.ResourceManager;
import cuina.widget.data.ButtonNode;
import cuina.widget.data.FrameNode;
import cuina.widget.data.LabelNode;
import cuina.widget.data.MenuNode;
import cuina.widget.data.PictureNode;
import cuina.widget.data.TextAreaNode;
import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.StyleSheet;

public class WidgetFactory
{
	private static StyleSheet defaultStyleSheet;
	
	/** Eine Map mit den zuletzt erstellten Widgets. */
	static Map<String, Widget> buildMap;
	
	private WidgetFactory() {}
	
	public static WidgetDescriptor createWidgetDescriptor(WidgetTree tree)
	{
		return createWidgetDescriptor(tree.root, tree.active);
	}
	
	public static WidgetDescriptor createWidgetDescriptor(WidgetNode root, String active)
	{
		return new DataWidgetDescriptor(root, active);
	}
	
	static Widget createWidget(WidgetNode data)
	{
		if (data == null) throw new NullPointerException("data is null.");
		
		buildMap = new HashMap<String, Widget>();
		return internalCreateWidget(data);
	}
	
	private static Widget internalCreateWidget(WidgetNode data)
	{
		Widget widget = null;
		
		if (data instanceof FrameNode)			widget = createResizableFrame((FrameNode) data);
		else if (data instanceof ButtonNode)	widget = createButton((ButtonNode) data);
		else if (data instanceof PictureNode)	widget = createPicture((PictureNode) data);
		else if (data instanceof TextAreaNode)	widget = createTextArea((TextAreaNode) data);
		else if (data instanceof MenuNode)		widget = createMenu((MenuNode) data);
		else if (data instanceof LabelNode)		widget = createLabel((LabelNode) data);
		else
			throw new IllegalArgumentException("can not handle node " + data.getClass());
		
		assert widget instanceof CuinaWidget;
		return widget;
	}

	// Einstellungen, die für alle Widget-Typen gültig sind.
	private static void applyGeneralSettings(Widget widget, WidgetNode data)
	{
		if (data.name != null)
			buildMap.put(data.name, widget);
		widget.setPosition(data.x, data.y);
		widget.setSize(data.width, data.height);
		widget.setEnabled(data.enabled);
		widget.setVisible(data.visible);
	}

	private static Widget createResizableFrame(FrameNode data)
	{
		Frame frame = new Frame(data.name);
		frame.setTheme("/frame");
		applyGeneralSettings(frame, data);
		frame.setTitle(nonNullString(data.title));
		if (data.resizable)
			frame.setResizableAxis(Frame.ResizableAxis.BOTH);
		else
			frame.setResizableAxis(Frame.ResizableAxis.NONE);
		
		frame.setDraggable(data.draggable);
		for (cuina.widget.data.WidgetNode child : data.children)
		{
			frame.add(internalCreateWidget(child));
		}
		
		return frame;
	}

	private static Widget createButton(ButtonNode data)
	{
		Button button = new Button(data.name, data.toggleButton);
		button.setTheme("/button");
		applyGeneralSettings(button, data);
		button.setText(data.text);
		
		for (cuina.widget.data.WidgetNode child : data.children)
		{
			button.add(internalCreateWidget(child));
		}
		
		return button;
	}

	private static Widget createPicture(PictureNode data)
	{
		Picture picture = new Picture(data.name);
		picture.setTheme("/picture");
		applyGeneralSettings(picture, data);
		picture.setImage(data.imageName);
		
		return picture;
	}
	
	private static Widget createMenu(MenuNode data)
	{
		int columns = data.columns;
		if (columns < 1) columns = data.commands.length;
		Menu menu = new Menu(data.name, columns);
		menu.setTheme("/menu");
		applyGeneralSettings(menu, data);
		menu.setCommands(data.commands);
		menu.setGaps(data.hGap, data.vGap);
		
		return menu;
	}

	private static Widget createLabel(LabelNode data)
	{
		Label label = new Label(data.name);
		label.setTheme("/label");
		applyGeneralSettings(label, data);
		label.setText(data.text);
		
		return label;
	}

	private static Widget createTextArea(final TextAreaNode data)
	{
		TextArea textArea = new TextArea();
		textArea.setTheme("/textarea");
		applyGeneralSettings(textArea, data);
		textArea.setModel(new HTMLTextAreaModel(data.text));
		if (defaultStyleSheet == null)
		{
			defaultStyleSheet = new StyleSheet();
			try
			{
				String resName = Game.getIni().get("TWL", "stylesheet.path");
				File cssFile = ResourceManager.getResource(WidgetContainer.TWL_RESOURE_PATH, resName).getFile();
				defaultStyleSheet.parse(new InputStreamReader(new FileInputStream(cssFile)));
			}
			catch (NullPointerException | IOException e)
			{
				Logger.log(WidgetFactory.class, Logger.ERROR, e);
			}
		}
		textArea.setStyleClassResolver(defaultStyleSheet);
		for (cuina.widget.data.WidgetNode child : data.children)
		{
			textArea.registerWidget(data.name, internalCreateWidget(child));
		}

		return textArea;
	}
	
	private static String nonNullString(String str)
	{
		return str == null ? "" : str;
	}

//	static class WidgetCallBackAdapter implements TextArea.Callback, Runnable
//	{
//		private WidgetNode data;
//		private Widget widget;
//		private WidgetEventHandler handler;
//
//		public WidgetCallBackAdapter(WidgetNode data, Widget widget, WidgetEventHandler handler)
//		{
//			this.data = data;
//			this.widget = widget;
//			this.handler = handler;
//		}
//
//		@Override
//		public void handleLinkClicked(String href)
//		{
//			handler.handleEvent(data.key, widget, href);
//		}
//
//		@Override
//		public void run()
//		{
//			handler.handleEvent(data.key, widget, null);
//		}
//	}
}
