package cuina.editor.gui.internal;

import cuina.widget.Picture;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;

@Deprecated
public class WidgetPropertyPage// extends PropertyPage
{
//	private Widget widget;
//	
//	// settings for all widgets:
//	private Text nameText, keyText;
//	private Spinner xSpinner, ySpinner, widthSpinner, heightSpinner;
//	
//	private Button scrollableButton, followLinksButton, enabledButton, editableButton, passwordButton;
//	private Text themeText, pictureNameText, pictureCategoryText;
//	
//	private final int TEXT_WIDTH = 40;
//	private final int SPINNER_WIDTH = 4;
//	
//	@Override
//	protected Control createContents(Composite parent)
//	{
//		Composite composite = new Composite(parent, SWT.NONE);
//		composite.setLayout(new GridLayout());
//		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		
//		if(getElement() instanceof Widget)
//		{	
//			widget = (Widget) getElement();
//			
//			setTitle(widget.getClass().getSimpleName() + " Template");
//			
//			addNameAndKey(composite);
//			addSizeAndPostion(composite);
//			
//			if(widget instanceof HtmlArea || widget instanceof TextArea || widget instanceof Menu
//					|| widget instanceof RadioCheckBox || widget instanceof cuina.data.widget.Button
//					|| widget instanceof TextLine)
//			{
//				addSeparator(composite);
//				addButtons(composite);
//			}
//			
//			if(widget instanceof RadioCheckBox)
//			{
//				addSeparator(composite);
//				addThemeText(composite);
//			}
//			
//			if(widget instanceof Picture)
//			{
//				addSeparator(composite);
//				addPictureSettings(composite);
//				
//			}
//		}
//		
//		if(widget != null)
//			getValues();
//		
//		return composite;
//	}
//	
//	private void addSeparator(Composite parent)
//	{
//		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
//		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//	}
//	
//	private Composite createGroup(Composite parent, int coloumns)
//	{
//		Composite group = new Composite(parent, SWT.NONE);
//		group.setLayout(new GridLayout(coloumns, false));
//		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		return group;
//	}
//	
//	private void addNameAndKey(Composite parent)
//	{
//		Composite group = createGroup(parent, 2);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);
//
//		new Label(group, SWT.NONE).setText("Key:");
//		keyText = new Text(group, SWT.BORDER);
//		keyText.setLayoutData(new GridData(width, -1));
//
//		new Label(group, SWT.NONE).setText("Name:");
//		nameText = new Text(group, SWT.BORDER);
//		nameText.setLayoutData(new GridData(width, -1));
//	}
//	
//	private void addSizeAndPostion(Composite parent)
//	{
//		Composite group = createGroup(parent, 4);
//		int width = convertWidthInCharsToPixels(SPINNER_WIDTH);
//
//		new Label(group, SWT.NONE).setText("Position:");
//		xSpinner = new Spinner(group, SWT.BORDER);
//		xSpinner.setLayoutData(new GridData(width, -1));
//		new Label(group, SWT.NONE).setText("x");
//		ySpinner = new Spinner(group, SWT.BORDER);
//		ySpinner.setLayoutData(new GridData(width, -1));
//
//		new Label(group, SWT.NONE).setText("Size:");
//		widthSpinner = new Spinner(group, SWT.BORDER);
//		widthSpinner.setLayoutData(new GridData(width, -1));
//		new Label(group, SWT.NONE).setText("x");
//		heightSpinner = new Spinner(group, SWT.BORDER);
//		heightSpinner.setLayoutData(new GridData(width, -1));
//	}
//	
//	private void addButtons(Composite parent)
//	{
//		Composite group = createGroup(parent, 1);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);
//		
//		// scrollable
//		if(widget instanceof HtmlArea || widget instanceof TextArea)
//		{
//			scrollableButton = new Button(group, SWT.CHECK);
//			scrollableButton.setText("scrollable");
//			scrollableButton.setLayoutData(new GridData(width, -1));
//		}
//		
//		// follow links
//		if(widget instanceof HtmlArea)
//		{
//			followLinksButton = new Button(group, SWT.CHECK);
//			followLinksButton.setText("follow links");
//			followLinksButton.setLayoutData(new GridData(width, -1));
//		}
//		
//		// enabled
//		if(widget instanceof ISelectable)
//		{
//			enabledButton = new Button(group, SWT.CHECK);
//			enabledButton.setText("enabled");
//			enabledButton.setLayoutData(new GridData(width, -1));
//		}
//		
//		// editable and password
//		if(widget instanceof TextLine)
//		{
//			editableButton = new Button(group, SWT.CHECK);
//			editableButton.setText("editable");
//			editableButton.setLayoutData(new GridData(width, -1));
//			editableButton.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent e)
//				{
//					if(!editableButton.getSelection())
//					{
//						passwordButton.setSelection(false);
//						passwordButton.setEnabled(false);
//					} else
//						passwordButton.setEnabled(true);
//				}
//			});
//			
//			passwordButton = new Button(group, SWT.CHECK);
//			passwordButton.setText("password");
//			passwordButton.setLayoutData(new GridData(width, -1));
//		}
//		
//	}
//	
//	private void addThemeText(Composite parent)
//	{
//		Composite group = createGroup(parent, 2);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);
//		
//		// 
//		new Label(group, SWT.NONE).setText("Theme:");
//		themeText = new Text(group, SWT.BORDER);
//		themeText.setLayoutData(new GridData(width, -1));
//	}
//	
//	private void addPictureSettings(Composite parent)
//	{
//		Composite group = createGroup(parent, 2);
//		int width = convertWidthInCharsToPixels(TEXT_WIDTH);
//		
//		new Label(group, SWT.NONE).setText("Picture Name:");
//		pictureNameText = new Text(group, SWT.BORDER);
//		pictureNameText.setLayoutData(new GridData(width, -1));
//		
//		new Label(group, SWT.NONE).setText("Picture Category:");
//		pictureCategoryText = new Text(group, SWT.BORDER);
//		pictureCategoryText.setLayoutData(new GridData(width, -1));
//	}
//	
//	private void getValues()
//	{
//		nameText.setText(widget.getName() != null ? widget.getName() : "");
//		keyText.setText(widget.getKey() != null ? widget.getKey() : "");
//		
//		xSpinner.setSelection(widget.getX());
//		ySpinner.setSelection(widget.getY());
//		
//		widthSpinner.setSelection(widget.getWidth());
//		heightSpinner.setSelection(widget.getHeight());
//		
//		// scrollable
//		if(widget instanceof HtmlArea || widget instanceof TextArea)
//			scrollableButton.setSelection(((IText)widget).isScrollable());
//		
//		// follow links
//		if(widget instanceof HtmlArea)
//			followLinksButton.setSelection(((HtmlArea)widget).canFollowLinks());
//
//		// enabled
//		if(widget instanceof ISelectable)
//			enabledButton.setSelection(((ISelectable)widget).isEnabled());
//		
//		// editable and password
//		if(widget instanceof TextLine)
//		{
//			editableButton.setSelection(((IText)widget).isEditable());
//			passwordButton.setSelection(((IText)widget).isPassword());
//			
//			if(!editableButton.getSelection())
//			{
//				passwordButton.setSelection(false);
//				passwordButton.setEnabled(false);
//			} else
//				passwordButton.setEnabled(true);
//		}
//		
//		// theme for RadioCheckBox, per default it is "checkbox" and "radiobutton"
//		if(widget instanceof RadioCheckBox)
//			themeText.setText(widget.getThemeName() != null ? widget.getThemeName() : "");
//		
//		// name and category for Picture
//		if(widget instanceof Picture)
//		{
//			pictureNameText.setText(((Picture)widget).getPictureName() != null ? ((Picture)widget).getPictureName() : "");
//			pictureCategoryText.setText(((Picture)widget).getPictureCategory() != null ? ((Picture)widget).getPictureCategory() : "");
//		}
//			
//	}
//	
//	@Override
//	public boolean performOk()
//	{
//		performApply();
//		return true;
//	}
//	
//	@Override
//	protected void performApply()
//	{
//		widget.setName(nameText.getText());
//		widget.setKey(keyText.getText());
//		
//		widget.setX(xSpinner.getSelection());
//		widget.setY(ySpinner.getSelection());
//		
//		widget.setWidth(widthSpinner.getSelection());
//		widget.setHeight(heightSpinner.getSelection());
//		
//		if(widget instanceof HtmlArea || widget instanceof TextArea)
//			((IText)widget).setScrollable(scrollableButton.getSelection());
//		
//		if(widget instanceof HtmlArea)
//			((HtmlArea)widget).setCanFollowLinks(followLinksButton.getSelection());
//		
//		if(widget instanceof ISelectable)
//			((ISelectable)widget).setEnabled(enabledButton.getSelection());
//		
//		if(widget instanceof TextLine)
//		{
//			((TextLine)widget).setEditable(editableButton.getSelection());
//			((TextLine)widget).setPassword(passwordButton.getSelection());
//			
//			if(!editableButton.getSelection())
//			{
//				passwordButton.setSelection(false);
//				passwordButton.setEnabled(false);
//			} else
//				passwordButton.setEnabled(true);
//		}
//		
//		if(widget instanceof RadioCheckBox)
//			widget.setThemeName(themeText.getText());
//		
//		if(widget instanceof Picture)
//		{
//			((Picture)widget).setPictureName(pictureNameText.getText());
//			((Picture)widget).setPictureCategory(pictureCategoryText.getText());
//		}
//	}
//	
//	@Override
//	protected void performDefaults()
//	{
//		nameText.setText(widget.getClass().getSimpleName());
//		keyText.setText(widget.getClass().getSimpleName() + "_" + Integer.toHexString(widget.hashCode()));
//		
//		xSpinner.setSelection(0);
//		ySpinner.setSelection(0);
//		
//		widthSpinner.setSelection(100);
//		heightSpinner.setSelection(100);
//		
//		if(widget instanceof HtmlArea || widget instanceof TextArea)
//			scrollableButton.setSelection(true);
//		
//		if(widget instanceof HtmlArea)
//			followLinksButton.setSelection(true);
//		
//		if(widget instanceof ISelectable)
//			enabledButton.setSelection(true);
//		
//		if(widget instanceof TextLine)
//		{
//			editableButton.setSelection(false);
//			passwordButton.setSelection(false);
//			passwordButton.setEnabled(false);
//		}
//		
//		if(widget instanceof RadioCheckBox)
//			themeText.setText("checkbox");
//	}
//	
}
