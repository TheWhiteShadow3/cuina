package cuina.database;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

public class DBPropertyTester extends PropertyTester
{
	private static final String NAME 					= "name";
	private static final String TYPE 					= "type";
	private static final String IS_TABLE_FILE 			= "isTableFile";
//	private static final String IS_DEFAULT_TABLE_FILE 	= "isDefaultTaleFile";
	
	@Override
	public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IFile)
		{
			return testFile((IFile) receiver, method, args, expectedValue);
		}
		return false;
	}

	private boolean testFile(IFile file, String method, Object[] args, Object expectedValue)
	{
		if (NAME.equals(method))
		{
			return testName(file, toString(expectedValue));
		}
		else if (TYPE.equals(method))
		{
			String dataClassName = DatabasePlugin.getDescriptor(file).getDataClass().getName();
			return dataClassName.equals(expectedValue);
		}
		else if (IS_TABLE_FILE.equals(method))
		{
			return isTableFile(file) == toBoolean(expectedValue);
		}
//		else if (IS_DEFAULT_TABLE_FILE.equals(method))
//		{
//			return isTableFile(file) && DatabasePlugin.getDatabase(file.getProject()).getPreferredDataFile(expectedValue.toString());
//		}
		return false;
	}
	
	private boolean testName(IFile file, String expectedName)
	{
		String name = file.getName();
		String ext = file.getFileExtension();
		if (name.length() <= ext.length() + 1) return false;
		
		return name.substring(0, ext.length() - 1).equals(expectedName);
	}
	
	private boolean isTableFile(IFile file)
	{
		return DatabasePlugin.isDataFile(file);
	}
	
	private boolean toBoolean(Object obj)
	{
		return Boolean.TRUE.equals(obj);
	}
	
	private String toString(Object obj)
	{
		return obj == null ? null : obj.toString();
	}
}
