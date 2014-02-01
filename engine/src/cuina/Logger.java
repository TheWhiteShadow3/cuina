package cuina;
 
import cuina.graphics.Graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;

/**
 * Behandelt Log-Ereignisse und schreibt die in den standard Output-Stream oder in eine Logdatei, wenn vorhanden.
 * Log-Ereignisse können über eine Priorität gefiltert werden um die Ausgabemenge zu reduzieren.
 * @author TheWhiteShadow
 */
public final class Logger
{
    /** Detailierte Debug-Ausgaben. */
    public static final int DEBUG_2     = 1;
    /** Debug-Ausgaben. */
    public static final int DEBUG       = 2;
    /** Informative Ausgaben. */
    public static final int INFO        = 3;
    /** Warnungen die fehlerhaftes Verhalten zeigen, aber nicht zum Absturz führen können. */
    public static final int WARNING     = 4;
    /** Fehler die fehlerhaftes Verhalten zeigen und evtl. zum Absturz führen. */
    public static final int ERROR       = 5;
    /** Schwere Fehler die einen Absturz der Engine zur Folge haben. Beim Auftreten wird die Engine sofort beendet. */
    public static final int CRIT_ERROR  = 6;

	private static File logFile;
	private static PrintStream console;
	private static PrintStream fileStream;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.y HH:mm:ss,SSS");

	/** Detail-Level der Ausgaben. */
	public static int logLevel = WARNING;

	static
	{
		console = System.out;
		try
		{
			setLogFile(null);
		}
		catch (FileNotFoundException e)
		{
			/* null wird immer gefunden^^ */
		}
	}

	/**
	 * Legt das Log-File fest.
	 * 
	 * @param file
	 *            File oder null, um File-Logging zu deaktivieren.
	 * @throws FileNotFoundException
	 *             Wenn die Datei nicht schreibend geöffnet oder angelegt werden konnte.
	 */
	public static void setLogFile(File file) throws FileNotFoundException
	{
		if (logFile == null && file == null) return;
		if (file != null && file.equals(logFile)) return;
		
		PrintStream newStream;
    	if (file == null)
    	{
    		newStream = null;
    		System.setErr(console);
    	}
    	else
    	{
	        newStream = new PrintStream(new FileOutputStream(file, true));
	        System.setErr(newStream);
    	}
    	if (fileStream != null) fileStream.close();
    	fileStream = newStream;
    	logFile = file;
    }

	/**
	 * Loggt die angegebene Fehlermeldung. Die Meldung wird in die Logdatei
	 * oder, wenn nicht vorhanden, in den standard Output-Stream geschrieben.
	 * Die Einträge haben dabei folgendes Format:
	 * 
	 * <pre>
	 * Level    Datum    Uhrzeit Modul       Fehlermeldung
	 * ERROR:   22.01.13 15:56  (Cuina.Game) FileNotFoundException: C:\Cuina\Engine\cuina.cfg
	 * </pre>
	 * 
	 * @param module
	 *            Klasse welche den Fehler meldet.
	 * @param level
	 *            Log-Level.
	 * @param error
	 *            Fehler-Objekt.
	 */
	public static void log(Class module, int level, Throwable error)
	{
		if (logLevel > level) return;

		String message = error.getClass().getSimpleName() + ": " + error.getMessage();
		if (fileStream != null) log(module, level, message, fileStream);
		if (fileStream == null || Game.isDebug()) log(module, level, message, console);

		if (fileStream != null) error.printStackTrace(fileStream);
		if (fileStream == null || Game.isDebug()) error.printStackTrace(console);

		if (level >= ERROR && Graphics.isInitialized())
		{
			showErrorDialog(level, message);
		}
	}

	/**
	 * Loggt die angegebene Nachicht. Die Meldung wird in die Logdatei oder,
	 * wenn nicht vorhanden, in den standard Output-Stream geschrieben. Die
	 * Einträge haben dabei folgendes Format:
	 * 
	 * <pre>
	 * Level    Datum    Uhrzeit Modul       Nachicht
	 * INFO:    22.01.13 15:56,135  (Cuina.Game) starte Cuina-Engine
	 * </pre>
	 * 
	 * @param module
	 *            Klasse welche den Fehler meldet.
	 * @param level
	 *            Log-Level.
	 * @param message
	 *            die zu loggende Nachicht.
	 */
	public static void log(Class module, int level, String message)
	{
		if (logLevel > level) return;

		if (fileStream != null) log(module, level, message, fileStream);
		if (fileStream == null || Game.isDebug()) log(module, level, message, console);

		if (level >= ERROR)
		{
			showErrorDialog(level, message);
		}
	}
	
	private static void showErrorDialog(int level, String message)
	{
		if (level == CRIT_ERROR)
		{
			JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			JDialog dialog = pane.createDialog(Display.getParent(), "Error");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
			criticalAbort();
		}
		else
		{
			JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
			JDialog dialog = pane.createDialog(Display.getParent(), "Error");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
			Object result = pane.getValue();
			
			if (result == null || ((Integer) result).intValue() == JOptionPane.CANCEL_OPTION) criticalAbort();
		}
	}

	private static void log(Class module, int level, String message, PrintStream stream)
	{
		switch (level)
		{
			case DEBUG_2:
			case DEBUG:
				stream.append("DEBUG:   ");
				break;
			case INFO:
				stream.append("INFO:    ");
				break;
			case WARNING:
				stream.append("WARNING: ");
				break;
			case ERROR:
			case CRIT_ERROR:
				stream.append("ERROR:   ");
				break;
		}
		stream.append(dateFormat.format(new Date()));
		stream.append(" (").append(module.getName()).append(") ");
		stream.println(message.replace("\n", "\n\t"));
		stream.flush();
	}

	private static void criticalAbort()
	{
		try { Game.getInstance().dispose(); }
		catch (Throwable t) { System.exit(2); }
		
		System.exit(1);
	}

	/**
	 * Schließt den File-Stream. Mehrfache Aufrufe haben keine Wirkung.
	 */
	public static void close()
	{
		if (fileStream != null)
		{
			log(Logger.class, INFO, "Logger closed.");
			fileStream.close();
			fileStream = null;
		}
	}
}