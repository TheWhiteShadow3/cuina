package cuina.animation;

import cuina.database.Database;
import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.io.Serializable;

public class ModelDataReference implements Serializable, Instantiable
{
	private static final long serialVersionUID = -8531043102198836128L;

	public static final String MODEL_DB = "Model";
	
	public String modelKey;
	
	@Override
	public Object createInstance(CuinaObject obj) throws Exception
	{
		return Database.<ModelData>get(MODEL_DB, modelKey).createInstance(obj);
	}
}
