package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.eventx.internal.CommandEditorContext;
import cuina.editor.ui.WidgetFactory;
import cuina.resource.ResourceException;

public class IDEditor implements TypeEditor<String>
{
	private CommandEditorContext context;
	private String type;
	private String value;
	private DatabaseComboViewer viewer;
	
	public IDEditor(String type)
	{
		this.type = type;
	}
	
	@Override
	public void init(CommandEditorContext context, String type, Object value)
	{
		this.context = context;
		if (value != null)
			this.value = (String) value;
	}

	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		try
		{
			DataTable table = context.getCuinaProject().getService(Database.class).loadTable(type);
			this.viewer =  WidgetFactory.createDatabaseComboViewer(parent, type, table);
			viewer.setSelection(table, value);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public boolean apply()
	{
		this.value = viewer.getSelectedElement().getKey();
		
		return true;
	}

}
