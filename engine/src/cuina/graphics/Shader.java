package cuina.graphics;

import cuina.Game;
import cuina.Logger;
import cuina.util.LoadingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import org.lwjgl.opengl.GL20;

public class Shader implements Serializable
{
	private static final long serialVersionUID = 3730591999820661352L;

	public static String ShaderDirectory = Game.getRootPath();
	
	/** Dummy-Shader zum Überschreiben eines übergeordneten Shaders. */
	public static final Shader EMPTY_SHADER = new Shader(null);
//	private static HashMap<String, Shader> instances = new HashMap<String, Shader>();
	
	private String name;
	transient private boolean valid = true;
	transient private int program = 0;
	transient private int vertShader = 0;
	transient private int fragShader = 0;
//	private int geomShader = 0;

	/**
	 * Erstellt ein neues Shaderprogram mit dem angegebenen Namen.
	 * Die einzelnen Shader müssen im <code>ShaderDirectory</code> vorliegen.<br>
	 * Die Syntax der Shader-Quelldateien ist folgende:
	 * <dl>
	 * 		<dt>Vertex-Shader</dt>
	 * 		<dd><code><i>ShaderName</i>.vx</code></dd>
	 * 		<dt>Fragment-Shader</dt>
	 * 		<dd><code><i>ShaderName</i>.fx</code></dd>
	 * </dl>
	 * @param shaderName Name des Shaders.
	 * @throws LoadingException
	 * @throws ShaderException
	 */
	public Shader(String shaderName)
	{
		this.name = shaderName;
	}
	
	public void refresh()
	{
		if (name == null) return;
		
		try
		{
			program = GL20.glCreateProgram();
			if (program == 0) throw new ShaderException("Shader-Program konnte nicht erstellt werden.");
			
			vertShader = createShader(GL20.GL_VERTEX_SHADER, new File(ShaderDirectory, name + ".vx"));
			fragShader = createShader(GL20.GL_FRAGMENT_SHADER, new File(ShaderDirectory, name + ".fx"));
			
			if(fragShader != 0) GL20.glAttachShader(program, fragShader);
			if(vertShader != 0) GL20.glAttachShader(program, vertShader);
//			if(geomShader != 0) GL20.glAttachShader(program, geomShader);
			GL20.glDeleteShader(vertShader);
			GL20.glDeleteShader(fragShader);
			
			GL20.glLinkProgram(program);
			checkShaderLog(program);
			
			valid = true;
		}
		catch (LoadingException | ShaderException e)
		{
			Logger.log(Shader.class, Logger.WARNING, e);
			valid = false;
		}
	}
	
	public boolean isValid()
	{
		return valid;
	}

	private int createShader(int type, File file) throws ShaderException, LoadingException
	{
		if (!file.exists()) return 0;
		int shader = GL20.glCreateShader(type);
		
		if (shader == 0) throw new ShaderException("Shader " + file + "konnte nicht erstellt werden.");

		GL20.glShaderSource(shader, readShaderFile(file));
		GL20.glCompileShader(shader);
		
		return shader;
	}
	
	private void checkShaderLog(int program) throws ShaderException
	{
		String log = GL20.glGetProgramInfoLog(program, 512);
		if (log.length() > 0)
		{
			if (log.contains("error"))
				throw new ShaderException("Shader-Program konnte nicht erstellt werden.\n" + log);
			else
				System.err.println("[Shader] " + log);
		}
	}
	
	private String readShaderFile(File file) throws LoadingException
	{
		StringBuffer buffer = new StringBuffer();
		String line;
		try(BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line + "\n");
			}
		}
		catch (IOException e)
		{
			throw new LoadingException(file, e);
		}
		return buffer.toString();
	}
	
	public void bind()
	{
		if (name != null && program == 0 && valid) refresh();
		GL20.glUseProgram(program);
	}
	
	public static void unbind()
	{
		GL20.glUseProgram(0);
	}

	@Override
	public String toString()
	{
		return "Shader: " + name + " [vertex=" + (vertShader != 0) + " fragment=" + (fragShader != 0) + "]";
	}
	
//	private int createGeomShader(String filename)
//	{
//		geomShader = GL20.glCreateShader(EXTGeometryShader4.GL_GEOMETRY_SHADER_EXT);
//		
//		if (geomShader == 0)
//		{
//			return 0;
//		}
//		String geomCode = "";
//		String line;
//		try
//		{
//			BufferedReader reader = new BufferedReader(new FileReader(filename));
//			while ((line = reader.readLine()) != null)
//			{
//				geomCode += line + "\n";
//			}
//		} catch (Exception e)
//		{
//			return 0;
//		}
//
//		compileShader(geomShader, geomCode);
//
//		return geomShader;
//	}
	

}