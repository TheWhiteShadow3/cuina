package cuina.ks;

import cuina.rpg.actor.Actor;
import cuina.rpg.actor.ActorData;

public class Enemy extends Actor
{
	private static final long serialVersionUID = 7804495559178100673L;

	private long xpGain;
	private KiSetting kiSetting;

	public Enemy()
	{
		super();
	}

	public Enemy(ActorData actorData)
	{
		super(actorData);
	}

	public long getXpGain()
	{
		return xpGain;
	}

	public void setXpGain(long xpGain)
	{
		this.xpGain = xpGain;
	}

	public KiSetting getKiSetting()
	{
		return kiSetting;
	}

	public void setKiSetting(KiSetting kiSetting)
	{
		this.kiSetting = kiSetting;
	}
}
