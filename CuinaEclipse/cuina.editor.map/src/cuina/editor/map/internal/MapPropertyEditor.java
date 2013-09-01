package cuina.editor.map.internal;
 
import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;
import cuina.editor.ui.WidgetFactory;
import cuina.map.Map;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.SerializationManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
 
public class MapPropertyEditor extends EditorPart implements ModifyListener
{
//  private Database db;
    
    private Map map;
	private CuinaProject project;
    private Text inName;
    private Spinner inWidth;
    private Spinner inHeight;
    private DatabaseComboViewer<Tileset> inTileset;
//    private TableViewer triggerViewer;
    
    private boolean update;
    private boolean dirty;
    
    public void setDirty(boolean value)
    {
        this.dirty = value;
        firePropertyChange(PROP_DIRTY);
    }
 
    @Override
    public boolean isDirty()
    {
        return dirty;
    }
    
    @Override
    public void doSave(IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void doSaveAs()
    {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        readInput(input);
    }
    
    private void readInput(IEditorInput input) throws PartInitException
    {
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file == null) throw new PartInitException("input must adapt an IFile.");
        try
        {
			project = (CuinaProject) input.getAdapter(CuinaProject.class);
			if (project == null) project = CuinaPlugin.getCuinaProject(file.getProject());
			
            map = (Map) input.getAdapter(Map.class);
            if (map == null) map = (Map) SerializationManager.load(file, Map.class.getClassLoader());
        }
        catch (ResourceException e)
        {
            throw new PartInitException("read Editor Input faild!", e);
        }
        setPartName(input.getName());
    }
 
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }
 
    @Override
    public void createPartControl(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));
        
        createPropertyBlock(parent);
//      createEventBlock(parent);
        setValues();
    }
 
    private void createPropertyBlock(Composite parent)
    {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Eigenschaften");
        group.setLayout(new GridLayout(3, false));
        
        inName = WidgetFactory.createText(group, "Name:");
        inName.addModifyListener(this);
        
        Group aligBox = new Group(group, SWT.NONE);
        aligBox.setText("Größe");
        aligBox.setLayout(new GridLayout(5, false));
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        gd.verticalSpan = 4;
        aligBox.setLayoutData(gd);
        
        inWidth = WidgetFactory.createSpinner(aligBox, "Width:");
        inWidth.addModifyListener(this);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
        inHeight = WidgetFactory.createSpinner(aligBox, "Height:");
        inHeight.addModifyListener(this);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
        new Label(aligBox, SWT.NONE);
        new Label(aligBox, SWT.NONE);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
        new Button(aligBox, SWT.RADIO);
 
        DataTable table = null;
        try
        {
            table = project.getService(Database.class).loadTable("Tileset");
        }
        catch (ResourceException e)
        {
            e.printStackTrace();
        }
        inTileset = WidgetFactory.createDatabaseComboViewer(group, "Tileset:", table);
    }
    
//  private void createEventBlock(Composite parent)
//  {
//      Group group = new Group(parent, SWT.NONE);
//      group.setText("Events");
//      group.setLayout(new GridLayout(3, false));
//      
//      triggerViewer = new TableViewer(group, SWT.H_SCROLL | SWT.V_SCROLL);
//      triggerViewer.setContentProvider(new TriggerContent());
//      triggerViewer.setInput(map.trigger);
//  }
    
    private void setValues()
    {
        update = true;
        inName.setText(map.getKey());
        inWidth.setSelection(map.width);
        inHeight.setSelection(map.height);
        try
        {
            Database db = project.getService(Database.class);
            Tileset tileset = db.<Tileset>loadTable("Tileset").get(map.tilesetKey);
            inTileset.setSelection(new StructuredSelection(tileset));
        }
        catch (ResourceException e)
        {
            e.printStackTrace();
        }
        update = false;
    }
    
    @Override
    public void setFocus()
    {
        getSite().getShell().setFocus();
    }
 
    @Override
    public void modifyText(ModifyEvent e)
    {
        if (update) return;
        
        setDirty(true);
    }
    
//    private class TriggerContent implements IStructuredContentProvider
//    {
//        @Override
//        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
//        {
//            // TODO Auto-generated method stub
//            
//        }
//        
//        @Override
//        public void dispose()
//        {
//            // TODO Auto-generated method stub
//            
//        }
//        
//        @Override
//        public Object[] getElements(Object inputElement)
//        {
//            // TODO Auto-generated method stub
//            return null;
//        }
//    }
}