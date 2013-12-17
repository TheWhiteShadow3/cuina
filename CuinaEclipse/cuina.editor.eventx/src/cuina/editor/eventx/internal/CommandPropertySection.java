package cuina.editor.eventx.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class CommandPropertySection extends AbstractPropertySection
{
	private CommandPropertyPanel panel;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage page)
	{
		super.createControls(parent, page);
		
		this.panel = new CommandPropertyPanel();
		panel.createControl(parent);
	}
}