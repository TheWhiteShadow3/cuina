package cuina.editor.map.internal;

import cuina.editor.map.EditorToolAction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class EditorActionManager implements IPropertyChangeListener
{
	private final Map<String, EditorToolAction> actions = new HashMap<String, EditorToolAction>();
	public IToolBarManager toolbarManager;

	public void addAction(EditorToolAction action)
	{
//		action.addPropertyChangeListener(this);
		actions.put(action.getId(), action);
	}
	
	public void update()
	{
		for(EditorToolAction action : actions.values())
		{
			action.execute(action.isChecked());
		}
	}

	public void activate(String id)
	{
		EditorToolAction action = actions.get(id);
		action.execute(true);
	}
	
	public void deactivate(String id)
	{
		EditorToolAction action = actions.get(id);
		action.execute(false);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
//		toolbarManager.markDirty();
		toolbarManager.update(true);
	}
}
