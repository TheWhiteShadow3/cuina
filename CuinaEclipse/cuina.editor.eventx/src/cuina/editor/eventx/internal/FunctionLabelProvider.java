package cuina.editor.eventx.internal;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FunctionLabelProvider extends LabelProvider implements ITableLabelProvider
{
	private Image IMAGE_GLOABL_CONTEXT;
	private Image IMAGE_SESSION_CONTEXT;
	private Image IMAGE_SCENE_CONTEXT;
	private Image IMAGE_STATIC_CONTEXT;
//	private Image IMAGE_OTHER_CONTEXT;
	
	public FunctionLabelProvider()
	{
		IMAGE_GLOABL_CONTEXT = EventPlugin.loadImage("global.png");
		IMAGE_SESSION_CONTEXT = EventPlugin.loadImage("session.png");
		IMAGE_SCENE_CONTEXT = EventPlugin.loadImage("scene.png");
		IMAGE_STATIC_CONTEXT = EventPlugin.loadImage("static.png");
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof FunctionEntry)
		{
			FunctionEntry func = (FunctionEntry) element;
			
			int p = func.target.indexOf(':');
			if (p == -1) return null;
			
			String context = func.target.substring(0, p);
			switch(context)
			{
				case "GLOBAL": return IMAGE_GLOABL_CONTEXT;
				case "SESSION": return IMAGE_SESSION_CONTEXT;
				case "SCENE": return IMAGE_SCENE_CONTEXT;
				case "STATIC": return IMAGE_STATIC_CONTEXT;
			}
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex > 0) return null;
			
		return getImage(element);
	}

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof Category)
		{
			if (columnIndex > 0) return null;
			
			return ((Category) element).name;
		}
		if (element instanceof FunctionEntry)
		{
			FunctionEntry func = (FunctionEntry) element;
//			if (CommandLibrary.INTERNAL_CONTEXT.equals(func.parent.context))
			switch(columnIndex)
			{
				case 0: return (func.label != null) ? func.label : func.name;
				case 1: return getArguments(func);

			}
		}
		return getText(element);
	}

	private String getArguments(FunctionEntry func)
	{
		if (func.argTypes.length == 0) return "";

		StringBuilder builder = new StringBuilder(32);
		builder.append('(');
		for (int i = 0; i < func.argTypes.length; i++)
		{
			if (i > 0) builder.append(", ");
			builder.append(func.argTypes[i].getSimpleName());
		}
		builder.append(')');
		return builder.toString();
	}

	@Override
	public void dispose()
	{
		IMAGE_GLOABL_CONTEXT.dispose();
		IMAGE_SESSION_CONTEXT.dispose();
		IMAGE_SCENE_CONTEXT.dispose();
		IMAGE_STATIC_CONTEXT.dispose();
//		IMAGE_OTHER_CONTEXT.dispose();
		
		super.dispose();
	}
}