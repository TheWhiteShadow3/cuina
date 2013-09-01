package cuina.editor.core.internal;


public class CuinaEngine
{
	/** Name der System-Variable zum Cuina-Engine Homeverzeichnis */
	public static final String CUINA_SYSTEM_VARIABLE = "CUINA_HOME"; //$NON-NLS-1$
	
    public static String getEnginePath()
    {
    	return System.getenv(CUINA_SYSTEM_VARIABLE);
    }
    
    //TODO: Preference-Eintrag machen.
    public static String getPluginPath()
    {
    	return getEnginePath() + "/plugins";
    }
}
