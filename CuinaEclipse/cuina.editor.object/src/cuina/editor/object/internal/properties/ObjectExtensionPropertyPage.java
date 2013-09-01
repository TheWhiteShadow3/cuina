package cuina.editor.object.internal.properties;

import cuina.editor.object.ObjectPropertyPage;
import cuina.object.ObjectData;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class ObjectExtensionPropertyPage extends ObjectPropertyPage
{
	public static final String ID = "cuina.editor.object.properties.ObjectExtensionPropertyPage";

	private List extensionList;
	private Button cmdAdd;
	private Button cmdDel;
	
	@Override
	protected Control createContents(Composite parent)
	{
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        addObjectData(composite);
        setValues();
        
        return composite;
	}
	
	private void addObjectData(Composite parent)
	{
		 Composite group = createGroup(parent, 2);
		 
		 new Label(group, SWT.NONE).setText("Erweiterungen");
		 
		 extensionList = new List(group, SWT.NONE);
		 
		 cmdAdd = new Button(group, SWT.PUSH);
		 cmdAdd.setText("Hinzufügen");
		 cmdDel = new Button(group, SWT.PUSH);
		 cmdDel.setText("Entfernen");
	}
	
	private void setValues()
	{
		ObjectData obj = getObject();
		
		String[] items = new String[obj.extensions.size()];
		
		Iterator<String> itr = obj.extensions.keySet().iterator();
		for(int i = 0; itr.hasNext(); i++)
		{
			String key = itr.next();
			items[i] = key + " - " + obj.extensions.get(key).getClass().getName();
		}
		extensionList.setItems(items);
	}
	
    private Composite createGroup(Composite parent, int coloumns)
    {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(coloumns, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return group;
    }
	
    @Override
    public boolean performOk()
    {
    	return true;
    }
}
