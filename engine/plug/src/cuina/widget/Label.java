package cuina.widget;

import cuina.Context;
import cuina.ContextListener;
import cuina.Game;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.utils.TextUtil;

public class Label extends TextWidget
{
	private LabelContextListener contextListener;
	
	public Label(String text)
	{
		this(null, false);
		setText(text);
	}
	
	public Label(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
	}
	
	public String getText()
	{
		return (String) getCharSequence();
	}

	public void setText(String text)
	{
		if (contextListener != null)
		{
			contextListener.dispose();
		}
		super.setCharSequence(TextUtil.notNull(text));
		invalidateLayout();
	}
	
	/**
	 * Setzt den Text des Labels auf einen Wert im angegebene Kontext.
	 * Der Wet wird automatisch aktualisiert, wenn sich der Wert im Kontext ändert.
	 * @param contextType Typ des Kontextes.
	 * @param key Kontext-Schlüssel.
	 */
	public void setContextEntry(int contextType, String key)
	{
		if (contextListener != null)
		{
			contextListener.dispose();
		}
		this.contextListener = new LabelContextListener(contextType, key);
	}
	
	private class LabelContextListener implements ContextListener
	{
		private Context context;
		private String key;
//		private volatile String text;
		
		private LabelContextListener(int contextType, String key)
		{
			this.context = Game.getContext(contextType);
			this.key = key;
			
			context.addContextListener(this);
			setLabelText(context.get(key).toString());
		}

		@Override
		public void entryChanged(Context context, String key, Object oldValue, Object newValue)
		{
			if (!this.key.equals(key)) return;
			
			setLabelText(newValue.toString());
		}

		@Override
		public void contextDisposing(Context context)
		{
			setLabelText("");
			dispose();
		}

		@Override
		public void contextClearing(Context context)
		{
			setLabelText("");
		}
		
//		@Override
//		public void run()
//		{
//			Label.this.setText(text);
//		}
		
		private void setLabelText(String text)
		{
//			this.text = text;
			Label.this.setText(text);
		}
		
		private void dispose()
		{
			context.removeContextListener(this);
			contextListener = null;
		}
	}
}
