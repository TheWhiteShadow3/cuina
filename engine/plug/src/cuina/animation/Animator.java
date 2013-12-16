package cuina.animation;

import java.io.Serializable;


public interface Animator extends Serializable
{
	public void init(Model model);
	public void update();
	public void animationFinished();
}
