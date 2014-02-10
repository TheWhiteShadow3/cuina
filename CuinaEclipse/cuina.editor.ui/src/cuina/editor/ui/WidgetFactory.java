package cuina.editor.ui;
 
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceManager;
 
public class WidgetFactory
{
	public static int DEFAULT_WIDTH = 144;
	
	private WidgetFactory() {}

	public static Button createButton(Composite parent, String title, int type)
	{
		Button button = new Button(parent, type);
		button.setText(title);
		GridData gd = new GridData(DEFAULT_WIDTH, 24);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		return button;
	}
	
	public static Text createText(Composite parent, String title)
	{
		if (title != null) new Label(parent, SWT.NONE).setText(title);
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(DEFAULT_WIDTH, 14));
		return text;
	}

	public static Spinner createSpinner(Composite parent, String title)
	{
		if (title != null) new Label(parent, SWT.NONE).setText(title);
		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(48, 14));
		return spinner;
	}

	public static Combo createCombo(Composite parent, String title, String[] items)
	{
		if (title != null) new Label(parent, SWT.NONE).setText(title);
		Combo combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(DEFAULT_WIDTH, 24));
		combo.setItems(items);
		combo.setText(items[0]);
		return combo;
	}

	public static <E extends DatabaseObject> DatabaseComboViewer<E> createDatabaseComboViewer(
			Composite parent, String title, final DataTable<E> table)
	{
		if (title != null) new Label(parent, SWT.NONE).setText(title);
		DatabaseComboViewer<E> combo = new DatabaseComboViewer<E>(parent, SWT.FILL);
		combo.getControl().setLayoutData(new GridData(DEFAULT_WIDTH, 24));
		combo.setTable(table);
		return combo;
	}

	public static ResourceButton createImageButton(Composite parent,
			CuinaProject project, String title, String initialValue)
	{
		if (title != null) new Label(parent, SWT.NONE).setText(title);
		ResourceButton button = new ResourceButton(parent, project, ResourceManager.KEY_GRAPHICS);
		button.setResourceName(initialValue);
		button.setLayoutData(new GridData(DEFAULT_WIDTH + 12, 24));
		return button;
	}

	public static <E> DefaultComboViewer<E> createComboViewer(Composite parent, String title, List<E> items, boolean withNull)
	{
		new Label(parent, SWT.NONE).setText(title);
		DefaultComboViewer<E> combo = new DefaultComboViewer<E>(parent, SWT.FILL);
		combo.getControl().setLayoutData(new GridData(DEFAULT_WIDTH, 24));
		combo.setList(items, withNull);
		return combo;
	}
}
