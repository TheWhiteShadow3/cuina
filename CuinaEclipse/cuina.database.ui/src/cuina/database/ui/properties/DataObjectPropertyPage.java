package cuina.database.ui.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.database.ui.tree.TreeNode;
import cuina.resource.ResourceException;

public class DataObjectPropertyPage extends PropertyPage
{
	private final int TEXT_WIDTH = 40;

	private Text fileText;
	private Text tableNameText;
	private Text tableClassText;
	private Text keyText;
	private Text nameText;

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		addTableInfo(composite);
		addSeparator(composite);
		addItemInfo(composite);
		setValues();

		return composite;
	}

	private void addSeparator(Composite parent)
	{
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	private void addTableInfo(Composite parent)
	{
		Composite group = createGroup(parent, 2);

		new Label(group, SWT.NONE).setText("Datei:");
		fileText = new Text(group, SWT.READ_ONLY);

		new Label(group, SWT.NONE).setText("Tabelle:");
		tableNameText = new Text(group, SWT.READ_ONLY);

		new Label(group, SWT.NONE).setText("Klasse:");
		tableClassText = new Text(group, SWT.READ_ONLY);
	}

	private void addItemInfo(Composite parent)
	{
		Composite group = createGroup(parent, 2);
		int width = convertWidthInCharsToPixels(TEXT_WIDTH);

		new Label(group, SWT.NONE).setText("Schlüssel:");
		keyText = new Text(group, SWT.BORDER);
		keyText.setLayoutData(new GridData(width, -1));

		new Label(group, SWT.NONE).setText("Name:");
		nameText = new Text(group, SWT.BORDER);
		nameText.setLayoutData(new GridData(width, -1));
	}

	private Composite createGroup(Composite parent, int coloumns)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(coloumns, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		return group;
	}

	private void setValues()
	{
		TreeNode node = (TreeNode) getElement();
		DataTable table = node.getTable();

		fileText.setText(table.getFileName());
		tableNameText.setText(table.getName());
		tableClassText.setText(table.getElementClass().getName());

		String name = node.getName();
		nameText.setText(name != null ? name : "");

		if(node instanceof TreeDataNode)
		{
			keyText.setText(((TreeDataNode) node).getKey());
		} else if(node instanceof TreeGroup)
		{
			keyText.setEditable(false);
			keyText.setText("#Group");
		}
	}

	@Override
	public boolean performOk()
	{
		TreeNode node = (TreeNode) getElement();
		if(node instanceof TreeDataNode)
		{
			TreeDataNode leaf = (TreeDataNode) node;

			leaf.changeKey(keyText.getText());
			leaf.getData().setName(nameText.getText());
		} else if(node instanceof TreeGroup)
		{
			((TreeGroup) node).setName(nameText.getText());
		}
		DataTable table = node.getTable();
		try
		{
			Database db = table.getDatabase();
			db.saveTable(table);
			db.saveMetaData();
			return true;
		} catch(ResourceException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}