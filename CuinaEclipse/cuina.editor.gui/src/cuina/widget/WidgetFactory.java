package cuina.widget;

import cuina.editor.gui.internal.ImageProvider;
import cuina.resource.ResourceException;
import cuina.widget.data.ButtonNode;
import cuina.widget.data.FrameNode;
import cuina.widget.data.MenuNode;
import cuina.widget.data.PictureNode;
import cuina.widget.data.TextAreaNode;
import cuina.widget.data.WidgetNode;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.StyleSheet;

public class WidgetFactory
{
	private static StyleSheet defaultStyleSheet;
	private ImageProvider imageProvider;
	
	public WidgetFactory(ImageProvider imageProvider)
	{
		this.imageProvider = imageProvider;
	}
	
	public Widget createWidget(WidgetNode data)
	{
		Widget widget = null;
		
		if (data instanceof FrameNode)			widget = createResizableFrame((FrameNode) data);
		else if (data instanceof ButtonNode)	widget = createButton((ButtonNode) data);
		else if (data instanceof PictureNode)	widget = createPicture((PictureNode) data);
		else if (data instanceof TextAreaNode)	widget = createTextArea((TextAreaNode) data);
		else if (data instanceof MenuNode) 		widget = createMenu((MenuNode) data);
		else
			throw new IllegalArgumentException("can not handle node " + data.getClass());

		return widget;
	}
	
	private Widget createMenu(MenuNode data)
	{
		return null;
	}

	// Einstellungen, die für alle Widget-Typen gültig sind.
	private void applyGeneralSettings(Widget widget, WidgetNode data)
	{
		widget.setPosition(data.x, data.y);
		widget.setSize(data.width, data.height);
		widget.setEnabled(data.enabled);
		widget.setVisible(data.visible);
	}

	private Widget createResizableFrame(FrameNode data)
	{
		final ResizableFrame frame = new FactoryFrame();
		frame.setTheme("/resizableframe");
		apply(frame, data);
		for (WidgetNode child : data.children)
		{
			frame.add(createWidget(child));
		}
		
		return frame;
	}

	private Widget createButton(ButtonNode data)
	{
		final Button button = new Button();
		button.setTheme("/button");
		apply(button, data);

		return button;
	}

	private Widget createPicture(PictureNode data)
	{
		Picture picture = new Picture();
		picture.setTheme("/picture");
		apply(picture, data);

		return picture;
	}
	
	private Widget createTextArea(TextAreaNode data)
	{
		TextArea textArea = new TextArea();
		textArea.setTheme("/textarea");
		apply(textArea, data);

		for (WidgetNode child : data.children)
		{
			textArea.registerWidget(data.getName(), createWidget(child));
		}

		return textArea;
	}
	
	private void apply(ResizableFrame frame, FrameNode data)
	{
		applyGeneralSettings(frame, data);
		frame.setTitle(data.title);
	}
	
	private void apply(Button button, ButtonNode data)
	{
		applyGeneralSettings(button, data);
		button.setText(data.text);
	}
	
	private void apply(Picture picture, PictureNode data)
	{
		applyGeneralSettings(picture, data);
		try
		{
			picture.setImage(imageProvider.createImage(data.imageName));
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}
	
	private void apply(TextArea textArea, TextAreaNode data)
	{
		applyGeneralSettings(textArea, data);
		textArea.setModel(new HTMLTextAreaModel(data.text));
		if (defaultStyleSheet == null)
		{
//			defaultStyleSheet = new StyleSheet();
//			try
//			{
//				project.g
//				String resName = Game.getIni().get("TWL", "stylesheet.path");
//				File cssFile = ResourceManager.getResource(WidgetContainer.TWL_RESOURE_PATH, resName).getFile();
//				defaultStyleSheet.parse(new InputStreamReader(new FileInputStream(cssFile)));
//			}
//			catch (NullPointerException | IOException e)
//			{
//				Logger.log(WidgetFactory.class, Logger.ERROR, e);
//			}
		}
		textArea.setStyleClassResolver(defaultStyleSheet);
	}
	
	public void reapply(Widget widget, WidgetNode data)
	{
		if (data instanceof FrameNode)
		{
			apply((ResizableFrame) widget, (FrameNode) data);
		}
		else if (data instanceof ButtonNode)
		{
			apply((Button) widget, (ButtonNode) data);
		}
		else if (data instanceof PictureNode)
		{
			apply((Picture) widget, (PictureNode) data);
		}
		else if (data instanceof TextAreaNode)
		{
			apply((TextArea) widget, (TextAreaNode) data);
		}
	}
	
	public static class FactoryFrame extends ResizableFrame
	{
		private FactoryFrame()
		{
			super();
		}

		@Override
		protected void layout()
		{
			int minWidth = getMinWidth();
			int minHeight = getMinHeight();
			if (getWidth() < minWidth || getHeight() < minHeight)
			{
				int width = Math.max(getWidth(), minWidth);
				int height = Math.max(getHeight(), minHeight);
				if (getParent() != null)
				{
					int x = Math.min(getX(), getParent().getInnerRight() - width);
					int y = Math.min(getY(), getParent().getInnerBottom() - height);
					setPosition(x, y);
				}
				setSize(width, height);
			}

	        layoutTitle();
	        layoutCloseButton();
	        layoutResizeHandle();
		}

		@Override
		public boolean isFrameElement(Widget widget)
		{
			return super.isFrameElement(widget);
		}
	};
}
