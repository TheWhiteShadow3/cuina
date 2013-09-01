package cuina.editor.object.internal.properties;

import cuina.editor.object.ObjectAdapter;
import cuina.editor.object.ObjectPropertyPage;
import cuina.object.ObjectData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
 
public class ObjectBasePropertyPage extends ObjectPropertyPage
{
	public static final String ID = "cuina.editor.object.properties.ObjectBasePropertyPage";
	
    private static final int TEXT_WIDTH = 40;
    
    private Text idText;
    private Text nameText;
    private Text templateText;
    private Spinner xSpinner;
    private Spinner ySpinner;
    private Spinner zSpinner;
 
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
        int width = convertWidthInCharsToPixels(TEXT_WIDTH);
        
        new Label(group, SWT.NONE).setText("ID:");
        idText = new Text(group, SWT.READ_ONLY);
        
        new Label(group, SWT.NONE).setText("Name:");
        nameText = new Text(group, SWT.BORDER);
        nameText.setLayoutData(new GridData(width, -1));
        
        new Label(group, SWT.NONE).setText("Vorlage:");
        templateText = new Text(group, SWT.BORDER);
        templateText.setLayoutData(new GridData(width, -1));
        
        Group posGroup = new Group(group, SWT.DEFAULT);
        posGroup.setLayout(new GridLayout(2, false));
        posGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
        posGroup.setText("Position");
        
        new Label(posGroup, SWT.NONE).setText("X:");
        xSpinner = new Spinner(posGroup, SWT.BORDER);
        new Label(posGroup, SWT.NONE).setText("Y:");
        ySpinner = new Spinner(posGroup, SWT.BORDER);
        new Label(posGroup, SWT.NONE).setText("Z:");
        zSpinner = new Spinner(posGroup, SWT.BORDER);
    }
    
    private Composite createGroup(Composite parent, int coloumns)
    {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(coloumns, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return group;
    }
    
    private ObjectData getPhysicalObject()
    {
		if (getElement() instanceof ObjectAdapter)
			return ((ObjectAdapter) getElement()).getPhysicalObject();
		else
			return getObject();
    }
    
	private void setValues()
	{
		ObjectData obj = getObject();

		nameText.setText(obj.name);
		if (isTemplate())
		{
			idText.setText("-");
			templateText.setText("");
			templateText.setEnabled(false);
			xSpinner.setEnabled(false);
			ySpinner.setEnabled(false);
			zSpinner.setEnabled(false);
		}
		else
		{
			obj = getPhysicalObject();
			idText.setText(Integer.toString(obj.id));
			templateText.setText(obj.templateKey != null ? obj.templateKey : null);
			xSpinner.setValues(obj.x, -9999, 9999, 0, 1, 16);
			ySpinner.setValues(obj.y, -9999, 9999, 0, 1, 16);
			zSpinner.setValues(obj.z, -9999, 9999, 0, 1, 16);
		}
	}
    
    @Override
    public boolean performOk()
    {
    	ObjectData obj = getObject();
        
        obj.name = nameText.getText();
        if (isTemplate())
        {
        	obj.templateKey = null;
        }
        else
        {
        	obj = getPhysicalObject();
        	obj.templateKey = nameText.getText().isEmpty() ? null : nameText.getText();
        	obj.x = xSpinner.getSelection();
        	obj.y = ySpinner.getSelection();
        	obj.z = zSpinner.getSelection();
        }
        return true;
    }
    
    //XXX: Debug-Methode. Kann später vollständig raus.
    @Override
    public int convertWidthInCharsToPixels(int chars)
    {
        int result = super.convertWidthInCharsToPixels(chars);
        if (result <= 0)
            result = chars * 4;
        return result;
    }
}