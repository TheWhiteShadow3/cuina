package cuina.editor.model.internal.properties;

import cuina.animation.ModelData;
import cuina.editor.object.ObjectPropertyPage;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
 
public class ModelPropertyPage extends ObjectPropertyPage implements ModifyListener
{
    private static final int TEXT_WIDTH = 40;
    private static final String EXTENSION_ID = "Model";
    
    private Text fileText;
    private Spinner frameCountSpinner;
    private Spinner aniCountSpinner;
    private Spinner startAniSpinner;
    private Spinner startFrameSpinner;
    private Combo animatorCombo;
	private Button standAnimation;
	private Label imageLabel;
    private boolean update;
	
    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        addModelData(composite);
        addSeparator(composite);
        addAnimatorData(composite);
        setValues();
        
        //XXX: Uncool. Deaktiviert das Anwählen statt der Eingabe.
//      if (isTemplate()) composite.setEnabled(false);
        
        return composite;
    }
    
    private void addSeparator(Composite parent)
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }
    
	private void addModelData(Composite parent)
	{
		Composite group = createGroup(parent, 3);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);

		new Label(group, SWT.NONE).setText("Filename:");
		fileText = new Text(group, SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		fileText.addModifyListener(this);
        
        new Label(group, SWT.NONE).setText("Frames:");
        frameCountSpinner = new Spinner(group, SWT.BORDER);
        
        imageLabel = new Label(group, SWT.BORDER);
        imageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 4));
        imageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(group, SWT.NONE).setText("Animationen:");
        aniCountSpinner = new Spinner(group, SWT.BORDER);
        
        new Label(group, SWT.NONE).setText("Start-Frame:");
        startFrameSpinner = new Spinner(group, SWT.BORDER);
        
        new Label(group, SWT.NONE).setText("Start-Animation:");
        startAniSpinner = new Spinner(group, SWT.BORDER);
        
        new Label(group, SWT.NONE).setText("Standanimation:");
        standAnimation = new Button(group, SWT.CHECK);
    }
    
    private void addAnimatorData(Composite parent)
    {
        Composite group = createGroup(parent, 2);
        int width = convertWidthInCharsToPixels(TEXT_WIDTH);
        
        new Label(group, SWT.NONE).setText("Animator:");
        animatorCombo = new Combo(group, SWT.BORDER);
        animatorCombo.setLayoutData(new GridData(width, -1));
        animatorCombo.setItems(AnimatorRegistry.getAnimatorTypes());
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
		update = true;
		ModelData model = (ModelData) getElement().getAdapter(ModelData.class);
		if (model == null)
		{
			model = new ModelData();
			getObject().extensions.put(EXTENSION_ID, model);
		}

		loadImage(model.fileName);
		fileText.setText(model.fileName);
		frameCountSpinner.setSelection(model.frames);
		aniCountSpinner.setSelection(model.animations);
		startFrameSpinner.setSelection(model.frame);
		startAniSpinner.setSelection(model.animation);
		standAnimation.setSelection(model.standAnimation);
		animatorCombo.setText(model.animator);
		
		update = false;
	}

//	private
	
	@Override
    public boolean performOk()
    {
		ModelData model = (ModelData) getElement().getAdapter(ModelData.class);
        if (model != null)
        {
        	model.fileName = fileText.getText();
        	model.frames = frameCountSpinner.getSelection();
        	model.animations = aniCountSpinner.getSelection();
        	model.frame = startFrameSpinner.getSelection();
        	model.animation = startAniSpinner.getSelection();
        	model.standAnimation = standAnimation.getSelection();
        	model.animator = animatorCombo.getText();
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

	@Override
	public void modifyText(ModifyEvent e)
	{
		if (update) return;
		
		String fileName = fileText.getText();
		if (!loadImage(fileName))
			setErrorMessage("Ungültiger Dateiname.");
		else
			setErrorMessage(null);
	}
	
	private boolean loadImage(String fileName)
	{
		try
		{
			ResourceProvider rp = ResourceManager.getResourceProvider(getProject());
			Resource res = rp.getResource(ResourceManager.KEY_GRAPHICS, fileName);
			Image image = new Image(Display.getCurrent(), res.getPath().toAbsolutePath().toString());
			imageLabel.setImage(image);
			return true;
		}
		catch (ResourceException e)
		{
			return false;
		}
	}
}