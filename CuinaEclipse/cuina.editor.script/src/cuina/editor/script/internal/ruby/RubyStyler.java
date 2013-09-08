package cuina.editor.script.internal.ruby;

import cuina.editor.core.util.Ini;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.CommentNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.ModuleNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;
import cuina.editor.script.ruby.ast.StrNode;
import cuina.editor.script.ruby.ast.VarNode;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Highlightet Textabschnitte eines übergebenen {@link StyledDocument} anhand eines zuvor eingelesenen Ruby-Codes.
 * Die Styles lassen sich über statische Felder einstellen.
 * @author TheWhiteShadow
 */
public class RubyStyler
{
	private static final String INI_SECTION = "Ruby-Highlighting";
	
	// Definiere schöne ästhetische Default-Styles^^
	public static Style styleNormal 	= new Style(Color.BLACK, false, false);
	public static Style styleKeyword 	= new Style(Color.MAGENTA, false, true);
	public static Style styleSystemVar 	= new Style(Color.MAGENTA, false, true);
	public static Style styleSymbol 	= new Style(Color.MAGENTA, false, true);
	public static Style styleClass		= new Style(new Color(0, 0, 255), false, true);
	public static Style styleModule		= new Style(new Color(0, 0, 255), false, true);
	public static Style styleMethode 	= new Style(new Color(128, 0, 128), false, true);
	public static Style styleComment 	= new Style(new Color(128, 128, 128), false, false);
	public static Style styleString 	= new Style(new Color(0, 128, 255), true, false);
	public static Style styleGlobalVar 	= new Style(new Color(255, 128, 0), false, false);
	public static Style styleClassVar 	= new Style(new Color(0, 0, 255), false, false);
	public static Style styleLocalVar 	= new Style(new Color(128, 0, 0), false, false);
	public static Style styleConstant 	= new Style(new Color(0, 0, 128), true, false);
	public static Style styleNumeric 	= new Style(Color.RED, false, false);
	
	private StyledDocument doc;
	private SimpleAttributeSet att = new SimpleAttributeSet();
	
	public void colorize(StyledDocument doc, RubySource source, RootNode rootNode)
	{
		this.doc = doc;

		if (rootNode != null)
			iterateChilds(rootNode);
		
		for(Token token : source.getTokens())
		{
			if ( RubyIdentifier.isKeyword(token.getValue()) )
			{
				setStyle(styleKeyword, token);
			}
		}
		
		
		
//		Token token;
//		Style style;
//		for (int i = 0; i < tokens.length; i++)
//		{
//			token = tokens[i];
//			if (token.getGroup() == Tokenizer.IDENTIFIER)
//				style = getidentifierStyle(token, i);
//			else
//				style = styleNormal;
//
//			if (style == null) continue;
//			setStyle(style);
//			doc.setCharacterAttributes(token.getPos(), token.getValue().length(), att, true);
//		}
	}
	
	private void iterateChilds(Node root)
	{
		for(Node child : root.getChildren())
		{
			if (child instanceof VarNode)
			{
				switch(((VarNode)child).getScope())
				{
					case Node.LOCAL_SCOPE: 	setStyle(styleLocalVar, child); break;
					case Node.INST_SCOPE: 	setStyle(styleClassVar, child); break;
					case Node.CLASS_SCOPE: 	setStyle(styleClassVar, child); break;
					case Node.GLOBAL_SCOPE: 	setStyle(styleGlobalVar, child); break;
				}
			}
			else if (child instanceof CommentNode)
			{
				setStyle(styleComment, child);
			}
			else if (child instanceof ConstNode)
			{
				setStyle(styleConstant, child);
			}
			else if (child instanceof ClassNode)
			{
				setStyle(styleClass, child);
			}
			else if (child instanceof ModuleNode)
			{
				setStyle(styleClass, child);
			}
			else if (child instanceof CallNode)
			{
				setStyle(styleMethode, child);
			}
			else if (child instanceof StrNode)
			{
				setStyle(styleString, child);
			}
			else if (child instanceof FixNumNode)
			{
				setStyle(styleNumeric, child);
			}
			else if (child instanceof DefNode)
			{
				setStyle(styleMethode, child);
			}
//			else setStyle(styleNormal, child);
			
			iterateChilds(child);
		}
	}
	
	private void setStyle(Style style, Node node)
	{
		setStyle(style, node.getPosition().getToken());
	}
	
	private void setStyle(Style style, Token token)
	{
		StyleConstants.setForeground(att, style.getColor());
		StyleConstants.setItalic(att, style.isItalic());
		StyleConstants.setBold(att, style.isBolt());
		doc.setCharacterAttributes(token.getPos(), token.getValue().length(), att, true);
	}
	
	/**
	 * Lese die zu benutzenden Styles aus einer Ini-Datei.
	 * Wenn eine Styleinformation nicht gelesen werden kann, wird die Default-Einstellung benutzt.
	 * @param ini
	 */
	public static void readStyles(Ini ini)
	{
		styleNormal 	= readStyle(ini, "Normal", 		styleNormal);
		styleKeyword 	= readStyle(ini, "Keyword", 	styleKeyword);
		styleSystemVar 	= readStyle(ini, "SystemVar", 	styleSystemVar);
		styleSymbol 	= readStyle(ini, "Symbol", 		styleSymbol);
		styleClass 		= readStyle(ini, "Class", 		styleClass);
		styleModule 	= readStyle(ini, "Module", 		styleModule);
		styleMethode 	= readStyle(ini, "Methode", 	styleMethode);
		styleComment 	= readStyle(ini, "Comment", 	styleComment);
		styleString 	= readStyle(ini, "String", 		styleString);
		styleGlobalVar 	= readStyle(ini, "GlobalVar", 	styleGlobalVar);
		styleClassVar 	= readStyle(ini, "ClassVar", 	styleClassVar);
		styleLocalVar 	= readStyle(ini, "LocalVar", 	styleLocalVar);
		styleConstant 	= readStyle(ini, "Constant", 	styleConstant);
		styleNumeric 	= readStyle(ini, "Numeric", 	styleNumeric);
	}
	
	private static Style readStyle(Ini ini, String section, Style defaultStyle)
	{
		try
		{
			return Style.createStyle(ini.get(INI_SECTION, section));
		}
		catch(Exception e)
		{
			return defaultStyle;
		}
	}
	
	/**
	 * Schreibe die aktuellen Styles in einer Ini-Datei.
	 * @param ini
	 */
	public static void writeStyles(Ini ini)
	{
		ini.set(INI_SECTION, "#", null);
		ini.set(INI_SECTION, "Normal", 		styleNormal.makeString());
		ini.set(INI_SECTION, "Keyword", 	styleKeyword.makeString());
		ini.set(INI_SECTION, "SystemVar", 	styleSystemVar.makeString());
		ini.set(INI_SECTION, "Symbol", 		styleSymbol.makeString());
		ini.set(INI_SECTION, "Class", 		styleClass.makeString());
		ini.set(INI_SECTION, "Module", 		styleModule.makeString());
		ini.set(INI_SECTION, "Methode", 	styleMethode.makeString());
		ini.set(INI_SECTION, "Comment", 	styleComment.makeString());
		ini.set(INI_SECTION, "String", 		styleString.makeString());
		ini.set(INI_SECTION, "GlobalVar", 	styleGlobalVar.makeString());
		ini.set(INI_SECTION, "ClassVar", 	styleClassVar.makeString());
		ini.set(INI_SECTION, "LocalVar", 	styleLocalVar.makeString());
		ini.set(INI_SECTION, "Constant", 	styleConstant.makeString());
		ini.set(INI_SECTION, "Numeric", 	styleNumeric.makeString());
	}
	
	public static class Style
	{
		private Color color;
		private boolean italic;
		private boolean bolt;

		private Style(Color color, boolean italic, boolean bolt)
		{
			this.color = color;
			this.italic = italic;
			this.bolt = bolt;
		}
		
		private static Style createStyle(String str)
		{
			String[] atts = str.split(",");
			
			Color col = new Color(Integer.parseInt(atts[0]), Integer.parseInt(atts[1]), Integer.parseInt(atts[2]));
			return new Style(col, Boolean.parseBoolean(atts[3]), Boolean.parseBoolean(atts[4]));
		}
		
		private String makeString()
		{
			return color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + italic + "," + bolt;
		}

		public Color getColor()
		{
			return color;
		}

		public boolean isItalic()
		{
			return italic;
		}

		public boolean isBolt()
		{
			return bolt;
		}
	}
}
