package cuina.plugin;

import java.util.Set;

public interface Upgradeable
{
	public Object getExtension(String key);

	public void addExtension(String key, Object instance);

	public Set<String> getExtensionKeys();
}
