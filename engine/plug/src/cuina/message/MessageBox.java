package cuina.message;

import cuina.Context;
import cuina.Game;
import cuina.event.AbstractTrigger;
import cuina.event.Trigger;
import cuina.eventx.EventMethod;
import cuina.eventx.Interpreter;
import cuina.eventx.Interpreter.Result;
import cuina.input.Input;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.plugin.Priority;
import cuina.widget.Button;
import cuina.widget.CuinaWidget;
import cuina.widget.WidgetContainer;
import cuina.widget.WidgetDescriptor;
import cuina.world.CuinaWorld;

@ForScene(name=MessageBox.MESSAGE_KEY)
@Priority(updatePriority=200)
public class MessageBox implements Plugin, LifeCycle, WidgetDescriptor
{
	public static final String MESSAGE_KEY = "Message";
	
	public static final String CONTROL_UP = "up";
	public static final String CONTROL_DOWN = "down";
	public static final String CONTROL_OK = "c2";
	public static final String CONTROL_CANCEL = "c3";
	
	private static final long serialVersionUID = 8472522785944518968L;
	
	private MessageHistory history;
	private WidgetContainer container;
//	private CuinaModel backgroundModel;
	private transient MessageWidget widget;
	private String text;
	private String[] choises;
	private Interpreter interpreter;
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
		if (closing) close();
		if (!active) return;
		
		if (index != -1)
		{
			if (Input.isPressed(CONTROL_UP))
			{
				index -= 1;
				if (index < 0) index = choises.length -1;
			}
			
			if (Input.isPressed(CONTROL_DOWN))
			{
				index += 1;
				if (index >= choises.length) index = 0;
			}
		}
		else if (Input.isPressed(CONTROL_OK) || Input.isPressed(CONTROL_CANCEL))
		{
			nextMessage();
		}
	}
	
	@Override
	public void postUpdate() {}

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
		if (history != null) history.add(text);
		this.text = text;
		
		widget.setText(text);
		setActive(true);
		return Result.WAIT_ONE_FRAME;
	}
	
	@EventMethod
	public Result showChoise(String... choises)
	{
		this.choises = choises;
		this.interpreter = Interpreter.getContextInterpreter();
			
		widget.setChoises(choises);
		setActive(true);
		return Result.WAIT_ONE_FRAME;
	}
	
	private void selectionCallback(int index)
	{
		if (choises == null) return;
		
		if (history != null)
		{
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < choises.length; i++)
			{
				if (i == index)
					builder.append("<p class='msg_elected'>").append(choises[i]).append("</p>");
				else
					builder.append("<p>").append(choises[i]).append("</p>");
			}
			history.add(builder.toString());
		}
		System.out.println("[MessageBox] Auswahl: " + index);
		interpreter.setSwitchValue(index);
		
		index = -1;
		choises = null;
		nextMessage();
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
	
	public boolean isActive()
	{
		return active;
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
	public CuinaWidget createRoot()
	{
		this.widget = new MessageWidget();
		widget.menu.addTrigger(getMenuTrigger());
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
	public void postBuild() {}
	
	@SuppressWarnings("serial")
	private Trigger getMenuTrigger()
	{
		return new AbstractTrigger(Button.BUTTON_PRESSED)
		{
			@Override
			public void run(Object... args)
			{
				selectionCallback((Integer) args[1]);
			}
		};
	}
}
