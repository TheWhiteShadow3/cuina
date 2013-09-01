package cuina.editor.gui.internal.provider;

import org.eclipse.jface.viewers.LabelProvider;

@Deprecated
public class WidgetLabelProvider extends LabelProvider
{
//	private boolean extendedLabel;
//	
//	public WidgetLabelProvider(boolean extendedLabel)
//	{
//		this.extendedLabel = extendedLabel;
//	}
//	
//	@Override
//	public Image getImage(Object element)
//	{
//		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//	}
//	
//	@Override
//	public String getText(Object element)
//	{
//		String label = element.getClass().getSimpleName() + " \"" + ((Widget) element).getName() + "\" (" + ((Widget) element).getKey() + ") ";
//				
//		if(extendedLabel && element instanceof Widget)
//		{
//			Widget widget = (Widget)element;
//			label += "{ position: " + widget.getX() + "x" + widget.getY() + " size: " + widget.getWidth() + "x" + widget.getHeight();
//			
//			if(element instanceof TextLine)
//			{
//				TextLine textData = (TextLine) element;
//
//				label += " editable: " + textData.isEditable();	
//				label += " password: " + textData.isPassword();
//			} else
//
//			if(element instanceof TextArea)
//			{
//				TextArea textData = (TextArea) element;
//				
//				label += " scrollable: " + textData.isEditable();
//			} else
//
//			if(element instanceof HtmlArea)
//			{
//				HtmlArea htmlWidgetData = (HtmlArea) element;
//				
//				label += " scrollable: " + htmlWidgetData.isScrollable();
//				label += " follow links: " + htmlWidgetData.canFollowLinks();
//			} else
//
//			if(element instanceof RadioCheckBox)
//			{
//				RadioCheckBox radioData = (RadioCheckBox) element;
//
//				label += " enabled: " + radioData.isEnabled();
//				label += " theme: " + radioData.getThemeName();
//			} else
//
//			if(element instanceof Button)
//			{
//				Button buttonData = (Button) element;
//				
//				label += " enabled: " + buttonData.isEnabled();
//			} else
//
//			if(element instanceof Menu)
//			{
//				Menu menuData = (Menu) element;
//				
//				label += " enabled: " + menuData.isEnabled();
//			}
//			
//			label += " }";
//		}
//
//		if(element instanceof Widget)
//			return label;
//		
//		return "Unknown Object";
//	}
}
