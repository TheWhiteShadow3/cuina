package cuina.widget.model;

import cuina.widget.Button;

public class ButtonGroup
{
	private ButtonModel selection;
	
	public void add(Button button)
	{
		if (button == null) throw new NullPointerException();
		
		if (button.isSelected())
		{
			if (selection == null)
				selection = button.getModel();
			else
				button.setSelected(false);
		}
		button.getModel().setGroup(this);
	}
	
	public void remove(Button button)
	{
		if (button.getModel() == selection)
		{
			selection = null;
		}
		button.getModel().setGroup(null);
	}
	
	public void select(ButtonModel model)
	{
		if (selection != null)
		{
			selection.setSelected(false);
		}
		selection = model;
		selection.setSelected(true);
	}
	
    public boolean isSelected(ButtonModel m)
    {
        return (m == selection);
    }
}
