package cuina.widget;

public class ToggleButtonModel extends DefaultButtonModel
{
	private static final long serialVersionUID = 7341383543893015178L;

	@Override
	public boolean isSelected()
	{
		return (state & SELECTED) != 0;
	}

	@Override
	public void setSelected(boolean selected)
	{
		if (selected == isSelected()) return;
		
		setStateBit(ENABLED, selected);
		fireStateChanged(ENABLED);
		super.setSelected(selected);
	}

	@Override
	protected void buttonPressed()
	{
		setSelected(!isSelected());
	}
}
