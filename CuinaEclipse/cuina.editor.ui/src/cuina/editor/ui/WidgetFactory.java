package cuina.editor.ui;
 
import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
 
public class WidgetFactory
{
	public static int DEFAULT_WIDTH = 128;
	
    private WidgetFactory() {}
    
    public static Text createText(Composite parent, String title)
    {
        new Label(parent, SWT.NONE).setText(title);
        Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(new GridData(DEFAULT_WIDTH, 14));
        return text;
    }
    
    public static Spinner createSpinner(Composite parent, String title)
    {
        new Label(parent, SWT.NONE).setText(title);
        Spinner spinner = new Spinner(parent, SWT.BORDER);
        spinner.setLayoutData(new GridData(48, 14));
        return spinner;
    }
    
//     //TODO: evtl. Teilweise auslagern in cuina.database.ui
    public static <E extends DatabaseObject> DatabaseComboViewer<E> createDatabaseComboViewer(
    		Composite parent, String title, final DataTable<E> table)
    {
    	
        new Label(parent, SWT.NONE).setText(title);
        DatabaseComboViewer<E> combo = new DatabaseComboViewer<E>(parent, SWT.DEFAULT | SWT.FILL);
        combo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        combo.setTable(table);
        return combo;
    }
    
    public static ResourceButton createImageButton(Composite parent, CuinaProject project, String title, String initialValue)
    {
    	new Label(parent, SWT.NONE).setText(title);
    	ResourceButton button = new ResourceButton(parent, project, ResourceManager.KEY_GRAPHICS);
    	button.setResourceName(initialValue);
    	button.setLayoutData(new GridData(DEFAULT_WIDTH + 12, 24));
    	return button;
    }
}

