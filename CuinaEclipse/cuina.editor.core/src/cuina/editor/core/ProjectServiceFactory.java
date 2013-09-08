package cuina.editor.core;

/**
 * Ermöglicht Services das Anbieten von Projekt bezogenen Objekten.
 * Beispielsweise eine Datenbank.
 * <p>
 * Der Service kann später über
 * <pre>Service service = project.getService(Service.class);</pre>
 * angefragt werden.
 * </p>
 * @author TheWhiteShadow
 */
public interface ProjectServiceFactory
{
	/**
	 * Erstellt ein Service-Objekt zum engegebenen Projekt.
	 * @param api Die Service-API.
	 * @param cuinaProject Das Projekt, zu dem das Objekt gehört.
	 * @return Ein Service-Objekt.
	 */
	public Object create(Class api, CuinaProject cuinaProject);
}
