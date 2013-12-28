package cuina.editor.movement.properties;

import cuina.editor.object.ObjectPropertyPage;
import cuina.map.movement.MotorData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class MotorPropertyPage extends ObjectPropertyPage
{
    private static final int TEXT_WIDTH = 40;
    private static final String EXTENSION_ID = "Motor";
    
    private Spinner speedSpinner;
    private Spinner directionSpinner;
    private Spinner frictionSpinner;
	private Combo driverCombo;
//	private boolean update;
	
	@Override
	protected Control createContents(Composite parent)
	{
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        addMotorData(composite);
        addSeparator(composite);
        addDriverData(composite);
        setValues();
        
        return composite;
	}
	
	private void addSeparator(Composite parent)
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }
	
	private void addMotorData(Composite parent)
	{
		Composite group = createGroup(parent, 2);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);
		
        new Label(group, SWT.NONE).setText("Geschwindigkeit:");
        speedSpinner = new Spinner(group, SWT.BORDER);
        speedSpinner.setSize(96, -1);
        speedSpinner.setMaximum(9999);
        speedSpinner.setDigits(1);
        
        new Label(group, SWT.NONE).setText("Richtung:");
        directionSpinner = new Spinner(group, SWT.BORDER);
        directionSpinner.setSize(96, -1);
        directionSpinner.setMaximum(3600);
        directionSpinner.setDigits(1);
        
        new Label(group, SWT.NONE).setText("Reibung:");
        frictionSpinner = new Spinner(group, SWT.BORDER);
        frictionSpinner.setSize(96, -1);
        frictionSpinner.setMaximum(1000);
        frictionSpinner.setDigits(3);
	}
	
    private void addDriverData(Composite parent)
    {
        Composite group = createGroup(parent, 2);
        int width = convertWidthInCharsToPixels(TEXT_WIDTH);
        
        new Label(group, SWT.NONE).setText("Driver:");
        driverCombo = new Combo(group, SWT.BORDER);
        driverCombo.setLayoutData(new GridData(width, -1));
        driverCombo.setItems(DriverRegistry.getDriverTypes());
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
//		update = true;
		MotorData motor = (MotorData) getElement().getAdapter(MotorData.class);
		if (motor == null)
		{
			motor = new MotorData();
			getObject().extensions.put(EXTENSION_ID, motor);
		}
		
		speedSpinner.setSelection((int) (motor.speed * 10));
		directionSpinner.setSelection((int) (motor.direction * 10));
		frictionSpinner.setSelection((int) (motor.friction * 1000));
		driverCombo.setText(motor.driver);
		
//		update = false;
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
    
	@Override
    public boolean performOk()
    {
		MotorData motor = (MotorData) getElement().getAdapter(MotorData.class);
        if (motor != null)
        {
        	motor.speed = speedSpinner.getSelection() / 10f;
        	motor.direction = directionSpinner.getSelection() / 10f;
        	motor.friction = frictionSpinner.getSelection() / 1000f;
        	motor.driver = driverCombo.getText();
        }
        
        return true;
    }
}
