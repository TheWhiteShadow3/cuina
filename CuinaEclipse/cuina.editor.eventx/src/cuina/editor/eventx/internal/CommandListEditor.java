package cuina.editor.eventx.internal;

import cuina.database.ui.DataEditorPage;
import cuina.database.ui.IDatabaseEditor;
import cuina.eventx.Command;
import cuina.eventx.CommandList;

import org.eclipse.swt.widgets.Composite;

public class CommandListEditor implements DataEditorPage<CommandList>
{
	private CommandList list;
	
	@Override
	public void setValue(CommandList list)
	{
		this.list = list;
	}

	@Override
	public void setChildValue(Object obj)
	{
		if (obj instanceof Command)
		{
			
		}
	}

	@Override
	public CommandList getValue()
	{
		return list;
	}

	@Override
	public void createEditorPage(Composite parent, IDatabaseEditor handler)
	{
		// TODO Auto-generated method stub
		
	}
}
