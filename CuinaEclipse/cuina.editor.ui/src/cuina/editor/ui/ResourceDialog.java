package cuina.editor.ui;

import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ResourceDialog extends Dialog
{
	private CuinaProject project;
	private String type;
	private Resource resource;

	public ResourceDialog(Shell parent, CuinaProject project, String type, Resource initialValue)
	{
		super(parent);
		this.project = project;
		this.type = type;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.resource = initialValue;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Ressourcen Dialog");
		newShell.setSize(640, 480);
	}

	public Resource getResource()
	{
		return resource;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
//		CTabFolder resourceFolder = new CTabFolder(parent, SWT.BORDER);
//		resourceFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		resourceFolder.addListener(SWT.Selection, handler);
//		resourceFolder.setTabHeight(20);
//		Display d = resourceFolder.getDisplay();
//		resourceFolder.setBackground(new Color[]
//		{
//				d.getSystemColor(SWT.COLOR_WHITE),
//				d.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
//		}, new int[] { 50 }, true);
//
//		ResourceProvider rp = project.getService(ResourceProvider.class);
//		for (Resource res : rp.getResourceList(type))
//		{
//			CTabItem item = new CTabItem(resourceFolder, SWT.NONE);
//			item.setText(rp.getPath().lastSegment());
//			
//			Composite itemControl = new Composite(resourceFolder, SWT.NONE);
//			item.setControl(itemControl);
//			createDirectory(itemControl, dir);
//		}
		createDirectory(parent);

		return parent;
	}

	private void createDirectory(Composite parent)
	{
		DirectoryPage page = new DirectoryPage();
		page.createControl(parent);
		parent.setData(page);
	}

	private class DirectoryPage implements Listener
	{
		private List list;
		private ArrayList<Resource> paths;
		private Label imageLabel;
		private Text inFilter;
		private Pattern filter;

		public DirectoryPage()
		{
		}

		public void createControl(Composite parent)
		{
			parent.setLayout(new GridLayout(2, false));
			
			Composite cmdGroup = new Composite(parent, SWT.NONE);
			cmdGroup.setLayout(new GridLayout(2, false));
			cmdGroup.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
			
			imageLabel = new Label(parent, SWT.BORDER);
			imageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//			imageLabel.setSize(512, 512);
			imageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			
			new Label(cmdGroup, SWT.NONE).setText("Filter");
			inFilter = new Text(cmdGroup, SWT.BORDER);
			inFilter.addListener(SWT.Modify, this);
			inFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
			
			list = new List(cmdGroup, SWT.SINGLE);
			list.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
			list.addListener(SWT.Selection, this);

			fillList();
		}
		
		private void fillList()
		{
			paths = new ArrayList<Resource>();
			ArrayList<String> texts = new ArrayList<String>();
			try
			{
				ResourceProvider rp = project.getService(ResourceProvider.class);
				for(Resource res : rp.getResourceList(type))
				{
					String name = res.getName();
					if (filter != null)
					{
						Matcher m = filter.matcher(name);
						if (!m.matches()) continue;
					}
					
					paths.add(res);
					texts.add(name);
				}

				list.setItems(texts.toArray(new String[paths.size()]));
				list.select(paths.indexOf(resource));
				
				refreshImage();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		private void refreshImage()
		{
			Image oldImage = imageLabel.getImage();
			if (oldImage != null) oldImage.dispose();
			
			if (resource != null)
			{
				String lowerCase = resource.getPath().toString().toLowerCase();
				if (lowerCase.endsWith("png") || lowerCase.endsWith("bmp") || lowerCase.endsWith("jpg"))
				{
					Image image = new Image(Display.getDefault(), resource.getPath().toAbsolutePath().toString());
//					ImageData data = image.getImageData();
//					imageLabel.setSize(data.width, data.height);
					imageLabel.setImage(image);
					return;
				}
			}
			imageLabel.setImage(null);
		}

		@Override
		public void handleEvent(Event event)
		{
			if (event.widget == list)
			{
				resource = paths.get(list.getSelectionIndex());
				refreshImage();
			}
			else if (event.widget == inFilter)
			{
				filter = Pattern.compile(inFilter.getText());
				fillList();
			}
		}
	}
}
