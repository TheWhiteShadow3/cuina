package cuina.editor.model.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import cuina.animation.ModelData;
import cuina.editor.model.AnimatorRegistry;
import cuina.editor.model.AnimatorType;
import cuina.editor.object.ExtensionEditor;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.ResourceButton;
import cuina.editor.ui.WidgetFactory;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

public class ModelExtensionEditor extends ExtensionEditor implements Listener
{
	private static final String EXTENSION_ID = "model";

	private ResourceButton cmdImage;
	private Spinner frameCountSpinner;
	private Spinner aniCountSpinner;
	private Spinner startAniSpinner;
	private Spinner startFrameSpinner;
	private DefaultComboViewer<AnimatorType> animatorCombo;
	private Button standAnimation;
	private ModelPanel modelPanel;
	private boolean update;
	
	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout());

		addModelData(parent);
		setValues();

		// XXX: Uncool. Deaktiviert das Anwählen statt der Eingabe.
		// if (isTemplate()) composite.setEnabled(false);
	}

	private void addSeparator(Composite parent, int rows)
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, rows, 1));
    }

	private void addModelData(Composite parent)
	{
		Composite group = createGroup(parent, 3);
		this.cmdImage = WidgetFactory.createImageButton(group, getCuinaProject(), "Filename:", null);
		cmdImage.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		cmdImage.addListener(SWT.Modify, this);
		
		frameCountSpinner = WidgetFactory.createSpinner(group, "Frames:");

		this.modelPanel = new ModelPanel(group, SWT.BORDER);
		modelPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 8));
		modelPanel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		aniCountSpinner = WidgetFactory.createSpinner(group, "Animationen:");
		startFrameSpinner = WidgetFactory.createSpinner(group, "Start-Frame:");
		startAniSpinner = WidgetFactory.createSpinner(group, "Start-Animation:");
		standAnimation = WidgetFactory.createButton(group, "Standanimation", SWT.CHECK);

		frameCountSpinner.addListener(SWT.Selection, this);
		aniCountSpinner.addListener(SWT.Selection, this);
		startFrameSpinner.addListener(SWT.Selection, this);
		startAniSpinner.addListener(SWT.Selection, this);
		standAnimation.addListener(SWT.Selection, this);
		
		addSeparator(group, 2);
		addAnimatorData(group);
	}

    private void addAnimatorData(Composite parent)
	{
		this.animatorCombo = WidgetFactory.createComboViewer(parent, "Animator:", AnimatorRegistry.getAnimatorTypes(), true);
		animatorCombo.getControl().addListener(SWT.Modify, this);
	}
    
	private Composite createGroup(Composite parent, int coloumns)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(coloumns, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return group;
	}

	private ModelData getModel()
	{
		Object ext = getExtension(EXTENSION_ID);
		if (!(ext instanceof ModelData))
			ext = new ModelData();

		return (ModelData) ext;
    }

	private void setValues()
	{
		update = true;
		ModelData model = getModel();

		loadImage(model.fileName);
		cmdImage.setResourceName(model.fileName);
		frameCountSpinner.setSelection(model.frames);
		aniCountSpinner.setSelection(model.animations);
		startFrameSpinner.setSelection(model.frame);
		startAniSpinner.setSelection(model.animation);
		standAnimation.setSelection(model.standAnimation);
		animatorCombo.setSelectedElement(AnimatorRegistry.getAnimatorTypeFromClass(model.animator));
		
		startFrameSpinner.setMaximum(model.frames-1);
		startAniSpinner.setMaximum(model.animations-1);
		
		modelPanel.setFrames(model.frames);
		modelPanel.setAnimations(model.animations);
		modelPanel.setFrameIndex(model.frame);
		modelPanel.setAnimationIndex(model.animation);
		if (model.standAnimation)
			modelPanel.startAnimation();
		
		update = false;
	}
	
	@Override
	public boolean performOk()
	{
		ModelData model = getModel();
		model.fileName = cmdImage.getResourceName();
		model.frames = frameCountSpinner.getSelection();
		model.animations = aniCountSpinner.getSelection();
		model.frame = startFrameSpinner.getSelection();
		model.animation = startAniSpinner.getSelection();
		model.standAnimation = standAnimation.getSelection();

		AnimatorType at = animatorCombo.getSelectedElement();
		model.animator = at != null ? at.getClassName() : null;
		setExtension(EXTENSION_ID, model);

		return true;
	}

	@Override
	public void handleEvent(Event ev)
	{
		if (update) return;
		
		if (ev.widget == cmdImage)
			loadImage(cmdImage.getResourceName());
		
		if (ev.widget == frameCountSpinner)
		{
			int frames = frameCountSpinner.getSelection();
			startFrameSpinner.setMaximum(frames-1);
			modelPanel.setFrames(frames);
		}
		
		if (ev.widget == aniCountSpinner)
		{
			int animations = aniCountSpinner.getSelection();
			startAniSpinner.setMaximum(animations-1);
			modelPanel.setAnimations(animations);
		}
		
		if (ev.widget == startFrameSpinner)
			modelPanel.setFrameIndex(startFrameSpinner.getSelection());
		
		if (ev.widget == startAniSpinner)
			modelPanel.setAnimationIndex(startAniSpinner.getSelection());
		
		if (ev.widget == standAnimation)
			if (standAnimation.getSelection())
				modelPanel.startAnimation();
			else
				modelPanel.stopAnimation();
		
		fireDataChanged();
	}

	private boolean loadImage(String fileName)
	{
		try
		{
			ResourceProvider rp = ResourceManager.getResourceProvider(getCuinaProject());
			Resource res = rp.getResource(ResourceManager.KEY_GRAPHICS, fileName);
			Image image = new Image(Display.getCurrent(), res.getPath().toAbsolutePath().toString());
			modelPanel.setImage(image);
			return true;
		}
		catch (ResourceException e)
		{
			return false;
		}
	}
}
