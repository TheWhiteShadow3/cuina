package cuina.editor.object.internal;

import cuina.editor.object.ExtensionEditor;
import cuina.editor.ui.WidgetFactory;
import cuina.map.BoxData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

public class BoxEditor implements ExtensionEditor<BoxData>, Listener
{
	private IExtensionContext context;
	private BoxData box;
	
	private Combo typeCombo;
	private Spinner inX;
	private Spinner inY;
	private Spinner inW;
	private Spinner inH;
	private Button checkThrough;
	private Spinner inAlpha;
	
	@Override
	public BoxData getData()
	{
		if (box == null) box = new BoxData();
		
		box.x = inX.getSelection();
		box.y = inY.getSelection();
		box.width = inW.getSelection();
		box.height = inH.getSelection();
		
		box.through = checkThrough.getSelection();
		box.alphaMask = inAlpha.getSelection();
		
		return box;
	}

	@Override
	public void setData(BoxData box)
	{
		this.box = box;
		setValues();
	}

	@Override
	public void init(IExtensionContext context)
	{
		this.context = context;
	}

	@Override
	public void createComponents(Composite parent)
	{
		this.typeCombo = WidgetFactory.createCombo(parent, "Vorlage",
				new String[] {"Leer", "Zugeschnitten", "Ausgefüllt", "Eigene"});
		typeCombo.addListener(SWT.Selection, this);
		
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Hülle");
		this.inX = WidgetFactory.createSpinner(group, "X");
		this.inY = WidgetFactory.createSpinner(group, "Y");
		this.inW = WidgetFactory.createSpinner(group, "Breite");
		this.inH = WidgetFactory.createSpinner(group, "Höhe");
		
		this.checkThrough = WidgetFactory.createButton(parent, "Durchlässig", SWT.CHECK);
		this.inAlpha = WidgetFactory.createSpinner(parent, "Alpha");
	}
	
	private void setValues()
	{
		if (box == null) return;
		
		inX.setSelection(box.x);
		inY.setSelection(box.y);
		inW.setSelection(box.width);
		inH.setSelection(box.height);
		
		checkThrough.setSelection(box.through);
		inAlpha.setSelection(box.alphaMask);
	}

	@Override
	public void handleEvent(Event event)
	{
		if (event.widget == typeCombo)
		{
			
		}
	}
}
