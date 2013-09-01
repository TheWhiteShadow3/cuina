package cuina.editor.map.internal;

import cuina.map.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class TileFlagPanel
{
	private CheckboxTableViewer viewer;
	private FlagListProvider flagContentProvider;
	private FlagInput input;
	private final List<Listener> listeners = new ArrayList<Listener>(1);
	
	public TileFlagPanel(Composite parent, IProject project)
	{
		this.viewer = new CheckboxTableViewer(new Table(parent, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION));
		this.flagContentProvider = new FlagListProvider(project);
		viewer.setContentProvider(flagContentProvider);
		viewer.setInput(input);
		viewer.addCheckStateListener(new ICheckStateListener()
		{
			@Override
			public void checkStateChanged(CheckStateChangedEvent ev)
			{
				if (input == null) return;
				
				int index = flagContentProvider.indexOf(ev.getElement());
				byte oldData = input.getFlag();
				byte data;
				if (ev.getChecked())
					data = (byte)(oldData | (1 << index));
				else
					data = (byte)(oldData & ~(1 << index));
				
				if (oldData == data) return;
				
				input.setFlag(data);
				fireSelectionListener();
			}
		});
	}
	
	public void addListener(Listener l)
	{
		listeners.add(l);
	}

	public void removeListener(Listener l)
	{
		listeners.remove(l);
	}
	
	private void fireSelectionListener()
	{
		Event event = new Event();
		event.type = SWT.Modify;
		event.widget = viewer.getTable();
		event.data = input.getFlag();
		
		for(Listener l : listeners)
		{
			l.handleEvent(event);
		}
	}
	
	public Control getTable()
	{
		return viewer.getTable();
	}
	
	public void clearTilesetField()
	{
		this.input = null;
		if (viewer != null) viewer.setInput(input);
	}

	public void setTilesetField(Tileset tileset, int id)
	{
		this.input = new FlagInput(tileset, id);
		if (viewer != null) viewer.setInput(input);
	}
	
	public void refresh()
	{
		flagContentProvider.refreshNames();
		viewer.refresh();
	}
	
	private static class FlagInput
	{
		public final Tileset tileset;
		public final int id;
		
		public FlagInput(Tileset tileset, int id)
		{
			this.tileset = tileset;
			this.id = id;
		}
		
		public byte getFlag()
		{
			return tileset.getFlags()[id];
		}
		
		public void setFlag(byte data)
		{
			tileset.getFlags()[id] = data;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof FlagInput)
			{
				FlagInput other = (FlagInput) obj;
				return (tileset == other.tileset && id == other.id);
			}
			return true;
		}
	}
	
	private static class FlagListProvider implements IStructuredContentProvider
	{
		private IProject project;
		private FlagInput input;
		private String[] names;
		
		public FlagListProvider(IProject project)
		{
			this.project = project;
			refreshNames();
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (Objects.equals(oldInput, newInput)) return;
			
			this.input = (FlagInput) newInput;
			
			CheckboxTableViewer cViewer = (CheckboxTableViewer) viewer;
			for(int i = 0; i < names.length; i++)
			{
				if (input == null)
					cViewer.setChecked(names[i], false);
				else
					cViewer.setChecked(names[i], (input.getFlag() & (1 << i)) != 0);
			}
		}

		@Override
		public Object[] getElements(Object element)
		{
			if (element instanceof FlagInput)
			{
				Object[] result = new Object[names.length];
				System.arraycopy(names, 0, result, 0, names.length);
				return result;
			}
			
			return null;
		}
		
		public void refreshNames()
		{
			IScopeContext projectScope = new ProjectScope(project);
			IPreferenceStore store = new ScopedPreferenceStore(projectScope, "cuina.editor.map");
			String nameString = store.getString("tileset.flags");
			if (nameString.isEmpty())
			{
				nameString = "Land,Wasser";
				store.setDefault("tileset.flags", nameString);
			}
			names = nameString.split(",");
		}
		
		public int indexOf(Object element)
		{
			if (!(element instanceof String)) return -1;
			for (int i = 0; i < names.length; i++) if (names[i].equals(element)) return i;
			
			return -1;
		}
		
		@Override public void dispose() {}
	}
}
