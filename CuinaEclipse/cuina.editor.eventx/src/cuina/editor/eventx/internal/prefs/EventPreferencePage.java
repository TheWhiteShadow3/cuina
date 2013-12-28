package cuina.editor.eventx.internal.prefs;

import cuina.editor.eventx.internal.EventPlugin;
import cuina.editor.ui.prefs.ColorListEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Einstellungseite für den Skript-Editor.
 */
public class EventPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public EventPreferencePage()
	{
		super(GRID);
		setPreferenceStore(EventPlugin.getPlugin().getPreferenceStore());
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
				EventPreferences.CMDLINE_FONT, "Schrift:", getFieldEditorParent()) );
		addField(new IntegerFieldEditor(
				EventPreferences.CMDLINE_INDENT_WIDTH, "Einrückungstiefe", getFieldEditorParent()) );
		addField(new ColorListEditor(new String[][]
		{
				{EventPreferences.CMDLINE_BACKGROUND_COLOR, 	"Hintergrund Farbe"},
				{EventPreferences.CMDLINE_COLOR_DEFAULT, 		"Default Farbe"},
				{EventPreferences.CMDLINE_COLOR_COMMENT, 		"Kommentare"},
				{EventPreferences.CMDLINE_COLOR_CONTROL, 		"Kontrollstrukturen"},
				{EventPreferences.CMDLINE_COLOR_ASSIGNMENT, 	"Zuweisungen"},
				{EventPreferences.CMDLINE_COLOR_FUNCTION, 		"Funktionen"},
		}, "Farb-Schema:", getFieldEditorParent()) );
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
