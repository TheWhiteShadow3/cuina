package cuina.editor.ui;

import cuina.editor.core.ObjectUtil;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class ClipboardUtil
{
	/**
	 * Kopiert eine volle Kopie der Auswahl in die Zwischenablage.
	 * @param selection
	 */
	public static void toClipboard(IStructuredSelection selection)
	{
		Clipboard cb = new Clipboard(Display.getDefault());
		try
		{
			IStructuredSelection selectionCopy = ObjectUtil.clone(selection);
			// Auswahl
			LocalSelectionTransfer selectionTransfer = LocalSelectionTransfer.getTransfer();
			selectionTransfer.setSelection(selectionCopy);

			cb.setContents(new Object[] { selectionCopy },  new Transfer[] { selectionTransfer });
		}
		finally
		{
			cb.dispose();
		}
	}

	public static IStructuredSelection fromClipboard()
	{
		Clipboard cb = new Clipboard(Display.getDefault());
		try
		{
			return (IStructuredSelection) cb.getContents(LocalSelectionTransfer.getTransfer());
		}
		finally
		{
			cb.dispose();
		}
	}
	
	public static void addDropListener(Control control, Class clazz)
	{
		int ops = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(control, ops);
		target.addDropListener(new DropTargetListener()
		{
			@Override
			public void dragEnter(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("dragEnter");
			}

			@Override
			public void dragLeave(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("dragLeave");
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("dragOperationChanged");
			}

			@Override
			public void dragOver(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("dragOver");
			}

			@Override
			public void drop(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("drop");
			}

			@Override
			public void dropAccept(DropTargetEvent event)
			{
				// TODO Auto-generated method stub
				System.out.println("dropAccept");
			}
		});
	}
}
