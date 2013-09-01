package cuina.resource.internal;

import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Directory;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;


public class CuinaPropertyTester extends PropertyTester
{
	private static final String DIRECTORY 			= "directory";

	@Override
	public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IFolder)
		{
			return testFile((IFolder) receiver, method, args, expectedValue);
		}
		return false;
	}
	
	private boolean testFile(IFolder file, String method, Object[] args, Object expectedValue)
	{
		if (DIRECTORY.equals(method))
		{
			return testDirectory(file, expectedValue);
		}
		return false;
	}


	private boolean testDirectory(IFolder file, Object expectedValue)
	{
		Directory dir = ResourceManager.getDirectories().get(expectedValue);
		if (dir == null) return false;
		
		System.out.println("File: " + file.getProjectRelativePath());
		System.out.println("Dir:  " + dir.getPath());
		
		return file.getProjectRelativePath().equals(dir.getPath());
	}
}
