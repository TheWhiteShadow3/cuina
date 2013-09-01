package cuina.message;

import cuina.Game;
import cuina.Logger;
import cuina.util.ResourceManager;
import cuina.util.ResourceManager.Resource;
import cuina.widget.Frame;
import cuina.widget.Picture;
import cuina.widget.WidgetEventHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import jline.internal.InputStreamReader;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.StyleSheet;

public class MessageWidget extends Frame
{
	public static enum EventType { CLOSE, RETURN, ESCAPE, CLICK_OK, CLICK_CANCEL }
	
	
	private static final int FACE_RIGHT = 0;
	private static final int FACE_LEFT = 1;
	private static final int FACE_BACK = 2;
	private static final int FACE_FRONT = 3;
	private static final int FACE_DEFAULT = FACE_RIGHT;
	
	private static final String WIDGET_KEY = "Message";
	
	private MessageBox messageBox;
	private String name;
	private String faceImage;
	private String text;
	
	private boolean hasFace;
	private int faceAlign;
	private int faceX;
	private int faceY;
	private Picture faceWidget;
	private TextArea textWidget;
	private HTMLTextAreaModel textModel;

	private int messageX;
	private int messageY;
	private int messageWidth;
	private int messageHeight;
	private WidgetEventHandler handler;

	private Runnable closeCB;
	
	public MessageWidget(MessageBox mb)
	{
		super(WIDGET_KEY);
		setResizableAxis(ResizableAxis.NONE);
		setEnabled(true);
		setFocusKeyEnabled(true);
		setDraggable(true);
		this.messageBox = mb;
		
		addCloseCallback(new Runnable()
		{
			@Override
			public void run()
			{
				messageBox.widgetClosed();
			}
		});
		
		createTextWidget();
	}
	
	public String getName()
	{
		return name;
	}

	public String getFaceImage()
	{
		return faceImage;
	}

	public String getText()
	{
		return text;
	}

	public void setName(String name)
	{
		this.name = name;
		setTitle(name);
	}
	
	public void setFaceImage(String faceImage)
	{
		this.faceImage = faceImage;
		if (faceWidget != null) faceWidget.setImage(faceImage);
	}
	
	public void setText(String text)
	{
		this.text = text;
		textModel.setHtml(text);
	}

	@Override
	public void setEventHandler(WidgetEventHandler handler)
	{
		this.handler = handler;
		if (this.handler != null && closeCB == null)
		{
			this.closeCB = new Runnable()
			{
				@Override
				public void run()
				{
					fireCallBack(EventType.CLOSE);
				}
			};
			addCloseCallback(closeCB);
		}
	}
	
	private void createTextWidget()
	{
		textModel = new HTMLTextAreaModel();
		textWidget = new TextArea(textModel);
		textWidget.setTheme("text");
		
		try
		{
			StyleSheet styleSheet = new StyleSheet();
			String filename = Game.getIni().get("TWL", "stylesheet.path", "theme.css");
			Resource res = ResourceManager.getResource("cuina.twl.path", filename);
			Reader reader = new InputStreamReader(new FileInputStream(res.getFile()));
			styleSheet.parse(reader);
			reader.close();
			textWidget.setStyleClassResolver(styleSheet);
		}
		catch (IOException e)
		{
			Logger.log(MessageWidget.class, Logger.WARNING, e);
		}
		
		add(textWidget);
	}
	
	protected void applyThemeMessageWidget(ThemeInfo themeInfo)
	{
		hasFace = themeInfo.getParameter("hasFace", false);
		faceX = themeInfo.getParameter("faceX", 0);
		faceY = themeInfo.getParameter("faceY", 0);
		faceAlign = alignToInt(themeInfo.getParameter("faceAlign", "left"));
		messageX = themeInfo.getParameter("x", 0);
		messageY = themeInfo.getParameter("y", 0);
		messageWidth = themeInfo.getParameter("width", 608);
		messageHeight = themeInfo.getParameter("height", 160);
		invalidateLayout();
	}
	
	private int alignToInt(String align)
	{
		switch(align)
		{
			case "left": return FACE_LEFT;
			case "right": return FACE_RIGHT;
			case "back": return FACE_BACK;
			case "front": return FACE_FRONT;
			default: return FACE_DEFAULT;
		}
	}

	@Override
	protected void applyTheme(ThemeInfo themeInfo)
	{
		super.applyTheme(themeInfo);
		applyThemeMessageWidget(themeInfo);
	}
	
	@Override
	protected void layout()
	{
		int minWidth = getMinWidth();
		int minHeight = getMinHeight();
		if (messageWidth < minWidth) messageWidth = minWidth;
		if (messageHeight < minHeight) messageHeight = minHeight;
		setPosition(messageX, messageY);
		setSize(messageWidth, messageHeight);
		
		layoutTitle();
		layoutCloseButton();
		layoutResizeHandle();

		layoutFace();
		layoutText();
	}

	private void layoutFace()
	{
		if (hasFace)
		{
			if (faceWidget == null)
			{
				faceWidget = new Picture(null);
				faceWidget.setTheme("face");
				insertChild(faceWidget, (faceAlign == FACE_BACK) ? 0 : 1);
			}
			faceWidget.setPosition(getInnerX() + faceX, getInnerY() + faceY);
			faceWidget.setImage(faceImage);
			faceWidget.calculateSize();
		}
	}
	
	private void layoutText()
	{
		int left = getInnerX() + 4;
		int right = getInnerRight() - 4;
		if (faceWidget != null && hasFace)
		{
			if (faceAlign == FACE_LEFT)
			{
				left = faceWidget.getRight() + 8;
			}
			else if (faceAlign == FACE_RIGHT)
			{
				right = faceWidget.getX() - 8;
			}
		}

		int top = getInnerY() + 6;
		textWidget.setPosition(left, top);
		textWidget.setSize(Math.max(right-left, 0), Math.max(getInnerBottom() - top, 0));
	}
	
	@Override
	public boolean handleEvent(Event ev)
	{
//		if (super.handleEvent(evt)) { return true; }
		switch (ev.getType())
		{
			case KEY_PRESSED:
				switch (ev.getKeyCode())
				{
					case Event.KEY_RETURN:
						fireCallBack(EventType.RETURN);
						return true;
						
					case Event.KEY_ESCAPE:
						fireCallBack(EventType.ESCAPE);
						return true;
				}
				break;
			case MOUSE_CLICKED:
				switch (ev.getMouseButton())
				{
					case Event.MOUSE_LBUTTON:
						fireCallBack(EventType.CLICK_OK);
						return true;
					case Event.MOUSE_RBUTTON:
						fireCallBack(EventType.CLICK_CANCEL);
						return true;
				}
				break;
		}
		return false;
	}
	
	private void fireCallBack(EventType type)
	{
		if (handler == null) return;
		
		handler.handleEvent(getKey(), this, type);
	}

//	@Override
//	protected void paintChildren(GUI gui)
//	{
//		for (int i = 0, n = getNumChildren(); i < n; i++)
//		{
//			Widget child = getChild(i);
//			if (child.isVisible())
//			{
//				child.paint(gui);
//			}
//		}
//	}
}
