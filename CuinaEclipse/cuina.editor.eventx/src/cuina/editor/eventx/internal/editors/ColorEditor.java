package cuina.editor.eventx.internal.editors;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

public class ColorEditor implements TypeEditor<Color>
{
	private static final org.eclipse.swt.graphics.Color[] COLORS = new org.eclipse.swt.graphics.Color[4];
	
	private Color color;
	
	private static final int ELEMENT_COUNT = 4;
//	private Label[] labels = new Label[4];
	private Slider[] sliders = new Slider[4];
	private Text[] texts = new Text[4];
	
	private boolean update;
	
	@Override
	public void init(Object value)
	{
		if (value != null)
			this.color = (Color) value;
		else
			this.color = Color.WHITE;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
		ColorVerifier verifier = new ColorVerifier();
		Handler handler = new Handler();
		
		COLORS[0] = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		COLORS[1] = parent.getDisplay().getSystemColor(SWT.COLOR_GREEN);
		COLORS[2] = parent.getDisplay().getSystemColor(SWT.COLOR_BLUE);
		COLORS[3] = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		int[] cc = new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		for (int i = 0; i < ELEMENT_COUNT; i++)
		{
//			labels[i] = new Label(parent, SWT.BORDER);
//			labels[i].setLayoutData(new GridData(32, 20));
//			labels[i].setBackground(COLORS[i]);
			sliders[i] = new Slider(parent, SWT.NONE);
			sliders[i].setBackground(COLORS[i]);
			sliders[i].addListener(SWT.Selection, handler);
			sliders[i].setValues(0, 0, 256, 1, 1, 10);
			texts[i] = new Text(parent, SWT.BORDER);
			texts[i].addVerifyListener(verifier);
			texts[i].addListener(SWT.Modify, handler);
			
			texts[i].setText( Integer.toString(cc[i]));
		}
	}

	@Override
	public boolean apply()
	{
		color = new Color(sliders[0].getSelection() 	<< 6
			  + sliders[1].getSelection()	<< 4
			  + sliders[2].getSelection()	<< 2
			  + sliders[3].getSelection(), true);
		
		//DEBUG:
		System.out.println("[ColorEditor] Ergebnis: " + color);
		
		return true;
	}

	@Override
	public Color getValue()
	{
		return color;
	}
	
	private class ColorVerifier implements VerifyListener
	{
		@Override
		public void verifyText(VerifyEvent ev)
		{
			ev.doit = false;
			// Konstruiere den Text;
			StringBuilder builder = new StringBuilder( ((Text) ev.widget).getText() );
			builder.replace(ev.start, ev.end, ev.text);
			try
			{
				int value = Integer.parseInt(builder.toString());
				if (value < 0 || value > 255) return;
			}
			catch(NumberFormatException e) { return; }
			ev.doit = true;
		}
	}
	
	private class Handler implements Listener
	{
		@Override
		public void handleEvent(Event event)
		{
			if (update) return;
			update = true;

			for (int i = 0; i < ELEMENT_COUNT; i++)
			{
				if (event.type == SWT.Modify)
				{
					if (event.widget == texts[i])
					{
						sliders[i].setSelection(Integer.parseInt(texts[i].getText()));
						break;
					}
				}
				else
				{
					if (event.widget == sliders[i])
					{
						texts[i].setText(Integer.toString(sliders[i].getSelection()));
						break;
					}
				}
			}
			update = false;
		}
	}
}