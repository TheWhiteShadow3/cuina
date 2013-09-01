package cuina.widget;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.utils.TintAnimator;
 
public class CycleTintAnimator extends TintAnimator
{
    private Color tintColor;
    private int duration;
    
    public CycleTintAnimator(Widget owner, Color tintColor, int duration)
    {
        super(owner);
        this.tintColor = tintColor;
        this.duration = duration;
        addFadeDoneCallback(new Runnable()
        {
            @Override
            public void run()
            {
                if (hasTint())
                    fadeTo(Color.WHITE, CycleTintAnimator.this.duration);
                else
                    fadeTo(CycleTintAnimator.this.tintColor, CycleTintAnimator.this.duration);
            }
        });
    }
    
    public void stop()
    {
        setColor(Color.WHITE);
    }
    
    public void start()
    {
        fadeTo(tintColor, duration);
    }
}