package cuina.widget;


/**
 * Stellt Methoden zur Verfügung um Text mit speziellen Schlüsselzeichen zu formatieren.<br>
 * Hauptaufgabe ist das einfärben, einfügen von Variablen, Font und größe ändern und Bilder einzufügen.
 * @author TheWhiteShadow
 * @version 1.0
 */
public class TextParser
{
//	private static final char KeyChar = '$';
//	
//	private Image image;
//	private BitmapFont font;
//	private String srcText;
//	private String formatText;
//	/** externer Text-Formatierer */
//	private TextTokenHandler handler;
//
//	private int waitCount = 0;	// Wartezeit in Frames
//	private int drawX = 0;		// X-Position fürs zeichnen
//	private int drawY = 0;		// Y-Position fürs zeichnen
//	private int index = 0;		// Token-Index
//	private int tokenPos = 0;	// Position innerhalb eines Token
//	private int lineHeight;		// Zeilenhöhe
//	
//	private int waitTime = 2;	// Wartezeit pro Zeichen
//	
//	ArrayList<String> tokens = new ArrayList<String>(4);
//	ArrayList<FormatCommand> formats = new ArrayList<FormatCommand>(4);
//	
////	/**
////	 * Erstellt einen neuen TextParser.
////	 */
////	public TextParser()
////	{
////		
////	}
//	
//	/**
//	 * Erstellt einen neuen TextParser für das angegebene Image.
//	 * @param image
//	 */
//	public TextParser(Image image)
//	{
//		this.image = image;
//		this.font = BitmapFont.getBitmapFont(Graphics.getDefaultFont());
//	}
//	
//	public void stop()
//	{
//		srcText = null;
//		formatText = null;
//		tokens.clear();
//		formats.clear();
//	}
//	
//	public void setTextTokenHandler(TextTokenHandler handler)
//	{
//		this.handler = handler;
//	}
//	
//	/**
//	 * Gibt den Roh-Text zurück.
//	 * @return Roh-Text.
//	 */
//	public String getText()
//	{
//		return srcText;
//	}
//
//	/**
//	 * Gibt den formatierten Text zurück.
//	 * @return formatierter Text.
//	 */
//	public String getFormatText()
//	{
//		return formatText;
//	}
//
//	public void parse(String text)
//	{
//		stop();
//		this.srcText = text;
//		
//		if (text == null) return;
//		
//		boolean key = false;
//		int start = 0;
//		int end = 0;
//		char c;
//		for(int i = 0; i < text.length(); i++)
//		{
//			c = text.charAt(i);
//			if (key == false)
//			{	// erste Sequenz
//				if (c == KeyChar) key = true;
//				continue;
//			}
//			else
//			{	// zweite Sequenz
//				key = false;
//				if (c == KeyChar)
//				{
//					end = i;
//					// Format-Text erweitern (Text vor der Formatierung)
//					tokens.add(text.substring(start, end));
//					// Formatierungs-Objekt erstellen
//					formats.add(new FormatCommand(c, null));
//					start = i + 1;
//					continue;
//				}
//				else
//				{
//					String value = null;
//					if (text.length() > i + 1)
//					{	// look-ahead
//						char h1 = text.charAt(i + 1);
//						if (h1 == '[')
//						{
//							int zu = text.indexOf(']', i + 2);
//							if (zu > i + 2)
//							{
//								value = text.substring(i + 2, zu);
//							}
//							end = i - 1;
//							i = zu;
//						}
//						else
//							end = i - 1;
//					}
//					// kurz-Formatierungen vervollständigen
//					switch(c)
//					{
//						case '.': c = 'w'; value = String.valueOf(FrameTimer.getTargetFPS() / 4); break;
//						case '|': c = 'w'; value = String.valueOf(FrameTimer.getTargetFPS()); break;
//					}
//					
//					// Format-Text erweitern (Text vor der Formatierung)
//					tokens.add(text.substring(start, end));
//					// Formatierungs-Objekt erstellen
//					formats.add(new FormatCommand(c, value));
//					start = i + 1;
//				}
//			}
//		} // end-for
//		// Rest-Text hinzufügen
//		tokens.add(text.substring(start, text.length()));
//		
//		// erstelle das Bild
//		drawX = 0;
//		drawY = 0;
//		index = 0;
//		tokenPos = 0;
//		preScanLine(index);
//		if (waitTime <= 0) nextToken();
//	}
//	
//	private Color getColor(int value)
//	{
//		switch(value)
//		{
//			//TODO: Mehr Farben
//			case 1: return new Color(255, 0, 0);
//			case 2: return new Color(0, 128, 255);
//			case 6: return new Color(255, 255, 0);
//			case 7: return new Color(255, 0, 128);
//			default: return new Color(255, 255, 255);
//		}
//	}
//	
//	private boolean drawNextChar()
//	{
////		System.out.println(Thread.currentThread().getName());
//		String text = tokens.get(index);
//		if (tokenPos < text.length())
//		{
//			String c = String.valueOf(text.charAt(tokenPos++));
//			int size = font.getHeight();
//			image.drawString(drawX, drawY + lineHeight - size, c);
//			drawX += font.getWidth(c);
//			
//			if (waitTime <= 0) return true;
//			waitCount = waitTime;
//		}
//		return false;
//	}
//	
//	private void preScanLine(int index)
//	{
//		lineHeight = font.getHeight();
//		
//		FormatCommand format;
//		while(index < formats.size())
//		{
//			format = formats.get(index++);
//			
//			switch(format.character)
//			{
//				case 's': lineHeight = Math.max(lineHeight, Integer.parseInt(format.value)); break;
//				case 'i':
//					try
//					{
//						Image image = new Image(format.value, ResourceManager.IMG_PICTURE);
//						lineHeight = Math.max(lineHeight, image.getHeight());
//						image.dispose(); // Verschwendung, aber die Ressourcen werden ja gecached.
//					}
//					catch (LoadingException e)
//					{
//						e.printStackTrace();
//					}
//					break;
//				case 'h': insertText(index, "Lisa"); break;
//				case 'e': insertText(index, "Event[" + format.value + "]"); break;
//				case 'v': insertText(index, "Var[" + format.value + "]"); break;
//				case 'n': return;
//			}
//		}
//	}
//	
//	private void insertText(int tokenIndex, String text)
//	{
//		tokens.set(tokenIndex, text + tokens.get(tokenIndex));
//	}
//	
//	private void nextToken()
//	{
//		if (image == null) return;
//		
//		while(drawNextChar());
//		if (tokenPos < tokens.get(index).length()) return;
//		tokenPos = 0;
//		String text = "";
//
//		if (index == formats.size())
//		{
//			index++;
//			return;
//		}
//		
//		FormatCommand format = formats.get(index);
//		Font fnt;
//		switch(format.character)
//		{
//			case 'n': 	drawX = 0; 
//						drawY += lineHeight + 1;
//						preScanLine(index + 1); // nächste Zeie
//						break;
//			case 'c': 	image.setColor(getColor( Integer.parseInt(format.value) )); break;
//			case 's':
//				fnt = image.getFont().deriveFont(Float.parseFloat(format.value));
//				BitmapFont.getBitmapFont(fnt);
//				break;
//			case 't':
//				fnt = image.getFont();
//				BitmapFont.getBitmapFont(new Font(format.value, fnt.getStyle(), fnt.getSize()));
//				break;
//			case 'x': 
//				fnt = image.getFont().deriveFont(Integer.parseInt(format.value));
//				BitmapFont.getBitmapFont(fnt);
//				break;
//			case 'd':	int width = font.getWidth(tokens.get(index));
//						int i = 0;
//						while (width == 0)
//						{
//							i++;
//							if (index - i < 0) break;
//							width = font.getWidth(tokens.get(index - i));
//						}
//						image.setColor(Image.COLOR_TRANSPARENT);
//						image.drawRect(drawX - width, drawY,
//						width, lineHeight, true);
//						drawX -= width;
//						break;
//			case 'i':
//					try
//					{
//						Image subImage = new Image(format.value, ResourceManager.IMG_PICTURE);
//						image.drawImage(drawX, drawY, subImage);
//						drawX += subImage.getWidth();
//					}
//					catch (LoadingException e)
//					{
//						e.printStackTrace();
//					}
//					break;
//			case 'w':	waitCount = Integer.parseInt(format.value); break;
//			// bereits entwertete Schlüssel
//			case 'h': 
//			case 'e':
//			case 'v': 
//			case '$': break;
//			
//			default:
//				System.out.println("Unbekannter Formatschlüssel: " + format.character);
//				if (handler != null)
//				{	// übergebe benötigte Parameter
//					// workaround wegen fehlender Referenz-Übergabe.
//					format.x = drawX;
//					format.y = drawY;
//					format.text = text;
//					handler.nextToken(image, format);
//					drawX = format.x;
//					drawY = format.y;
//					text = format.text;
//				}
//		}
//		index++;
//		if (waitCount <= 0 && index < tokens.size()) nextToken();
//	}
//	
//	public void update()
//	{
//		if (srcText == null) return;
//		
//		if (waitCount > 0)
//		{
//			waitCount--;
//			if (waitCount > 0) return;
//		}
//		
//		if (index < tokens.size())
//		{
//			nextToken();
//		}
//	}
//	
////	private enum FormatType
////	{
////		/** Fügt einen Zeilenumbruch ein. */
////		RETURN,
////		/** Ändert die Farbe des Textes. */
////		COLOR,
////		/** Ändert die Schriftart des Textes. */
////		FONT,
////		/** Ändert die Schriftgröße des Textes. */
////		SIZE,
////		/** Ändert den Schrift-Style des Textes. (Normal, Bold, Krusiv) */
////		STYLE,
////		/** Zeigt eine Variable an. */
////		VARIABLE,
////		/** Fügt den Namen eines Helden an die Stelle ein. */
////		HERO_NAME,
////		/** Fügt den Namen eines Events an die Stelle ein. */
////		EVENT_NAME,
////		/** Fügt ein Image an die Stelle ein. */
////		IMAGE,
////		/** Wartet eine Zeit in Frames bis weiter gezeichnet wird. (Erfordert den Aufruf von update() in jedem frame.) */
////		WAIT,
////		/** Eine unbekannte Formatierung. (Für Erweiterungen bestimmt) */
////		OTHER,
////	}
//	
////	public static void main(String[] args)
////	{
////		TextParser parser = new TextParser();
////		parser.parse("<Start>\\c[7]\\h[0]\\c[0] Oh,\\. ja!\\|\\nFester,\\. \\s[24]aah! \\i[herz.png]<Ende>");
////		System.out.println(parser.getFormatText());
////	}
}
