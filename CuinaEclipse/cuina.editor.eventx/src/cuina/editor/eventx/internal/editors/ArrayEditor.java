package cuina.editor.eventx.internal.editors;

import cuina.editor.eventx.internal.CommandEditorContext;
import cuina.editor.eventx.internal.CommandLibrary;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

public class ArrayEditor implements TypeEditor<Object>
{
	private Object array;
	private String elementType;
	private int length;
	
	private List<Composite> editorBlocks;
	private List<TypeEditor<?>> editors;
	
	private Composite parent;
	private Handler handler;
	private Button cmdNew;
	private boolean update;
	private CommandEditorContext context;
	
	@Override
	public void init(CommandEditorContext context, String type, Object value)
	{
		this.context = context;
		this.array = value;
		this.length = Array.getLength(value);
		if (type.endsWith("[]"))
		{
			this.elementType = type.substring(0, type.length()-2);
		}
		else
		{
			try
			{
				Class<?> arrayClass = context.getCommandLibrary().getClass(type);
				this.elementType = arrayClass.getComponentType().getName();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		this.handler = new Handler();
		
		this.cmdNew = new Button(parent, SWT.PUSH);
		cmdNew.setText("+");
		cmdNew.addListener(SWT.Selection, handler);
		if (elementType == null)
			cmdNew.setEnabled(false);
		
		this.editorBlocks = new ArrayList<Composite>(length);
		this.editors = new ArrayList<TypeEditor<?>>(length);
		for (int i = 0; i < length; i++)
		{
			addEditor(i);
		}
	}
	
	private void addEditor(int index)
	{
		TypeEditor<?> editor = CommandLibrary.newTypeEditor(elementType);
		if (editor == null) throw new RuntimeException("Unsupported class '" + elementType + "'.");
		
		editor.init(context, elementType, Array.get(array, index));
		
		Group editorBlock = new Group(parent, SWT.NONE);
		editorBlock.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorBlock.setLayout(new GridLayout(2, false));
		editorBlock.setText('[' + Integer.toString(index) + ']');
		
		Button removeButton = new Button(editorBlock, SWT.PUSH);
		removeButton.setText("X");
		removeButton.setData(index);
		removeButton.addListener(SWT.Selection, handler);
		
		Composite wrapper = new Composite(editorBlock, SWT.NONE);
		wrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wrapper.setLayout(new FillLayout());
		editor.createComponents(wrapper);

		// Verschiebe den Neu-Knopf nach unten.
		cmdNew.moveBelow(editorBlock);
		
		editorBlocks.add(editorBlock);
		editors.add(editor);
		refresh();
	}
	
	private void refresh()
	{
		parent.getParent().getParent().layout(true, true);
		parent.getParent().layout(true, true);
		parent.layout(true, true);
	}

	@Override
	public Object getValue()
	{
		for(int i = 0; i < editors.size(); i++)
			Array.set(array, i, editors.get(i).getValue());
		
		return array;
	}

	@Override
	public boolean apply()
	{
		for(TypeEditor<?> e : editors)
			if (!e.apply()) return false;
		
		return true;
	}
	
	private class Handler implements Listener
	{
		@Override
		public void handleEvent(Event event)
		{
			if (update) return;
			update = true;
			
			if (event.widget == cmdNew)
			{
				Object newArray = Array.newInstance(array.getClass().getComponentType(), length+1);
				System.arraycopy(array, 0, newArray, 0, length);
				array = newArray;
				addEditor(length);
				length++;
			}
			else
			{
				Integer index = (Integer) event.widget.getData();
				if (index != null)
				{
					int i = index;
					editorBlocks.get(i).dispose();
					editorBlocks.remove(i);
					editors.remove(i);
					length--;
					
					Object newArray = Array.newInstance(array.getClass().getComponentType(), length);
					if (length > i)
					{
						System.arraycopy(array, i+1, newArray, i, length-i);
					}
					array = newArray;
					refresh();
				}
			}
			
			update = false;
		}
	}
}
