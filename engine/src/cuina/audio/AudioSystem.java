package cuina.audio;

import cuina.Game;
import cuina.Logger;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.net.URL;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.codecs.CodecIBXM;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecJSpeex;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import de.cuina.fireandfuel.CodecJLayerMP3;

public class AudioSystem
{
	private static SoundSystem soundSystem;
	
	private AudioSystem() {}
	
	public static void start()
	{
		try
		{
//			boolean aLCompatible = SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class);
//			boolean jSCompatible = SoundSystem.libraryCompatible(LibraryJavaSound.class);
//	
//			if(true)
//			{
//				libraryType = LibraryLWJGLOpenAL.class; // OpenAL
//			}
//	
//			else if(jSCompatible)
//			{
//	
//				libraryType = LibraryJavaSound.class; // Java Sound
//			} else libraryType = Library.class; // "No Sound, Silent Mode"
	
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			SoundSystemConfig.setCodec("xm", CodecIBXM.class);
			SoundSystemConfig.setCodec("s2m", CodecIBXM.class);
			SoundSystemConfig.setCodec("mod", CodecIBXM.class);
			SoundSystemConfig.setCodec("spx", CodecJSpeex.class);
			SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
	
			SoundSystemConfig.setLogger(new SoundSystemLogger()
			{
				@Override
				public void message(String message, int indent)
				{ /* Do NOT print unimportend Stuff! */ }
				
				@Override
				public void importantMessage(String message, int indent)
				{ /* Do NOT print unimportend Stuff! */ }
			});
			Logger.log(AudioSystem.class, Logger.INFO, "start Audio");
			soundSystem = new SoundSystem(LibraryLWJGLOpenAL.class);
	
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
	//		SoundSystemConfig.addLibrary(LibraryJavaSound.class);
		}
		catch (SoundSystemException e)
		{
			// Shouldn’t happen, but it is best to prepare for anything
			Logger.log(AudioSystem.class, Logger.ERROR, e);
		}
	}

	public static void playBGM(int index, String fileName, float volume)
	{
		Logger.log(AudioSystem.class, Logger.DEBUG, "play BGM(" + index +"): " + fileName);
		try
		{
			URL url = ResourceManager.getResource(ResourceManager.KEY_AUDIO, fileName).getURL();
			soundSystem.backgroundMusic(Integer.toString(index), url, fileName, true);
		}
		catch (LoadingException e)
		{
			Logger.log(AudioSystem.class, Logger.ERROR, e);
		}
	}

	public static void playSound(String filename, float volume)
	{
		playSound(filename, 0f, 0f, 0f, volume, 1f);
	}

	public static void playSound(String filename, float x, float y, float z, float volume, float pitch)
	{
		Logger.log(AudioSystem.class, Logger.DEBUG, "play Sound: " + filename);
		
		String id = filename + System.nanoTime();
		try
		{
			URL url = ResourceManager.getResource(ResourceManager.KEY_AUDIO, filename).getURL();
			
			soundSystem.newSource(false, id, url, filename, false, x, y, z,
					SoundSystemConfig.ATTENUATION_NONE, 0f);
			soundSystem.setVolume(id, volume);
			soundSystem.setPitch(id, pitch);
			soundSystem.setTemporary(id, false);
			soundSystem.play(id);
		}
		catch (LoadingException e)
		{
			Logger.log(AudioSystem.class, Logger.ERROR, e);
		}
	}

	public static void stop(int index)
	{
		Logger.log(AudioSystem.class, Logger.DEBUG, "stop BGM(" + index + ')');
		soundSystem.stop(Integer.toString(index));
	}

	public static void fadeOut(int index, long time)
	{
		Logger.log(AudioSystem.class, Logger.DEBUG, "fade-out BGM(" + index + ')');
		soundSystem.fadeOut(Integer.toString(index), null, null, time);
	}

	public static void fadeOutIn(int index, String nextFilename, long time)
	{
		fadeOutIn(index, nextFilename, time, time);
	}
	
	public static void fadeOutIn(int index, String nextFilename, long timeOut, long timeIn)
	{
		Logger.log(AudioSystem.class, Logger.DEBUG, "fade-out-in BGM(" + index + ')');
		try
		{
			URL url = ResourceManager.getResource(ResourceManager.KEY_AUDIO, nextFilename).getURL();
			soundSystem.fadeOutIn(Integer.toString(index), url, nextFilename, timeOut, timeIn);
		}
		catch (LoadingException e)
		{
			Logger.log(AudioSystem.class, Logger.ERROR, e);
		}
	}

	public static void setGlobalVolume(float volume)
	{
		soundSystem.setMasterVolume(volume);
	}

	public static void setVolume(int index, float volume)
	{
		soundSystem.setVolume(Integer.toString(index), volume);
	}

	public static void setPitch(int index, float pitch)
	{
		if (soundSystem == null) return;
		soundSystem.setPitch(Integer.toString(index), pitch);
	}

	public static void setLoop(int index, boolean loop)
	{
		soundSystem.setLooping(Integer.toString(index), loop);
	}

	public static void dispose()
	{
		if (soundSystem == null) return;
		soundSystem.cleanup();
		soundSystem = null;
	}

	public static void main(String[] args) throws LoadingException, InterruptedException
	{
		Game game = new Game();
		game.loadConfig();
		
		AudioSystem.start();
		
		System.out.println("load");
		AudioSystem.playBGM(1, "bgm/test.mp3", 0.65f);
		AudioSystem.playSound("snd/test.wav", 20, 0, 0, 0.6f, 1);
		AudioSystem.playSound("snd/bieb.wav", -20, 0, 0, 0.8f, 1);
		System.out.println("loaded!");
		
		Thread.sleep(5000);
		
		AudioSystem.fadeOutIn(1, "bgm/test.mp3", 3000, 5000);

		Thread.sleep(6000);
		
		System.out.println("stop!");
		AudioSystem.stop(1);
		AudioSystem.playSound("snd/test.wav", -20, 0, 0, 1, 1);
		
		Thread.sleep(5000);
		
		AudioSystem.dispose();
	}
}