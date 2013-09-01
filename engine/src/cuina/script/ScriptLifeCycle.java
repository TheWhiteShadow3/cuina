package cuina.script;

import cuina.plugin.LifeCycle;

import org.jruby.RubyClass;
import org.jruby.runtime.builtin.IRubyObject;

public class ScriptLifeCycle implements LifeCycle
{
	public final IRubyObject owner;
	public boolean init;
	public boolean update;
	public boolean postUpdate;
	public boolean dispose;
	
	public ScriptLifeCycle(IRubyObject owner)
	{
		this.owner = owner;
		RubyClass clazz = owner.getMetaClass();
		init = clazz.isMethodBound("init", false);
		update = clazz.isMethodBound("update", false);
		postUpdate = clazz.isMethodBound("post_update", false);
		dispose = clazz.isMethodBound("dispose", false);
	}
	
	@Override
	public void init()
	{
		if (init) call("init");
	}
	
	@Override
	public void update()
	{
		if (update) call("update");
	}
	
	@Override
	public void postUpdate()
	{
		if (postUpdate) call("post_update");
	}
	
	@Override
	public void dispose()
	{
		if (dispose) call("dispose");
	}
	
	private void call(String method)
	{
		owner.callMethod(ScriptExecuter.getRuntime().getCurrentContext(), method);
	}
}