package cuina.script;

import cuina.GameEvent;


public interface MainScript
{
	public void start();
	public void newGame(GameEvent session);
	public void loadGame(GameEvent session);
	public void endGame(GameEvent session);
	public boolean close();
}
