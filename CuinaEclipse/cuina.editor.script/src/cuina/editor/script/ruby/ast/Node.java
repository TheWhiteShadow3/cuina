package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public interface Node
{
	public static final int LOCAL_SCOPE 	= 0;
	public static final int INST_SCOPE 		= 1;
	public static final int CLASS_SCOPE 	= 2;
	public static final int GLOBAL_SCOPE 	= 3;
	public static final int UNKNOWN_SCOPE 	= 4;
	
	public SourceData getPosition();

	/**
	 * Gibt den Vorrangigen Knoten aus dem Syntax-Baum zurück.
	 * @return Vorrangigen Knoten im Baum.
	 */
	public Node getParent();

	public String getNodeName();

	@Override
	public String toString();

	/**
	 * Gibt eine Liste der vorhandenen Kind-Knoten zurück.
	 * <p>
	 * Kindklassen müssen diese Methode überschreiben.
	 * Die Implementierung darf nie <code>null</code> zurück geben.
	 * </p>
	 * @return Liste der vorhandenen Kind-Knoten.
	 */
	public List<Node> getChilds();

	/**
	 * Gibt den Typ des Knoten zurück.
	 * @return Typ des Knoten.
	 */
	public NodeType getNodeType();

}
