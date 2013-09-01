package cuina.editor.model.properties;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class AnimatorRegistry
{
	private static final String TYPE_EXTENSION = "cuina.model.Animator";

	private static String[] animatorTypes;
	
	private AnimatorRegistry() {}
	
	public static String[] getAnimatorTypes()
	{
		if (animatorTypes == null)
		{
			IConfigurationElement[] elements = Platform.getExtensionRegistry().
					getConfigurationElementsFor(TYPE_EXTENSION);
			
			ArrayList<String> list = new ArrayList<String>(elements.length);
			for(IConfigurationElement conf : elements)
			{
				String name = conf.getAttribute("class");
				if (name == null)
					throw new NullPointerException("attribut 'class' must not be null.");
				list.add(name);
			}
			animatorTypes = list.toArray(new String[list.size()]);
		}
		return animatorTypes;
	}
}
