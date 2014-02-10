package cuina.editor.object.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import cuina.editor.object.ExtensionEditor;
import cuina.editor.ui.WidgetFactory;
import cuina.map.BoxData;

public class MaskExtensionEditor extends ExtensionEditor implements Listener
{
	private static final String EXTENSION_ID = "box";

	private Spinner inX;
	private Spinner inY;
	private Spinner inW;
	private Spinner inH;
	private Button checkThrough;
	private Spinner inAlpha;

	private boolean update;

	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
//		this.typeCombo = WidgetFactory.createCombo(parent, "Vorlage",
//				new String[] {"Eigene", "Zugeschnitten", "Ausgefüllt"});
//		typeCombo.addListener(SWT.Selection, this);
		
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 4));
		group.setLayout(new GridLayout(2, false));
		group.setText("Hülle");
		this.inX = WidgetFactory.createSpinner(group, "X");
		this.inY = WidgetFactory.createSpinner(group, "Y");
		this.inW = WidgetFactory.createSpinner(group, "Breite");
		this.inH = WidgetFactory.createSpinner(group, "Höhe");
		inX.addListener(SWT.Modify, this);
		inY.addListener(SWT.Modify, this);
		inW.addListener(SWT.Modify, this);
		inH.addListener(SWT.Modify, this);
		
		this.checkThrough = WidgetFactory.createButton(parent, "Durchlässig", SWT.CHECK);
		this.inAlpha = WidgetFactory.createSpinner(parent, "Alpha");
		
		checkThrough.addListener(SWT.Selection, this);
		inAlpha.addListener(SWT.Modify, this);
		
		setValues();
	}
	
	private BoxData getBox()
	{
		Object ext = getExtension(EXTENSION_ID);
		if (!(ext instanceof BoxData))
			ext = new BoxData();
		
		return (BoxData) ext;
	}
	
	private void setValues()
	{
		update = true;
		BoxData box = getBox();
		
		inX.setSelection(box.x);
		inY.setSelection(box.y);
		inW.setSelection(box.width);
		inH.setSelection(box.height);
		
		checkThrough.setSelection(box.through);
		inAlpha.setSelection(box.alphaMask);
		
		update = false;
	}
	
	@Override
    public boolean performOk()
    {
		BoxData box = getBox();
		
		box.x = inX.getSelection();
		box.y = inY.getSelection();
		box.width = inW.getSelection();
		box.height = inH.getSelection();
		
		box.through = checkThrough.getSelection();
		box.alphaMask = inAlpha.getSelection();
		setExtension(EXTENSION_ID, box);
        
        return true;
    }

	@Override
	public void handleEvent(Event event)
	{
		if (update) return;
		
		fireDataChanged();
		
//		if (event.widget == typeCombo)
//		{
//			switch(typeCombo.getSelectionIndex())
//			{
//				case 1: 
//			}
//		}
	}
}
