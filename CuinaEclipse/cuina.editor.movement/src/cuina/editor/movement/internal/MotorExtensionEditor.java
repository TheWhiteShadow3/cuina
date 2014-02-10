package cuina.editor.movement.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import cuina.editor.movement.DriverRegistry;
import cuina.editor.movement.DriverType;
import cuina.editor.object.ExtensionEditor;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.movement.MotorData;

public class MotorExtensionEditor extends ExtensionEditor implements Listener
{
	private static final String EXTENSION_ID = "motor";

	private Spinner speedSpinner;
	private Spinner directionSpinner;
	private Spinner frictionSpinner;
	private DefaultComboViewer<DriverType> driverCombo;

	private boolean update;
	
	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout());

		addMotorData(parent);
		addSeparator(parent);
		addDriverData(parent);
		setValues();
	}
	
	private void addSeparator(Composite parent)
	{
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	private void addMotorData(Composite parent)
	{
		Composite group = createGroup(parent, 2);
		
		speedSpinner = WidgetFactory.createSpinner(group, "Geschwindigkeit:");
		speedSpinner.setMaximum(9999);
		speedSpinner.setDigits(1);

		directionSpinner = WidgetFactory.createSpinner(group, "Richtung:");
		directionSpinner.setMaximum(3600);
		directionSpinner.setDigits(1);

		frictionSpinner = WidgetFactory.createSpinner(group, "Reibung:");
		frictionSpinner.setMaximum(1000);
		frictionSpinner.setDigits(3);
		
		speedSpinner.addListener(SWT.Modify, this);
		directionSpinner.addListener(SWT.Modify, this);
		frictionSpinner.addListener(SWT.Modify, this);
	}

	private void addDriverData(Composite parent)
	{
		Composite group = createGroup(parent, 2);
		this.driverCombo = WidgetFactory.<DriverType>createComboViewer(group, "Driver:", DriverRegistry.getDriverTypes(), true);
		driverCombo.getControl().addListener(SWT.Modify, this);
	}

	private Composite createGroup(Composite parent, int coloumns)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(coloumns, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		return group;
	}

	private MotorData getMotor()
	{
		Object ext = getExtension(EXTENSION_ID);
		if (!(ext instanceof MotorData))
			ext = new MotorData();
		
		return (MotorData) ext;
	}

	private void setValues()
	{
		update = true;
		MotorData motor = getMotor();
		
		speedSpinner.setSelection((int) (motor.speed * 10));
		directionSpinner.setSelection((int) (motor.direction * 10));
		frictionSpinner.setSelection((int) (motor.friction * 1000));
		driverCombo.setSelectedElement(DriverRegistry.getDriverTypeFromClass(motor.driver));
		
		update = false;
	}

	@Override
	public boolean performOk()
	{
		MotorData motor = getMotor();
		
		motor.speed = speedSpinner.getSelection() / 10f;
		motor.direction = directionSpinner.getSelection() / 10f;
		motor.friction = frictionSpinner.getSelection() / 1000f;
		
		DriverType dt = driverCombo.getSelectedElement();
		motor.driver = dt != null ? dt.getClassName() : null;
		setExtension(EXTENSION_ID, motor);

		return true;
	}

	@Override
	public void handleEvent(Event event)
	{
		if (update) return;
		
		fireDataChanged();
	}
}
