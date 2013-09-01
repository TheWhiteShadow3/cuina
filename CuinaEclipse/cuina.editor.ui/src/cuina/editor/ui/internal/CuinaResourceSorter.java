package cuina.editor.ui.internal;

import java.text.Collator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class CuinaResourceSorter extends ViewerSorter
{
	public CuinaResourceSorter() { super(); }
    public CuinaResourceSorter(Collator collator) { super(collator); }
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		if (e1 instanceof IFolder || e2 instanceof IFolder)
		{
			if (e1 instanceof IFolder && e2 instanceof IFolder) return 0;
			return (e1 instanceof IFolder) ? -1 : 1;
		}
		
		return super.compare(viewer, e1, e2);
	}
	
}
