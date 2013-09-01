package cuina.editor.script.internal.prefs;

import cuina.editor.script.Scripts;
import cuina.editor.script.ruby.ast.CallNode;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Constant definitions for plug-in preferences
 */
public class ScriptPreferences extends AbstractPreferenceInitializer
{
	public static final String CMDLINE_FONT 				= "cmdLineFont";
	public static final String CMDLINE_INDENT_WIDTH 		= "cmdLineIndentWidth";
	
	public static final String CMDLINE_BACKGROUND_COLOR 	= "cmdLineBackgroundColor";
	public static final String CMDLINE_COLOR_DEFAULT 		= "cmdLineColorDefault";
	public static final String CMDLINE_COLOR_CONTROL 		= "cmdLineColorControl";
	public static final String CMDLINE_COLOR_ASSIGNMENT 	= "cmdLineColorAssignment";
	public static final String CMDLINE_COLOR_FUNCTION 		= "cmdLineColorFunction";
	public static final String CMDLINE_COLOR_COMMENT 		= "cmdLineColorComment";
	
	public static final String CMDLINE_DEFAULT_NODE 		= "cmdLineDefaultNode";
	
	private static IPreferenceStore getStore()
	{
		return Scripts.getPlugin().getPreferenceStore();
	}
	
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = getStore();
		store.setDefault(CMDLINE_FONT, "Courier New");
		store.setDefault(CMDLINE_INDENT_WIDTH, 4);
		store.setDefault(CMDLINE_DEFAULT_NODE, CallNode.class.getSimpleName());
		PreferenceConverter.setDefault(store, CMDLINE_BACKGROUND_COLOR, new RGB(255, 255, 255));
		PreferenceConverter.setDefault(store, CMDLINE_COLOR_DEFAULT, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, CMDLINE_COLOR_CONTROL, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, CMDLINE_COLOR_ASSIGNMENT, new RGB(0, 128, 0));
		PreferenceConverter.setDefault(store, CMDLINE_COLOR_FUNCTION, new RGB(128, 0, 128));
		PreferenceConverter.setDefault(store, CMDLINE_COLOR_COMMENT, new RGB(120, 120, 120));
	}
	
	public static Color getColor(String name)
	{
		return new Color(Display.getCurrent(), PreferenceConverter.getColor(getStore(), name));
	}
	
	public static FontData getFontData(String name)
	{
		return PreferenceConverter.getFontData(getStore(), name);
	}

	public static int getIndentWidth()
	{
		return getStore().getInt(CMDLINE_INDENT_WIDTH);
	}
	
	public static String getDefaultNode()
	{
		return getStore().getString(CMDLINE_DEFAULT_NODE);
	}
}
