package cuina.editor.script.internal.prefs;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class ColorListEditor extends FieldEditor
{
	private List list;
	private ColorSelector colorSelector;
	private Label label;
	
	private String labelText;
	private String[][] names;
	private RGB[] colors;
	private int index = -1;

	public ColorListEditor(String[][] names, String labelText, Composite parent)
	{
		this.names = names;
		this.labelText = labelText;
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns)
	{
		((GridData) label.getLayoutData()).horizontalSpan = numColumns - 1;
		((GridData) list.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		label = new Label(parent, SWT.LEFT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		label.setText(labelText);
		
		list = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		list.addSelectionListener(getSelectionListener());
		
		colorSelector = new ColorSelector(parent);
//		list.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}

	@Override
	protected void doLoad()
	{
		if (list == null) return;

		list.removeAll();
		IPreferenceStore store = getPreferenceStore();
		colors = new RGB[names.length];
		for (int i = 0; i < names.length; i++)
		{
			colors[i] = PreferenceConverter.getColor(store, names[i][0]);
			list.add(names[i][1]);
		}
		select(0);
	}
	
	@Override
	protected void doLoadDefault()
	{
		if (list == null) return;
		
		list.removeAll();
		IPreferenceStore store = getPreferenceStore();
		colors = new RGB[names.length];
		for (int i = 0; i < names.length; i++)
		{
			colors[i] = PreferenceConverter.getDefaultColor(store, names[i][0]);
			list.add(names[i][1]);
		}
		select(0);
	}
	
	private void select(int index)
	{
		this.index = index;
		list.select(index);
		colorSelector.setColorValue(colors[index]);
	}

	@Override
	protected void doStore()
	{
		if (colors == null) return;
		
		if (index >= 0) colors[index] = colorSelector.getColorValue();
		IPreferenceStore store = getPreferenceStore();
		for (int i = 0; i < names.length; i++)
		{
			PreferenceConverter.setValue(store, names[i][0], colors[i]);
		}
	}

	@Override
	public int getNumberOfControls()
	{
		return 3;
	}

	private SelectionListener getSelectionListener()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (index >= 0) colors[index] = colorSelector.getColorValue();
				index = list.getSelectionIndex();
				colorSelector.setColorValue(colors[index]);
			}
		};
	}
}
