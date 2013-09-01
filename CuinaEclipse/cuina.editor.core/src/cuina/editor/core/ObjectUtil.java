package cuina.editor.core;

public class ObjectUtil
{
	private static final com.rits.cloning.Cloner cloner = new com.rits.cloning.Cloner();
	
	private ObjectUtil() {}
	
	/**
	 * Erstellt eine tiefe Kopie des übergebenen Objekts.
	 * @param original Originales Objekt.
	 * @return Kopie des Objekts.
	 */
	public static <E> E clone(E original)
	{
		return cloner.deepClone(original);
	}
}
