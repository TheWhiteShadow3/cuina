package plugin.simple;

import cuina.plugin.LifeCycle;

public class SimplePlugin implements LifeCycle
{
	public static int init_value = 0;
	public static int update_value = 0;
	public static int post_update_value = 0;
	public static int dispose_value = 0;
	
	@Override
	public void init()
	{
		init_value++;
	}

	@Override
	public void update()
	{
		update_value++;
	}

	@Override
	public void postUpdate()
	{
		post_update_value++;
	}

	@Override
	public void dispose()
	{
		dispose_value++;
	}
}
