package cuina.graphics;

import java.lang.ref.WeakReference;

/**
 * Kappselt eine Weakreferenz für ein GraphicElement.
 * Damit nicht mehr benötigte Grafiken nicht länger gezeichnet werden,
 * werden diese im GraphicManager als Weakreferenzen gehalten.
 * @author TheWhiteShadow
 */
public class GraphicReference extends WeakReference<Graphic>
{
	public GraphicReference(Graphic referent)
	{
		super(referent);
	}
	
	@Override
	public String toString()
	{
		return "ref: " + get();
	}
}