package cuina.script;

import cuina.GameEvent;
import cuina.Scene;

public interface MainScript
{
	public void start();
	public void newScene(Scene scene);
	public void newGame(GameEvent session);
	public void loadGame(GameEvent session);
	public void endGame(GameEvent session);
	public boolean close();
}
