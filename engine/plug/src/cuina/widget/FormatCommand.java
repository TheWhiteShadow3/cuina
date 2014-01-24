package cuina.widget;

public class FormatCommand
{
	// Format-Anweisung
	char	character;
	String 	value;
	
	// Attribute für dynamisches formatieren
	int 	x;
	int 	y;
	String 	text;
	
	public FormatCommand(char c, String value)
	{
		this.character = c;
		this.value = value;
		
//		switch(c)
//		{
//			case 'n': type = FormatType.RETURN; break;
//			case 'c': type = FormatType.COLOR; break;
//			case 'f': type = FormatType.FONT; break;
//			case 'w': type = FormatType.WAIT; break;
//			case 'v': type = FormatType.VARIABLE; break;
//			case 'y': type = FormatType.STYLE; break;
//			case 's': type = FormatType.SIZE; break;
//			case 'h': type = FormatType.HERO_NAME; break;
//			case 'e': type = FormatType.EVENT_NAME; break;
//			case 'i': type = FormatType.IMAGE; break;
//			default: type = FormatType.OTHER;
//		}
	}
}