package cuina.plugin;

/**
 * Kapselt ein LifeCycle-Interface.
 * @author TheWhiteShadow
 * @see LifeCycle
 */
public class LifeCycleAdapter implements LifeCycle
{
	@Override
	public void init() {}

	@Override
	public void update() {}

	@Override
	public void postUpdate() {}

	@Override
	public void dispose() {}
}
