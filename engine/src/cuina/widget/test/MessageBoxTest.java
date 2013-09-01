package cuina.widget.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.matthiasmann.twl.Rect;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.TextArea.Callback;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.ValueAdjusterInt;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleIntegerModel;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.StyleSheet;
import de.matthiasmann.twl.textarea.TextAreaModel;
import de.matthiasmann.twl.utils.TextUtil;

public class MessageBoxTest extends Widget
{
	private final TextFrame textFrame;
	public boolean quit;

	public MessageBoxTest()
	{
		textFrame = new TextFrame();
		add(textFrame);

		textFrame.setSize(620, 192);
		textFrame.setPosition(10, 276);
		setVisible(true);
	}

	static class TextFrame extends ResizableFrame
	{
		private final HTMLTextAreaModel textAreaModel;
		private final TextArea textArea;
		private final ScrollPane scrollPane;
		private Timer timer;
		private int size;
		private int dir;

		private static final int MIN_SIZE = 128;
		private static final int MAX_SIZE = 256;

		public TextFrame()
		{
			setTitle("Text");
			setResizableAxis(ResizableAxis.NONE);
			setDraggable(false);

			this.textAreaModel = new HTMLTextAreaModel();
			this.textArea = new TextArea(textAreaModel);

			readFile("demo.html");

			textArea.addCallback(new Callback()
			{
				@Override
				public void handleLinkClicked(String href)
				{
					if (href.startsWith("javascript:"))
					{
//						handleAction(href.substring(11));
					}
					else if (href.startsWith("#"))
					{
						TextAreaModel.Element ankor = textAreaModel.getElementById(href.substring(1));
						if (ankor != null)
						{
							Rect rect = textArea.getElementRect(ankor);
							if (rect != null)
							{
								scrollPane.setScrollPositionY(rect.getY());
							}
						}
					}
					else
					{
						readFile(href);
					}
				}
			});

			ValueAdjusterInt vai = new ValueAdjusterInt(new SimpleIntegerModel(0, 100, 50));
			vai.setTooltipContent("Select a nice value");
			textArea.registerWidget("niceValueSlider", vai);

			scrollPane = new ScrollPane(textArea);
			scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);

			add(scrollPane);
		}

//		@Override
//		protected void afterAddToGUI(GUI gui)
//		{
//			super.afterAddToGUI(gui);
//			timer = gui.createTimer();
//			timer.setDelay(16);
//			timer.setContinuous(true);
//			timer.setCallback(new Runnable()
//			{
//				public void run()
//				{
//					animate();
//				}
//			});
//		}

//		@Override
//		protected void beforeRemoveFromGUI(GUI gui)
//		{
//			super.beforeRemoveFromGUI(gui);
//			timer.stop();
//			timer = null;
//		}

		void readFile(String name)
		{
			try
			{
//				textAreaModel.setHtml("<html><p>Text</p></html>");
				textAreaModel.readHTMLFromURL(TextAreaDemo.class.getResource(name));

				StyleSheet styleSheet = new StyleSheet();
				for (String styleSheetLink : textAreaModel.getStyleSheetLinks())
				{
					try
					{
						styleSheet.parse(TextAreaDemo.class.getResource(styleSheetLink));
					}
					catch (IOException ex)
					{
						Logger.getLogger(TextAreaDemo.class.getName()).log(Level.SEVERE,
								"Can't parse style sheet: " + styleSheetLink, ex);
					}
				}
				textArea.setStyleClassResolver(styleSheet);

				setTitle(TextUtil.notNull(textAreaModel.getTitle()));

				size = MIN_SIZE;
				dir = -4;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
//				Logger.getLogger(TextAreaDemo.class.getName()).log(Level.SEVERE, "Can't read HTML: " + name, ex);
			}
		}

//		void handleAction(String what)
//		{
//			if ("zoomImage()".equals(what))
//			{
//				if (timer != null && !timer.isRunning())
//				{
//					dir = -dir;
//					timer.start();
//				}
//			}
//		}
//
//		void animate()
//		{
//			size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, size + dir));
//			if (size == MIN_SIZE || size == MAX_SIZE)
//			{
//				timer.stop();
//			}
//
//			TextAreaModel.Element e = textAreaModel.getElementById("portrait");
//			if (e != null)
//			{
//				e.setStyle(e.getStyle().with(StyleAttribute.WIDTH, new Value(size, Value.Unit.PX)));
//				textAreaModel.domModified();
//			}
//		}
	}
}
