package cuina.editor.debug;
 
import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
 
public class ProjectBlock
{
    private CuinaTab cuinaTab;
    private Shell shell;
    private ProjectListener listener;
    private Text inProject;
    private Button cmdProject;
    
    public ProjectBlock(CuinaTab cuinaTab)
    {
        this.cuinaTab = cuinaTab;
    }
    
    public void createControl(Composite parent)
    {
        shell = parent.getShell();
        listener = new ProjectListener();
        
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        group.setText("Projekt");
        
        createProjectSelection(group);
    }
    
    private void createProjectSelection(Composite parent)
    {
        inProject = new Text(parent, SWT.BORDER);
        inProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        inProject.addModifyListener(listener);
        
        cmdProject = new Button(parent, SWT.NONE);
        cmdProject.setText("Browse...");
        cmdProject.addSelectionListener(listener);
    }
    
    public void setDefaults(ILaunchConfigurationWorkingCopy config)
    {
        config.setAttribute(CuinaLaunch.PROJECT_NAME, "");
//      config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, "");
//      config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
    }
    
    public void initializeFrom(ILaunchConfiguration config) throws CoreException
    {
        inProject.setText(config.getAttribute(CuinaLaunch.PROJECT_NAME, ""));
    }
    
    public void performApply(ILaunchConfigurationWorkingCopy config)
    {
        config.setAttribute(CuinaLaunch.PROJECT_NAME, inProject.getText());
//      config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, inProject.getText());
//      config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, inProject.getText());
    }
    
    public String validate()
    {
        String name = inProject.getText();
        if (name.isEmpty())
            return "Project can not be empty!";
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        if (!project.exists())
            return "Project '" + name + "' not found!";
        return null;
    }
    
    private CuinaProject chooseCuinaProject()
    {
        ILabelProvider labelProvider= new LabelProvider(); //XXX war: CuinaElementLabelProvider
        ElementListSelectionDialog dialog= new ElementListSelectionDialog(shell, labelProvider);
        dialog.setTitle("Projekt Auswahl"); 
        dialog.setMessage("Wähle ein Projekt aus."); 
        
        dialog.setElements(CuinaPlugin.getCuinaProjects());
        
        CuinaProject project = getCuinaProject();
        if (project != null) 
        {
            dialog.setInitialSelections(new Object[] { project });
        }
        if (dialog.open() == Window.OK)
        {           
            return (CuinaProject) dialog.getFirstResult();
        }       
        return null;        
    }
 
    public CuinaProject getCuinaProject()
    {
        if (inProject.getText().isEmpty()) return null;
 
        CuinaProject project = CuinaPlugin.getCuinaProject(inProject.getText());
        if (project.valid())
            return project;
        else
            return null;
    }
    
    public void setProjectText(String name)
    {
        inProject.setText(name);
    }
 
    private class ProjectListener implements ModifyListener, SelectionListener
    {
        @Override
        public void modifyText(ModifyEvent e)
        {
            cuinaTab.updateTab();
        }
 
        @Override
        public void widgetSelected(SelectionEvent e)
        {
        	CuinaProject project = chooseCuinaProject();
            if (project != null)
                inProject.setText(project.getName());
        }
 
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {}
    }
}