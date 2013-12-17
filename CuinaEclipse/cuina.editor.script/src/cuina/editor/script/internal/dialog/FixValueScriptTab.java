package cuina.editor.script.internal.dialog;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabaseObject;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaProject;
import cuina.editor.script.dialog.CommandTab;
import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.internal.ScriptSelection;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.ast.CommentNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.StrNode;
import cuina.editor.ui.NumberValidator;
import cuina.resource.ResourceException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FixValueScriptTab implements CommandTab, Listener
{
	private static final int TYPE_UNDEFINED = 0;
	private static final int TYPE_BOOLEAN 	= 1;
	private static final int TYPE_STRING 	= 2;
	private static final int TYPE_NUMBER 	= 3;
	private static final int TYPE_DATABASE 	= 4;
	private static final int TYPE_NULL 		= 5;
	
	private ScriptDialogContext context;
	private ValueDefinition parameter;
	private Node node;
	private int type;
	private boolean update;
	
	private Composite contentGroup;
	private Button optNone;
	private Button optBoolean;
	private Button optString;
	private Button optNumber;
	private Button optDatabase;
	private Button optNull;
	
	private Composite numberGroup;
	private Text numberInputField;
	
	private Composite textGroup;
	private Text stringInputField;
	
	private Composite booleanGroup;
	private Button boolCheckBox;
	
	private Composite databaseGroup;
	private Combo tableCombo;
	private DatabaseComboViewer<? extends DatabaseObject> comboViewer;
	private String[] tables;
	
	private Composite noneGroup;
	private Composite nullGroup;
	private Composite buttonGroup;
	private boolean enabled = true;
	
	@Override
	public void setNode(Node node, ValueDefinition parameter)
	{
		System.out.println("[FixValueScriptTab] Node: " + node);
		this.parameter = parameter;
		this.node = node;
		
		update = true;
		if (node instanceof CommentNode) //XXX: Workaround1 um den Tab für Kommentare benutzbar zu machen.
		{
			setValueAsString( ((CommentNode) node).getValue() );
			setInputType(TYPE_STRING);
		}
		else if (node instanceof StrNode)
		{
			setValueAsString( ((StrNode) node).getValue() );
			if (parameter != null && parameter.type.startsWith("key:"))
				setInputType(TYPE_DATABASE);
			else
				setInputType(TYPE_STRING);
		}
		else if (node instanceof FixNumNode)
		{
			setValueAsString( ((FixNumNode) node).getValue().toString() );
			setInputType(TYPE_NUMBER);
		}
		else if (node instanceof ConstNode)
		{
			String constName = ((ConstNode) node).getName();
			setValueAsString(constName);
			if ("nil".equals(constName))
				setInputType(TYPE_NULL);
			else if ("true".equals(constName) || "false".equals(constName))
				setInputType(TYPE_BOOLEAN);
			else
				setInputType(TYPE_UNDEFINED);
		}
		else 
		{
			setValueAsString(null);
			setInputType(TYPE_UNDEFINED);
		}
		update = false;
	}
	
	private void setInputType(int type)
	{
		this.type = type;
		Composite group = null;
		optNone.setSelection(false);
		optBoolean.setSelection(false);
		optString.setSelection(false);
		optNumber.setSelection(false);
		optDatabase.setSelection(false);
		optNull.setSelection(false);
		switch(type)
		{
			case TYPE_UNDEFINED:
				group = noneGroup;
				optNone.setSelection(true);
				break;
				
			case TYPE_BOOLEAN:
				group = booleanGroup;
				optBoolean.setSelection(true);
				break;
				
			case TYPE_STRING:
				group = textGroup;
				optString.setSelection(true);
				break;
				
			case TYPE_NUMBER:
				group = numberGroup;
				optNumber.setSelection(true);
				break;
				
			case TYPE_DATABASE:
				group = databaseGroup;
				optDatabase.setSelection(true);
				break;
				
			case TYPE_NULL:
				group = nullGroup;
				optNull.setSelection(true);
				break;
				
			default: return;
		}
		((StackLayout) contentGroup.getLayout()).topControl = group;
		contentGroup.layout();
		updateNode();
	}

	@Override
	public Node getNode()
	{
		return node;
	}
	
	private DataTable loadDataTable(String tableName)
	{
		if (tableName == null) return null;
		try
		{
			return context.getCuinaProject().getService(Database.class).loadTable(tableName);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void setValueAsString(String text)
	{
		if (text == null)
		{
			boolCheckBox.setSelection(false);
			stringInputField.setText("");
			numberInputField.setSelection(0);
			tableCombo.select(-1);
			comboViewer.setSelection(null);
		}
		else
		{
			boolCheckBox.setSelection(text.equals("true"));
			stringInputField.setText(text);
			try
			{
				Double.parseDouble(text);
				numberInputField.setText(text);
			}
			catch( NumberFormatException e)
			{ numberInputField.setText("0"); }
			
			selectDatabaseObject();
		}
	}
	
	private void selectDatabaseObject()
	{
		if (parameter != null && parameter.type.startsWith("key:"))
		{
			String tableName = parameter.type.substring(4);
			String objKey = ((StrNode) node).getValue();
			
			for (int i = 0; i < tables.length; i++)
			{
				if (tables[i].equals(tableName))
				{
					tableCombo.select(i);
					comboViewer.setSelection(loadDataTable(tableName), objKey);
					break;
				}
			}
		}

	}

	@Override
	public void init(ScriptDialogContext context)
	{
		this.context = context;
	}

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		
		buttonGroup = new Composite(parent, SWT.NONE);
		buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createTypeButtons(buttonGroup);
		
		contentGroup = new Composite(parent, SWT.NONE);
		contentGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentGroup.setLayout(new StackLayout());

		createNoneGroup(contentGroup);
		createBooleanGroup(contentGroup);
		createStringGroup(contentGroup);
		createNumberGroup(contentGroup);
		createDatabaseGroup(contentGroup);
		createNullGroup(contentGroup);
	}

	private void createTypeButtons(Composite parent)
	{
		parent.setLayout(new GridLayout(6, false));
		
		optNone = new Button(parent, SWT.RADIO);
		optNone.setText("Undefiniert");
		optNone.addListener(SWT.Selection, this);
		optNone.setData(TYPE_UNDEFINED);
		
		optBoolean = new Button(parent, SWT.RADIO);
		optBoolean.setText("Bool");
		optBoolean.addListener(SWT.Selection, this);
		optBoolean.setData(TYPE_BOOLEAN);
		
		optString = new Button(parent, SWT.RADIO);
		optString.setText("Text");
		optString.addListener(SWT.Selection, this);
		optString.setData(TYPE_STRING);
		
		optNumber = new Button(parent, SWT.RADIO);
		optNumber.setText("Zahl");
		optNumber.addListener(SWT.Selection, this);
		optNumber.setData(TYPE_NUMBER);
		
		optDatabase = new Button(parent, SWT.RADIO);
		optDatabase.setText("ID");
		optDatabase.addListener(SWT.Selection, this);
		optDatabase.setData(TYPE_DATABASE);
		
		optNull = new Button(parent, SWT.RADIO);
		optNull.setText("Null");
		optNull.addListener(SWT.Selection, this);
		optNull.setData(TYPE_NULL);
	}
	
	private void createNoneGroup(Composite parent)
	{
		noneGroup = new Composite(parent, SWT.NONE);
	}
	
	private void createBooleanGroup(Composite parent)
	{
		booleanGroup = new Composite(parent, SWT.NONE);
		booleanGroup.setLayout(new GridLayout(1, false));
		
		boolCheckBox = new Button(booleanGroup, SWT.CHECK);
		boolCheckBox.setText("TRUE");
		boolCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		boolCheckBox.addListener(SWT.Selection, this);
	}
	
	private void createStringGroup(Composite parent)
	{
		textGroup = new Composite(parent, SWT.NONE);
		textGroup.setLayout(new GridLayout(2, false));
		
		new Label(textGroup, SWT.NONE).setText("Value:");
		stringInputField = new Text(textGroup, SWT.BORDER | SWT.MULTI);
		stringInputField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stringInputField.addListener(SWT.Modify, this);
	}
	
	private void createNumberGroup(Composite parent)
	{
		numberGroup = new Composite(parent, SWT.NONE);
		numberGroup.setLayout(new GridLayout(2, false));
		
		new Label(numberGroup, SWT.NONE).setText("Value:");
		numberInputField = new Text(numberGroup, SWT.BORDER);
		numberInputField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		numberInputField.addVerifyListener(new NumberValidator());
		numberInputField.addListener(SWT.Modify, this);
	}
	
	private void createDatabaseGroup(Composite parent)
	{
		databaseGroup = new Composite(parent, SWT.NONE);
		databaseGroup.setLayout(new GridLayout(2, false));
		
		new Label(databaseGroup, SWT.NONE).setText("Typ:");
		tableCombo = new Combo(databaseGroup, SWT.NONE);
		tableCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		tables = createTableList();
		tableCombo.setItems(tables);
		tableCombo.addListener(SWT.Selection, this);
		
		new Label(databaseGroup, SWT.NONE).setText("Value:");
		comboViewer = new DatabaseComboViewer(databaseGroup, SWT.NONE);
		comboViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		comboViewer.getControl().addListener(SWT.Selection, this);
	}
	
	private void createNullGroup(Composite parent)
	{
		nullGroup = new Composite(parent, SWT.NONE);
	}

	private String[] createTableList()
	{
		IDatabaseDescriptor[] descriptors = DatabasePlugin.getDescriptors();
		String[] names = new String[descriptors.length];
		for (int i = 0; i < descriptors.length; i++)
		{
//			context.getProject().getService(Database.class).loadTable(names[i]);
			names[i] = descriptors[i].getName();
		}
		return names;
//		return new String[] {"Tileset", "pantsu"};
	}

	@Override
	public void handleEvent(Event event)
	{
		if (update) return;
		
		if (event.widget instanceof Button && event.widget.getData() != null)
		{
			if ( ((Button) event.widget).getSelection() )
				setInputType((Integer) event.widget.getData());
			return;
		}
		
		if (event.widget == tableCombo)
		{
			DataTable table = null;
			if (tableCombo.getSelectionIndex() != -1) 
				table = loadDataTable( tableCombo.getItem(tableCombo.getSelectionIndex()) );
			comboViewer.setTable(table);
		}
		
		updateNode();
	}
	
	private void updateNode()
	{
		switch (type)
		{
			case TYPE_UNDEFINED:
			{
				node = new EmptyNode();
				break;
			}
			
			case TYPE_STRING:
			{
				String value = stringInputField.getText().
						replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t");
				if (node instanceof CommentNode) //XXX: Workaround2 um den Tab für Kommentare benutzbar zu machen.
					node = new CommentNode(value);
				else
					node = new StrNode(value);
				break;
			}

			case TYPE_NUMBER:
			{
				try
				{
					Double value = Double.valueOf(numberInputField.getText());
					node = new FixNumNode(value);
				}
				catch (NumberFormatException e)
				{
					node = new FixNumNode(Double.valueOf(0));
				}
				break;
			}
			
			case TYPE_BOOLEAN:
			{
				String value = boolCheckBox.getSelection() ? "true" : "false";
				node = new ConstNode(value);
				break;
			}
			
			case TYPE_DATABASE:
			{
				DatabaseObject obj = comboViewer.getSelectedElement();
				if (obj != null) node = new StrNode(obj.getKey());
				break;
			}
			
			case TYPE_NULL:
			{
				node = new ConstNode("nil");
				break;
			}
			
			default: node = null;
		}
		context.valueChanged(this, node);
	}
	
	@Override
	public void setEnabled(boolean value)
	{
		if (enabled == value) return;
		enabled  = value;
		setInputType(TYPE_UNDEFINED);
		buttonGroup.setEnabled(value);
		contentGroup.setEnabled(value);
	}

	@Override
	public String getName()
	{
		return "Wert";
	}
	
	public static void main(String[] args) throws Exception
	{
		new DatabasePlugin(); // Dummy
		
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setSize(360, 240);
		shell.setLayout(new FillLayout());
		Composite parent = new Composite(shell, SWT.NONE);
		
		FixValueScriptTab tab = new FixValueScriptTab();
		tab.init(new ScriptDialogContext()
		{
			@Override
			public void valueChanged(Object source, Node node)
			{
				System.out.println("Knoten: " + node);
			}
			
			@Override
			public TreeEditor getTreeEditor() { return null; }
			@Override
			public ScriptSelection getSelection() { return null; }
			@Override
			public TreeLibrary getTreeLibrary() { return null; }
			@Override
			public CuinaProject getCuinaProject() { return null; }
			@Override
			public Node getNode() { return null; }
			@Override
			public Shell getShell() { return shell; }
			@Override
			public ScriptPosition getPosition() { return null; }
		});
		tab.createControl(parent);
		tab.setNode(new CommentNode("pantsu"), null);
		tab.setEnabled(false);
		
		shell.open();
		
		while (!shell.isDisposed())
		{
			try
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
//		System.out.println( new NodeLabelProvider().getText(tab.getNode()) );
		System.out.println( "Return-Value: " + tab.getNode() );
		display.dispose();
	}
}
