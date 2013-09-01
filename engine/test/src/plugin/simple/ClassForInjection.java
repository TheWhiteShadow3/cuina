package plugin.simple;

import cuina.plugin.ForGlobal;
import cuina.plugin.ForScene;
import cuina.plugin.ForSession;
import cuina.plugin.Plugin;

@SuppressWarnings("serial")
@ForGlobal(name="junit_global")
@ForSession(name="junit_session")
@ForScene(name="junit_scene", scenes= {"junit"})
public class ClassForInjection implements Plugin
{
}
