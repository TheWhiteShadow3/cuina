package cuina.editor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;


public class AnimatorRegistry
{
	private static final String TYPE_EXTENSION = "cuina.model.Animator";

	private static List<AnimatorType> animatorTypes;
	
	private AnimatorRegistry() {}
	
	public static List<AnimatorType> getAnimatorTypes()
	{
		if (animatorTypes == null) registAnimatorTypes();
		
		return Collections.unmodifiableList(animatorTypes);
	}
	
	public static AnimatorType getAnimatorTypeFromClass(String className)
	{
		if (animatorTypes == null) registAnimatorTypes();
		
		for(AnimatorType type : animatorTypes)
		{
			if (type.getClassName().equals(className)) return type;
		}
		return null;
	}
	
	private static void registAnimatorTypes()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(TYPE_EXTENSION);
		
		animatorTypes = new ArrayList<AnimatorType>(elements.length);
		for(IConfigurationElement conf : elements)
		{
			animatorTypes.add(new AnimatorType(conf));
		}
	}
}
