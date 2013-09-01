package cuina.rpg.actor;

import cuina.rpg.NamedItem;

import java.io.Serializable;

/** Update-Relevant */
public class Attribut implements Serializable, NamedItem
{
	private static final long serialVersionUID = 1920077519253579015L;

	/** Limitiert den Wert lediglich auf das Maximum. <code>Wert<=Max</code> */
	public static final int MODIFY_NONE = 0;
	/** Setzt den Wert auf das neue Maximum. <code>Wert=Max</code> */
	public static final int MODIFY_ABSOLUTE = 1;
	/** Addiert die Differenz zum Wert hinzu. <code>Max-Wert=Const</code> */
	public static final int MODIFY_ADDITIV = 2;
	/** Multipliziert das Verhältnis der Änderung. <code>Wert/Max=Const</code> */
	public static final int MODIFY_MULTI = 3;

	private String name;

	private float[] primaryParams;
	private float[] secondaryParams;
	private long limit;
	private long max;
	private long value;
	private int digitCount = 2;

	/**
	 * Erstellt ein Attribut mit dem angegebenen Namen und
	 * Berechnungs-Parameter.<br>
	 * 
	 * @param name
	 *            Name des Attributs.
	 * @param config
	 *            Konfiguration zur Berechnung der Maximal-Werte. Muss ein Array
	 *            der Länge 3 sein.
	 * @param fix
	 *            Fixes Attribut oder veränderbares Attribut.
	 * @see #calc(int)
	 */
	public Attribut(String name, float[] config, boolean fix)
	{
		this.name = name;
		if (fix) this.max = -1;
		this.primaryParams = config;
	}

	/**
	 * Erstellt ein fixes Attribut mit dem angegebenen Namen und einer
	 * Priorität.
	 * 
	 * @param name
	 *            Name des Attributs.
	 * @param value
	 *            Wert des Attributs.
	 */
	public Attribut(String name, long value)
	{
		this.name = name;
		this.max = -1;
		setMax(value);
	}

	/**
	 * Erstellt ein variables Attribut mit dem angegebenen Namen, sowie einer
	 * Priorität.
	 * 
	 * @param name
	 *            Name des Attributs.
	 * @param max
	 *            Maximaler Attribut-Wert.
	 * @param value
	 *            Anfangswert des Attributs. Muss zwischen 0 und max liegen.
	 */
	public Attribut(String name, long max, long value)
	{
		this.name = name;
		setMax(max);
		setValue(value);
	}

	public void setLimit(long limit)
	{
		this.limit = limit;
		setMax(max);
	}

	public long getLimit()
	{
		return limit;
	}

	public long getMax()
	{
		return max;
	}

	public void setMax(long max)
	{
		if (max >= 0) this.max = max;
		if (limit > 0 && max > limit) max = limit;
		if (value > max) value = max;
	}

	public long getValue()
	{
		return value;
	}

	public void setValue(long value)
	{
		if (isFix())
			throw new UnsupportedOperationException("Wert von " + name
					+ " kann nicht verändert werden, da es ein fixes Attribut ist.");
		if (value < 0)
			this.value = 0;
		else if (value > max)
			this.value = max;
		else
			this.value = value;
	}

	public void add(long value)
	{
		setValue(this.value + value);
	}

	public void sub(long value)
	{
		setValue(this.value - value);
	}

	public long subRel(float value)
	{
		return -addRel(-value);
	}

	public long addRel(float value)
	{
		long n = (long) (getMax() * value);
		setValue(this.value + n);
		return n;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public boolean isFix()
	{
		return (max == -1);
	}

	/**
	 * Gibt an, ob das Attribut levelabhängig berechnet werden kann.
	 * 
	 * @see #calc(int)
	 */
	public boolean isCalculable()
	{
		return (primaryParams != null);
	}

	public void fill()
	{
		if (!isFix()) value = max;
	}

	/**
	 * Ändert das Maximum des Attributes entsprechend den angegebenen
	 * Parametern. Dadurch kann eine temporäre Abweichung zur eigentlichen
	 * Berechnung eingestellt werden. Zusätzlich kann der aktuelle Wert relativ
	 * zur Änderung angepasst werden.
	 * 
	 * @param add
	 *            Additiver Anteil. default 0
	 * @param fac
	 *            Multiplikativer Anteil. default 1
	 * @param valueInfluence
	 *            Einfluss auf den aktuellen Wert bei nicht fixen Attributen.
	 */
	public void setSecondaryCalculation(float add, float fac, int valueInfluence)
	{
		long oldMax = max;
		if (secondaryParams == null) secondaryParams = new float[2];
		secondaryParams[0] = add;
		secondaryParams[1] = fac;
		setMax((long) (secondaryParams[0] + secondaryParams[1] * max));

		if (isFix()) return;

		switch (valueInfluence)
		{
			case MODIFY_NONE:
				if (value > max) value = max;
				break;
			case MODIFY_ABSOLUTE:
				value = max;
				break;
			case MODIFY_ADDITIV:
				value += max - oldMax;
				break;
			case MODIFY_MULTI:
				value *= max / oldMax;
				break;
		}
	}

	public float[] getSecondaryCalculation()
	{
		return secondaryParams;
	}

	/**
	 * Berechnet den maximalen Wert anhand des Levels.<br>
	 * <b>Formel:</b>
	 * 
	 * <pre>
	 * [0] + [1] * level ^ [2]
	 * </pre>
	 * 
	 * Wenn das Attribut keine Parameter zur Berechnung hat, wird der aktuelle
	 * max-Wert zurückgegeben.
	 * 
	 * @param level
	 *            Level als Grundlage zur Berechnung.
	 * @return neues Maximum.
	 * @see #isCalculable()
	 */
	public long calc(int level)
	{
		if (!isCalculable()) return max;
		// throw new UnsupportedOperationException("Max-Wert von " + name +
		// " kann nicht berechnet werden.");
		setMax((long) (primaryParams[0] + primaryParams[1] * Math.pow(level, primaryParams[2])));
		if (secondaryParams != null) max = (long) (secondaryParams[0] + secondaryParams[1] * max);
		return max;
	}

	@Override
	public String toString()
	{
		if (digitCount <= 0)
		{
			if (isFix())
				return String.valueOf(max);
			else
				return value + "/" + max;
		}
		else
		{
			String formatString = "%" + digitCount + "d";
			if (isFix())
				return String.format(formatString, max);
			else
				return String.format(formatString, value) + "/" + String.format(formatString, max);
		}
	}
}
