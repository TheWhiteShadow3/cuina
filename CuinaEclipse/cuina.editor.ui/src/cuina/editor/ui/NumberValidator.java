package cuina.editor.ui;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class NumberValidator implements VerifyListener, ICellEditorValidator
{
    private static final Pattern PATTERN = Pattern.compile("^[-+]?[0-9]{0,10}([.][0-9]{0,6})?(e[+-]?[0-9]{1,2})?$");  
    
	@Override
	public void verifyText(VerifyEvent e)
	{
		String text = ((Text) e.getSource()).getText();
		e.doit = validate(text);
	}

	@Override
	public String isValid(Object value)
	{
		if (value instanceof Number) return null;
		
		if (value instanceof String)
		{
			if (validate((String) value)) return null;
		}
		
		return "Value is invalid!";
	}
	
	private boolean validate(String input)
	{
//		char[] chars = new char[input.length()];
//		input.getChars(0, chars.length, chars, 0);

//		Text text = (Text) e.getSource();
//
//		if ((",".equals(input) || ".".equals(input)) && input.indexOf(',') >= 0)
//		{
//			return false;
//		}
//
//		for (int i = 0; i < chars.length; i++)
//		{
//			if (!(('0' <= chars[i] && chars[i] <= '9') ||
//				chars[i] == '_' || chars[i] == '.' || chars[i] == ',' || chars[i] == '-'))
//			{
//				return false;
//			}
//
//			if (chars[i] == ',') chars[i] = '.';
//		}
//
//		e.text = new String(chars);

//		final String oldS = text.getText();
//		String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
		return PATTERN.matcher(input).matches();
	}
}
