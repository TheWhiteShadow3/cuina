package cuina.message;

import cuina.Context;
import cuina.Game;
import cuina.Input;
import cuina.eventx.EventMethod;
import cuina.eventx.Interpreter.Result;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.plugin.Priority;
import cuina.widget.CuinaWidget;
import cuina.widget.WidgetContainer;
import cuina.widget.WidgetDescriptor;
import cuina.widget.WidgetEventHandler;
import cuina.world.CuinaWorld;

import de.matthiasmann.twl.Widget;

@ForScene(name=MessageBox.MESSAGE_KEY)
@Priority(updatePriority=200)
public class MessageBox implements Plugin, LifeCycle, WidgetDescriptor, WidgetEventHandler
{
	public static final String MESSAGE_KEY = "Message";
	
	private static final long serialVersionUID = 8472522785944518968L;
	
	private MessageHistory history;
	private WidgetContainer container;
//	private CuinaModel backgroundModel;
	private transient MessageWidget widget;
	private String text;
	private String[] choises;
	private int index = -1;
	private boolean active;
	private boolean closing;
	
	public MessageBox() {}
	
	@Override
	public void init()
	{
		this.container = new WidgetContainer(this);
		if (Game.contextExists(Context.SESSION))
			history = Game.getContext(Context.SESSION).get("MessageHistory");
		
		if(history != null) history.setCapacity(100);
	}
	
	public String getText()
	{
		return text;
	}

	@Override
	public void update()
	{
		if (!active) return;
		
		if (index != -1)
		{
			if (Input.isPressed(Input.UP))
			{
				index -= 1;
				if (index < 0) index = choises.length -1;
			}
			
			if (Input.isPressed(Input.DOWN))
			{
				index += 1;
				if (index >= choises.length) index = 0;
			}
		}
//		if (backgroundModel != null) backgroundModel.update();
		
//		if (Input.isPressed(Input.OK) || Input.isPressed(Input.CANCEL))
//		{
//			nextMessage();
//		}
	}

	@Override
	public void postUpdate()
	{
		if (closing) close();
	}

	@Override
	public void dispose()
	{
		container.dispose();
//		if (backgroundModel != null) backgroundModel.dispose();
	}
	
	@EventMethod
	public boolean setName(String name)
	{
		widget.setName(name);
		return true;
	}

	@EventMethod
	public Result setFaceImage(String faceImage)
	{
		widget.setFaceImage(faceImage);
		return Result.DEFAULT;
	}

//	public CuinaModel getBackgroundModel()
//	{
//		return backgroundModel;
//	}
//	
//	/**
//	 * Setzt ein Hintergrundmodel für die MessageBox.
//	 * <p>
//	 * Diese Methode ist Abhängig vom Plugin <b>cuina.animation-1.0</b>
//	 * </p>
//	 * @param imageName der Name der Bilddatei.
//	 */
//	@EventMethod
//	public void setBackgroundModel(String imageName)
//	{
//		setBackgroundModel(new StaticModel(imageName));
//	}
//
//	public void setBackgroundModel(CuinaModel model)
//	{
//		this.backgroundModel = model;
//		model.setPosition(0, Graphics.getHeight(), 0);
//	}

	@EventMethod
	public Result showMessage(String text)
	{
		if(history != null) history.add(text);
		this.text = text;
		
		widget.setText(text);
		setActive(true);
		return Result.WAIT_ONE_FRAME;
	}
	
	@EventMethod
	public Result showChoise(String... choises)
	{
		this.choises = choises;
		// Skip-Anweisung folgt bei Auswahl. TODO: this!
		return Result.WAIT_ONE_FRAME;
	}
	
	private void setActive(boolean value)
	{
		CuinaWorld world = Game.getWorld();
		if (world != null) world.setFreeze(value);
		
		active = value;
		widget.setVisible(value);
		if (value) widget.requestKeyboardFocus();
		closing = false;
	}
	
	private void nextMessage()
	{
		closing = true;
	}
	
	private void close()
	{
		setActive(false);
	}

	@Override
	public Widget createRoot()
	{
		this.widget = new MessageWidget();
		widget.setEventHandler(this);
		widget.setVisible(false);
		return widget;
	}
	
	@Override
	public String getTheme()
	{
		return null;
	}

	public void widgetClosed()
	{
		close();
	}

	@Override
	public Widget getWidget(String key)
	{
		if (widget.getName().equals(key))
			return widget;
		
		return null;
	}

	@Override
	public void handleEvent(String key, CuinaWidget widget, Object type)
	{
		switch((MessageWidget.EventType) type)
		{
			case RETURN:
			case CLICK_OK: nextMessage(); break;
			case CLOSE:
			case ESCAPE:
			case CLICK_CANCEL: close(); break;
		}
	}

	@Override
	public void postBuild()
	{
		
	}

	@Override
	public void setGlobalEventHandler(WidgetEventHandler handler)
	{
		throw new UnsupportedOperationException();
	}
}
