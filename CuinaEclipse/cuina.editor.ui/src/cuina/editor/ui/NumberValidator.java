package cuina.editor.ui;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * Eine Klasse, die alle Arten von Zahlen validieren kann.
 * @author TheWhiteShadow
 */
public class NumberValidator implements VerifyListener, ICellEditorValidator
{
    private static final Pattern PATTERN = Pattern.compile("^[-+]?[0-9]{0,10}([.][0-9]{0,6})?(e[+-]?[0-9]{1,2})?$");
    private Class<?> type;
    
    /**
     * Erstellt einen neuen NumberValidator mit folgendem regulärem Ausdruck als Validator:
     * <pre>^[-+]?[0-9]{0,10}([.][0-9]{0,6})?(e[+-]?[0-9]{1,2})?$</pre>
     */
    public NumberValidator()
    {
    	this(null);
    }
    
    /**
     * Erstellt einen neuen NumberValidator mit der angegebenen Datentyp-Klasse.
     * @param type Klasse des Datentyps.
     */
    public NumberValidator(Class<?> type)
    {
    	this.type = type;
    }
    
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
	
	private boolean validate(String value)
	{
		if (value == null) throw new NullPointerException();
		
		if (type == null) return PATTERN.matcher(value).matches();
		try
		{
			if (type == byte.class || type == Byte.class) Byte.parseByte(value);
			else if (type == short.class || type == Short.class) Short.parseShort(value);
			else if (type == int.class || type == Integer.class) Integer.parseInt(value);
			else if (type == long.class || type == Long.class) Long.parseLong(value);
			else if (type == float.class || type == Float.class) Float.parseFloat(value);
			else if (type == double.class || type == Double.class) Double.parseDouble(value);
			else if (type == BigDecimal.class) new BigDecimal(value);
			else if (type == BigInteger.class) new BigInteger(value);
			else return false; // Unbekannte Zahlenklasse.
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
}
