package cuina.graphics;

import java.util.List;

public interface GraphicContainer
{
	/**
	 * Fügt dem Kontainer ein neues Grafikelement hinzu.
	 * Grafiken können nur einmal im Kontainer vorhanden sein.
	 * @param graphic die Grafik.
	 * @return <code>true</code>, wenn die Graphic dem Kontainer hinzugefügt wurde,
	 * andernfalls <code>false</code>.
	 */
	public boolean addGraphic(Graphic graphic);
	
	/**
	 * Entfernt aus dem Kontainer das angegebene Grafikelement.
	 * @param graphic Graphic
	 */
	public void removeGraphic(Graphic graphic);
	
	/**
	 * Setzt den Shader für den Kontainer.
	 * @param shader
	 */
	public void setShader(Shader shader);

	/**
	 * Gibt eine Liste aller beinhaltenden Grafikelemente zurück.
	 * @return Liste der beinhaltenden Grafikelemente.
	 */
	public List<Graphic> toList();

	public void setContainer(GraphicContainer container);
	public GraphicContainer getContainer();
	
	public String getName();

	public void clear();
}
