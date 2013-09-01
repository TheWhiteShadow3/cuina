package cuina.editor.script.dialog;

import cuina.editor.script.internal.dialog.CommandDialog;
import cuina.editor.script.internal.dialog.ScriptDialogContext;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.ast.Node;

import org.eclipse.swt.widgets.Composite;

/**
 * Eine Seite im {@link CommandDialog}.
 * Seiten ermöglichen es Argumente zur Skript-Anweisung hinzuzufügen.
 * @author TheWhiteShadow
 */
public interface CommandTab
{
	/**
	 * Gibt den Namen der Seite zurück, welcher im Reiterfeld angezeigt wird.
	 * @return Name der Seite.
	 */
	public String getName();
	
	/**
	 * Setzt den Argument-Knoten und die Definition zum Argument.
	 * @param node Argument-Knoten.
	 * @param parameter Definition.
	 */
	public void setNode(Node node, ValueDefinition parameter);
	
	/**
	 * Gibt den Argument-Knoten zurück.
	 * Der Knoten darf nicht bereits in einem Syntax-Baum eingebunden sein.
	 * @return Argument-Knoten.
	 */
	public Node getNode();
	
	/**
	 * Initialisiert die Seite. Diese Methode wird vor allen anderen aufgerufen.
	 * @param context Kontext, des Dialogs.
	 */
	public void init(ScriptDialogContext context);
	
	/**
	 * Erstellt die Grafische Oberfläche der Seite.
	 * @param parent
	 */
	public void createControl(Composite parent);
	
	/**
	 * Aktiviert oder Deaktiviert die Seiten-Objerfläche.
	 * <i>(Optionale Funktionalität)</i><br>
	 * Per Default sind alle Seiten aktiviert.
	 * @param value <code>true</code>, wenn die Seite aktiviert wird, ansonsten <code>false</code>.
	 */
	public void setEnabled(boolean value);
}
