package cuina.script;

import cuina.editor.script.Parameters;

public interface MainScript
{
	public void start();
	
	@Parameters(names="game", types="Game")
	public void newGame(Object game);
	
	@Parameters(names="game", types="Game")
	public void loadGame(Object game);
	
	@Parameters(names="game", types="Game")
	public void saveGame(Object game);
	
	@Parameters(names="game", types="Game")
	public void endGame(Object game);
	
	public boolean close();
}