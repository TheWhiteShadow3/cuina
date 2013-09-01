package cuina.editor.script;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ViwerTester extends TitleAreaDialog implements Listener
{
	private static ArrayList<String> entries = new ArrayList<String>();
	private TableViewer viewer;
	
	public ViwerTester()
	{
		super(null);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite blub = new Composite(parent, SWT.NONE);
		blub.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		blub.setLayout(new FillLayout());
		
		viewer = new TableViewer(blub);
		viewer.getControl().addListener(SWT.KeyDown, this);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setInput(new Object());
		
		return blub;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		entries.add("Anna");
		entries.add("Lisa");
		entries.add("Krista");
		entries.add("Yuna");
		entries.add("Saki");
		entries.add("Biankna");
		entries.add("Melanie");
		
		new ViwerTester().open();
	}

	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Test-Dialog");
		shell.setSize(640, 480);
	}

	@Override
	public void handleEvent(Event event)
	{
		if (event.type == SWT.KeyDown)
		{
			System.out.println("Event: " + event.keyCode);
			IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
			entries.remove(s.getFirstElement());
			viewer.refresh();
		}
	}
	
	private class MyContentProvider implements IStructuredContentProvider
	{
		@Override
		public void dispose()
		{}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{}

		@Override
		public Object[] getElements(Object inputElement)
		{
			return entries.toArray();
		}
	}
}
