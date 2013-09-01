package cuina.editor.map;

import cuina.map.Map;

public class MapEvent
{
	public static final int PROP_NONE 		= 0;
	public static final int PROP_SIZE 		= 1;
	public static final int PROP_TILES 		= 2;
	public static final int PROP_TILESET 	= 4;
	public static final int PROP_OBJECTS 	= 8;
	public static final int PROP_AREAS 		= 16;
	public static final int PROP_PATHS 		= 32;
	
	public Object source;
	public Map map;
	public int props;
	
	public MapEvent(Object source, Map map, int props)
	{
		this.source = source;
		this.map = map;
		this.props = props;
	}
}
