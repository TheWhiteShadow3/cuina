package cuina.editor.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Eine Sektion im Tabbed-Property-Fenster.
 * Zeigt eine Vorschau für die ausgewählte Datei an.
 * @author TheWhiteShadow
 */
public class PreviewPropertySection extends AbstractPropertySection
{
	private static enum ContentType
	{
		UNDEFINED,
		ASCII,
		IMAGE
	}
	
	private static final String[] ASCII_FILES =
	{
		"txt", "bat", "java", "htm", "html", "xhtml", "css", "cfg", "rb", "ini", "nfo", "xml"
	};
	private static final String[] IMGAE_FILES = {"png", "jpg", "bmp"};
	
	private Composite parent;
	private IFile file;
	private ContentType contentType;
	private boolean needRefresh;
	
	@Override
	public void createControls(Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage)
	{
		super.createControls(parent, tabbedPropertySheetPage);
		this.parent = parent;
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new FillLayout());
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		
		if (selection instanceof IStructuredSelection)
		{	// Die Spezifiaktion im Erweiterungspunkt erlaubt diesen Cast.
			IResource res = (IResource) ((IStructuredSelection) selection).getFirstElement();
			
			if (res instanceof IFolder)
				displayFolder((IFolder) res);
			else if (res instanceof IFile)
				displayFileContent((IFile) res);
			else
				assert false;
		}
		if (parent != null && needRefresh) refresh();
	}

	/*
	 * TODO: Dateiinhalt wird nicht unverzüglich angezeigt.
	 * Bei ASCII-Dateien tritt der Fehler nicht immer auf, bei Images immer.
	 */
	@Override
	public void refresh()
	{
		if (!needRefresh) return;

		for(Control c : parent.getChildren()) c.dispose();
		
		switch(contentType)
		{
			case ASCII:
				Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY);
				text.setText(readAsciiFile(file));
				break;
			case IMAGE:
				new ImageBox(parent, file);
				break;
			case UNDEFINED:
				break;
		}

		needRefresh = false;
	}
	
	private String readAsciiFile(IFile file)
	{
		try
		{
			InputStreamReader in = new InputStreamReader(file.getContents());
			StringBuilder builder = new StringBuilder(2048);
			char[] buffer = new char[2048];
			int lenght;
			while ((lenght = in.read(buffer)) > 0)
			{
				builder.append(buffer, 0, lenght);
			}
			return builder.toString();
		}
		catch (IOException | CoreException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void dispose()
	{
		parent = null;
	}

	private void displayFolder(IFolder folder)
	{
		this.file = null;
		this.contentType = ContentType.UNDEFINED;
		this.needRefresh = true;
	}
	
	private void displayFileContent(IFile file)
	{
		if (Objects.equals(this.file, file)) return;
		
		this.file = file;
		this.contentType = getContentType(file);
		System.out.println("File " + file.getName() + " ist vom Typ " + contentType.name());
		this.needRefresh = true;
	}
	
	private ContentType getContentType(IFile file)
	{
		String ext = file.getFileExtension();
		
		if (ext.isEmpty()) return ContentType.UNDEFINED;
		
		for (int i = 0; i < ASCII_FILES.length; i++)
			if (ASCII_FILES[i].equals(ext))
				return ContentType.ASCII;
		
		for (int i = 0; i < IMGAE_FILES.length; i++)
			if (IMGAE_FILES[i].equals(ext))
				return ContentType.IMAGE;
		
		return ContentType.UNDEFINED;
	}
	
	private static class ImageBox extends Canvas
	{
		private Image image;
		
		public ImageBox(Composite parent, IFile file)
		{
			super(parent, SWT.DOUBLE_BUFFERED);
			
			try
			{
				this.image = new Image(parent.getDisplay(), file.getContents());
				addPaintListener(new PaintListener()
				{
					@Override
					public void paintControl(PaintEvent e)
					{
						e.gc.drawImage(image, 0, 0);
					}
				});
			}
			catch (CoreException e1)
			{
				e1.printStackTrace();
			}
		}

		@Override
		public void dispose()
		{
			image.dispose();
			super.dispose();
		}
	}
}
