package cuina.widget;

import java.io.Serializable;

import javax.swing.event.ChangeListener;

public interface ButtonModel extends Serializable
{
	public static final int ENABLED		= 1;
	public static final int HOVER		= 2;
	public static final int SELECTED	= 4;
	public static final int ARMED		= 8;
	public static final int PRESSED		= 16;
	
	public boolean isEnabled();

	public boolean isHover();
	
	public boolean isSelected();

	public boolean isArmed();

	public boolean isPressed();

	public void setEnabled(boolean enable);
	
	public void setHover(boolean hover);
	
	public void setSelected(boolean selected);
	
	public void setArmed(boolean armed);
	
	public void setPressed(boolean pressed);
	
	public void addListener(Listener l);

	public void removeListener(ChangeListener l);

	public void setGroup(ButtonGroup group);

	public ButtonGroup getGroup();
}
