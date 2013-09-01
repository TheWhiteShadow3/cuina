package cuina.editor.script.internal.prefs;

import cuina.editor.script.Scripts;
import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.CaseNode;
import cuina.editor.script.ruby.ast.IfNode;
import cuina.editor.script.ruby.ast.WhileNode;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Einstellungseite für den Skript-Editor.
 */
public class ScriptPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public ScriptPreferencePage()
	{
		super(GRID);
		setPreferenceStore(Scripts.getPlugin().getPreferenceStore());
		setDescription("Skript Editor Einstellungen");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors()
	{
		addField(new FontFieldEditor(
				ScriptPreferences.CMDLINE_FONT, "Schrift:", getFieldEditorParent()) );
		addField(new IntegerFieldEditor(
				ScriptPreferences.CMDLINE_INDENT_WIDTH, "Einrückungstiefe", getFieldEditorParent()) );
		addField(new ColorListEditor(new String[][]
		{
				{ScriptPreferences.CMDLINE_BACKGROUND_COLOR, 	"Hintergrund Farbe"},
				{ScriptPreferences.CMDLINE_COLOR_DEFAULT, 		"Default Farbe"},
				{ScriptPreferences.CMDLINE_COLOR_COMMENT, 		"Kommentare"},
				{ScriptPreferences.CMDLINE_COLOR_CONTROL, 		"Kontrollstrukturen"},
				{ScriptPreferences.CMDLINE_COLOR_ASSIGNMENT, 	"Zuweisungen"},
				{ScriptPreferences.CMDLINE_COLOR_FUNCTION, 		"Funktionen"},
		}, "Farb-Schema:", getFieldEditorParent()) );
		
		addField(new ComboFieldEditor(ScriptPreferences.CMDLINE_DEFAULT_NODE, "Standard Dialog", new String[][]
		{
			{ScriptUtil.getTitleForNode(CallNode.class), 	"CallNode"},
			{ScriptUtil.getTitleForNode(AsgNode.class), 	"AsgNode"},
			{ScriptUtil.getTitleForNode(IfNode.class), 		"IfNode"},
			{ScriptUtil.getTitleForNode(WhileNode.class), 	"WhileNode"},
			{ScriptUtil.getTitleForNode(CaseNode.class), 	"CaseNode"},
		}, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {}
}
