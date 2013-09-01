package cuina.editor.script.library;

import java.util.ArrayList;
import java.util.HashMap;

public interface IScriptLibrary
{
	/**
	 * Gibt die Definition zu einer Klassen-Variable zurück.
	 * @param classID ID der Klasse.
	 * @param id ID der Variablen.
	 * @return Die Definition der Klassenvariable oder <code>null</code>, falls nicht vorhanden.
	 */
	public ValueDefinition getClassVariable(String classID, String id);

	/**
	 * Gibt eine Liste aller Funktions-Definitionen zurück, die den angegebenen Rückgabewert besitzen.
	 * @param returnType Rückgabewert.
	 * @return Liste der Funktions-Definitionen mit angegebenem Rückgabewert.
	 */
	public ArrayList<FunctionDefinition> getFunctions(String returnType);

	/**
	 * Gibt eine Funktions-Definition zurück.
	 * @param classID ID der Klasse.
	 * @param functionID ID der Funktion.
	 * @return Die Definition der Funktion.
	 */
	public FunctionDefinition getFunction(String classID, String functionID);

	/**
	 * Gibt eine Klassen-Definition zurück.
	 * @param classID ID der Klasse.
	 * @return Die Definition der Klasse.
	 */
	public ClassDefinition getClassDefinition(String classID);

	/**
	 * Gibt eine Liste der aller Klassen-Definitionen zurück.
	 * @return Liste der Klassen-Definitionen.
	 */
	public HashMap<String, ClassDefinition> getClassDefinitions();
}
