package cuina.plugin;

import java.io.Serializable;

/**
 * Das Plugin-Interface dient als Kennzeichnung für ein dynamisch einzubindendes Spielobjekt aus einem Plugin.
 * Pluginklassen können über eine Kontext-Annotation einem Spielkontext zugewieden werden.
 * @author TheWhiteShadow
 */
public interface Plugin extends Serializable {}
