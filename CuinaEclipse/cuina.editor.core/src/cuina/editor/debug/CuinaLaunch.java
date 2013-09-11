package cuina.editor.debug;


public class CuinaLaunch
{
	public static final String CONFIGURATION_TYPE = "cuina.editor.debug.CuinaProjectApplication";
	public static final String CONFIGURATION_NAME = "Cuina_Project";

	public static final String PROJECT_NAME     = "cuina.project.name";
	public static final String ENGINE_PATH      = "cuina.engine.path";
	public static final String ENGINE_SOURCE    = "cuina.engine.src";
	public static final String PLUGIN_PATH      = "cuina.plugin.path";
	public static final String PLUGIN_LIST  	= "cuina.plugin.entries";
	public static final String PLUGIN_MAGIC     = "cuina.plugin.magicNumber";

	public static String getDefaultVMArgs()
	{
		return "-Xms64m -Xmx512m";
	}

	public static String getDefaultArgs()
	{
		return "debug logLevel=2";
	}
}
