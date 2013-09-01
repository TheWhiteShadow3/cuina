package cuina.editor.ui.selection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * Die Klasse verwaltet Auswahl-Bereiche auf der Zeichenfläche
 * und bietet Methoden um diese zu manipulieren.
 * Es können beliebig viele Auswahlen gleichzeitig existieren,
 * mindestens aber eine um <code>null</code>-Referenzen vorzubeugen.
 * <p>
 * Es können Listener hinzugefügt werden, die Auf Änderungen der Auswahl reagieren,
 * oder diese bei einem Ereignis selbst ändern.
 * </p>
 * <p>
 * Es ist möglich einen Auswahl-Modus zu setzen, der selbstständig auf Ereignisse reagiert.
 * </p>
 * Steuert ein Set von Auswahl-Methoden. Der SelectionHandler besitzt Slots für
 * 4 einzelne Moden, wovon eine als Default benutzt wird und die 3 Anderen den
 * Maustasten zugeordnet werden.
 * <p>
 * Es kann eine Domäne definiert werden, in der der SelectionManager agieren soll.
 * Startet die Domäne nicht bei der Koordinate 0/0, wird ein Offset definiert was
 * die Auswahl und Ereignisse relativ dazu verschiebt.
 * </p>
 * @author TheWhiteShadow
 * @see SelectionMode
 */
public class SelectionManager implements MouseListener, MouseMoveListener, MouseTrackListener, FocusListener
{
	public static final int DEFAULT = 0;
	public static final int MOUSE_LEFT = 1;
	public static final int MOUSE_MIDDLE = 2;
	public static final int MOUSE_RIGHT = 3;

	private List<Selection> selections;
	private int currentIndex;
	private SelectionMode mode;
	private Rectangle domain;
	private Control control;
	private MouseEvent mouseEvent;
	private int button;
	
	private boolean active;
	private boolean disableOutside = true;
	private final ArrayList<SelectionListener> listeners = new ArrayList<SelectionListener>();

	/**
	 * Erstellt einen neuen SelectionManager.
	 * <p>
	 * Fügt dem Control die benötigten Mouse-Listener hinzu.
	 * </p>
	 * 
	 * @param control
	 *            Widget-Element, in dem die Auswahl angezeigt werden soll.
	 * @param domain
	 *            Region, in der der SelectionManager agieren soll.
	 */
	public SelectionManager(Control control, Rectangle domain)
	{
		this.control = control;
		this.domain = new Rectangle(domain.x, domain.y, domain.width, domain.height);
		initSelectionList();
		
		control.addMouseListener(this);
		control.addMouseMoveListener(this);
		control.addMouseTrackListener(this);
		control.addFocusListener(this);
	}
	
	private void initSelectionList()
	{
		this.selections = new ArrayList<Selection>();
//		selections.add(new Selection());
		currentIndex = -1;
	}

	public void clearSelections()
	{
//		System.out.println("[SelectionManager] clearSelections");
		for (Selection s : selections) s.dispose();
		initSelectionList();
		update();
	}
	
	public void setSelectionMode(SelectionMode mode, boolean activate)
	{
		if (this.mode != null)
		{
			if (mouseEvent != null)
				this.mode.deactivate(this, mouseEvent.x, mouseEvent.y);
			else
				this.mode.deactivate(this, 0, 0);
		}
		if (activate)
		{
			if (mouseEvent != null)
				mode.activate(this, mouseEvent.x, mouseEvent.y);
			else
				mode.activate(this, 0, 0);
		}
		this.mode = mode;
		
		System.out.println("[SelectionManager] setSelectionMode: " + mode);
	}
	
	public void clearSeletionMode()
	{
		setSelectionMode(null, false);
	}

	public SelectionMode getSelectionMode()
	{
		return mode;
	}

	public Selection getSelection()
	{
		if (currentIndex == -1) return addSelection();
		
		return getSelection(currentIndex);
	}
	
	public Selection getSelection(int index)
	{
		return selections.get(index);
	}

	public Selection findSelection(int x, int y)
	{
		for (int i = selections.size() - 1; i >= 0; i--)
		{
			Selection sel = selections.get(i);
			if (sel.contains(x, y))
			{
				currentIndex = i;
				return sel;
			}
		}
		return null;
	}
	
	public List<Selection> getSelectionList()
	{
		return selections;
	}
	
	public int getSelectionCount()
	{
		return selections.size();
	}
	
	public Selection addSelection(int x, int y, int width, int height)
	{
		Selection s = new Selection(x, y, width, height);
		selections.add(s);
		currentIndex = selections.size() - 1;
//		System.out.println("[SelectionManager] addSelection count=" + selections.size());
		return s;
	}

	public Selection addSelection(Rectangle rect)
	{
		return addSelection(rect.x, rect.y, rect.width, rect.height);
	}
	
	public Selection addSelection()
	{
		return addSelection(0, 0, 0, 0);
	}
	
	public Control getControl()
	{
		return control;
	}
	
	public boolean isDisableOutside()
	{
		return disableOutside;
	}

	public void setDisableOutside(boolean disableOutside)
	{
		this.disableOutside = disableOutside;
	}

	/**
	 * Setzt die Region in der der SelectionManager Events verarbeitet.
	 * @param rect Region
	 */
	public void setSelectionArea(Rectangle rect)
	{
		domain.x = rect.x;
		domain.y = rect.y;
		domain.width = rect.width;
		domain.height = rect.height;
	}
	
	/**
	 * Setzt die Region in der der SelectionManager Events verarbeitet.
	 * @param x X
	 * @param y Y
	 * @param width Breite
	 * @param height Höhe
	 */
	public void setSelectionArea(int x, int y, int width, int height)
	{
		domain.x = x;
		domain.y = y;
		domain.width = width;
		domain.height = height;
	}

	@Override
	public void mouseEnter(MouseEvent e)
	{
		translateEventPosition(e);
		if (insideDomain(e.x, e.y)) enterArea(e);
	}

	@Override
	public void mouseExit(MouseEvent e)
	{
		translateEventPosition(e);
		leaveArea(e);
	}

	@Override
	public void mouseHover(MouseEvent e)
	{}

	@Override
	public void mouseMove(MouseEvent e)
	{
		translateEventPosition(e);
		if (!active)
		{
			if (insideDomain(e.x, e.y)) enterArea(e);
			return;
		}
		if (!insideDomain(e.x, e.y))
		{
			leaveArea(e);
			return;
		}

		// Um Drag-Events zu erkennen, muss die zuletzt gedrückte Maustaste abfragbar sein.
		e.button = button;
		this.mouseEvent = e;
		if (mode != null)
		{
			if (mode.move(this, e.x, e.y))
			{
				fireSelectionUpdate(e);
				update();
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e)
	{}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if (!active) return;
		
		translateEventPosition(e);
		if (!insideDomain(e.x, e.y)) return;
		// System.out.println("[SelectionManager] " + e.x + ", " + e.y);
		
		this.mouseEvent = e;
		this.button = e.button;
		if (fireSelectionStart(e, findSelection(e.x, e.y)))
		{
			System.out.println("[SelectionManager] selection start: " + e.x + ", " + e.y);
			if (mode != null) mode.activate(this, e.x, e.y);
			update();
		}
	}

	@Override
	public void mouseUp(MouseEvent e)
	{
		if (!active) return;
		
		translateEventPosition(e);
//		if (!insideArea(e.x, e.y)) return;

		this.mouseEvent = e;
		this.button = 0;
		if (!fireSelectionEnd(e))
		{
			System.out.println("[SelectionManager] selection end: " + e.x + ", " + e.y);
			if (mode != null) mode.deactivate(this, e.x, e.y);
		}
		update();
//		SelectionMode mode = modes[button];
//		button = 0;
//		if (mode != null && mode != modes[0])
//		{
//			mode.deactivate(this, e.x, e.y);
//			if (modes[0] != null) modes[0].activate(this, e.x, e.y);
//			fireSelectionModeChanged(mode, modes[0]);
//			
//			update();
//		}
	}
	
	private void enterArea(MouseEvent e)
	{
		if (active) return;
		active = true;
		
//		SelectionMode mode = modes[button];
//		if (mode != null)
//		{
//			mode.activate(this, e.x, e.y);
//			fireSelectionModeChanged(null, mode);
//			// selection.setVisible(true);
//			update();
//		}
	}
	
	private void leaveArea(MouseEvent e)
	{
		if (!active || !disableOutside) return;
		active = false;

//		SelectionMode mode = modes[button];
//		if (mode != null)
//		{
//			mode.deactivate(this, e.x, e.y);
//			fireSelectionModeChanged(mode, null);
//			// selection.setVisible(false);
//			update();
//		}
	}
	
	private boolean insideDomain(int x, int y)
	{
		return (x >= 0 && y >= 0 && x < domain.width && y < domain.height);
	}
	
	protected void update()
	{
		for (int i = selections.size() - 1; i >= 0; i--)
		{
			Selection s = selections.get(i);
			
			if (!s.isDisposed() && !s.needRefresh()) break;
	
			Rectangle rect = s.getModificationBounds();
			rect.x += domain.x;
			rect.y += domain.y;
			s.refresh();
			
			if (s.isDisposed())
				selections.remove(i);
		}
		control.redraw();
	}

	private void translateEventPosition(MouseEvent e)
	{
		e.x -= domain.x;
		e.y -= domain.y;
	}

	public void addSelectionListener(SelectionListener l)
	{
		if (listeners.contains(l)) return;

		listeners.add(l);
	}

	public void removeSelectionListener(SelectionListener l)
	{
		listeners.remove(l);
	}

	protected boolean fireSelectionStart(MouseEvent event, Selection selection)
	{
		SelectionEvent selectionEvent = new SelectionEvent(this, selection, event);
		for (SelectionListener l : listeners)
		{
			l.startSelection(selectionEvent);
		}
		return selectionEvent.doIt;
	}
	
	protected boolean fireSelectionEnd(MouseEvent event)
	{
		SelectionEvent selectionEvent = new SelectionEvent(this, getSelection(), event);
		for (SelectionListener l : listeners)
		{
			l.endSelection(selectionEvent);
		}
		return selectionEvent.doIt;
	}
	
	protected boolean fireSelectionUpdate(MouseEvent event)
	{
		SelectionEvent selectionEvent = new SelectionEvent(this, getSelection(), event);
		for (SelectionListener l : listeners)
		{
			l.updateSelection(selectionEvent);
		}
		return selectionEvent.doIt;
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		button = 0;
		active = false;
//		fireSelectionEnd(null);
	}
}
