package cuina.widget;

import java.io.Serializable;

import de.matthiasmann.twl.Widget;

public interface WidgetDescriptor extends Serializable
{
	public Widget getWidget(String key);
	public Widget createRoot();
	
	/**
	 * Diese Methode wird aufgerufen, nachdem das Wurzel-Widget dem Baum hinzugefügt wurde.
	 */
	public void postBuild();
	public String getTheme();
}
