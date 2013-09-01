package cuina.editor.script.library;



import org.eclipse.jface.viewers.ViewerSorter;

public class TreeLibrarySorter extends ViewerSorter
{
	@Override
	public int category(Object element)
	{
		if (element instanceof Reciver)
		{
			if (((Reciver) element).getID() == null)
				return 0;
			else
				return 1;
		}
		return 2;
	}
}
