package cuina.editor.eventx.internal.editors;

import cuina.editor.eventx.internal.CommandEditorContext;
import cuina.editor.eventx.internal.CommandLibrary;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
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
	private Class<?> elementClass;
	private int length;
	
	private List<Composite> editorBlocks;
	private List<TypeEditor<?>> editors;
	
	private Composite parent;
	private Handler handler;
	private Button cmdNew;
	private boolean update;
	private CommandEditorContext context;
	
	@Override
	public void init(CommandEditorContext context, Object value)
	{
		this.context = context;
		this.array = value;
		this.length = Array.getLength(value);
		this.elementClass = value.getClass().getComponentType();
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		this.handler = new Handler();
		
		this.editorBlocks = new ArrayList<Composite>(length);
		this.editors = new ArrayList<TypeEditor<?>>(length);
		for (int i = 0; i < length; i++)
		{
			addEditor(i);
		}
		
		this.cmdNew = new Button(parent, SWT.PUSH);
		cmdNew.setText("+");
		cmdNew.addListener(SWT.Selection, handler);
	}
	
	private void addEditor(int index)
	{
		TypeEditor<?> editor = CommandLibrary.newTypeEditor(elementClass.getName());
		if (editor == null) throw new RuntimeException("Unsupported class '" + elementClass + "'.");
		
		editor.init(context, Array.get(array, index));
		
		Group editorBlock = new Group(parent, SWT.NONE);
		editorBlock.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorBlock.setLayout(new GridLayout(2, false));
		editorBlock.setText('[' + Integer.toString(index) + ']');
		
		Button removeButton = new Button(editorBlock, SWT.PUSH);
		removeButton.setText("X");
		removeButton.setData(index);
		removeButton.addListener(SWT.Selection, handler);
		
		Composite wrapper = new Composite(editorBlock, SWT.NONE);
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
				length++;
				array = Array.newInstance(elementClass, length);
				addEditor(length-1);
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
					
					if (length > i)
					{
						System.arraycopy(array, i+1, array, i, length-i);
					}
					refresh();
				}
			}
			
			update = false;
		}
	}
}
