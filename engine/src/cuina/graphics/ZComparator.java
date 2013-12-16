package cuina.graphics;

import java.util.Comparator;


/**
 * Vergleicher für die Z-order der Grafik-Elemente.
 * Wird vom Graphic-Manager benutzt um die Zeichenreihenfolge zu bestimmen.
 * Elemente mit höherem Z-Wert werden später gezeichnet und liegen somit über Elementen mit niedrigerem Z-Wert.
 * @author TheWhiteShadow
 * @version 1.0
 */
public class ZComparator implements Comparator<GraphicReference>
{
	public static final ZComparator INSTANCE = new ZComparator();
	
	private ZComparator() {}
	
	@Override
	public int compare(GraphicReference a, GraphicReference b)
	{
		if(a == null || b == null) return 0;
		Graphic ga = a.get();
		Graphic gb = b.get();
		if(ga == null || gb == null) return 0;
		
		return ga.getDepth() - gb.getDepth();
	}
}
