package cuina.map.movement;

import cuina.object.BaseObject;
import cuina.plugin.LifeCycleAdapter;
import cuina.world.CuinaMask;
import cuina.world.CuinaMotor;
import cuina.world.CuinaObject;
 
public class Motor extends LifeCycleAdapter implements CuinaMotor
{
    private static final long   serialVersionUID    = -5062893747414485936L;
    
    protected CuinaObject object;
    // Bewegungs-Geschwindigkeit in 1/32 Pixel pro Frame
    private float speedX;
    private float speedY;
    private float friction;
    private float direction;
    private Driver driver;
    private Force force;
    private boolean moving;
    
    public Motor()
    {
        
    }
    
    public Motor(CuinaObject object, float speed, float friction, float direction)
    {
        this.object = object;
        this.friction = friction;
        this.direction = direction;
        setSpeed(speed);
    }
    
	public Motor(CuinaObject object, Motor clone)
	{
		this.object = object;
		this.speedX = clone.speedX;
		this.speedY = clone.speedY;
		this.friction = clone.friction;
		this.direction = clone.direction;
	}

	protected void setObject(CuinaObject object)
	{
		this.object = object;
	}

	public CuinaObject getObject()
	{
		return object;
	}

	public Driver getDriver()
	{
		return driver;
	}

	public void setDriver(Driver driver)
	{
		this.driver = driver;
		if (driver != null) driver.init(this);
	}

	public float getFriction()
	{
		return friction;
	}

	public void setFriction(float friction)
	{
		this.friction = (friction > 1) ? 1 : friction;
	}

	public Force getForce()
	{
		return force;
	}
 
    public void setForce(Force force)
    {
        this.force = force;
    }
 
    public float getSpeed()
    {
        return (float) Math.hypot(speedX, speedY);
    }
    
    public void setSpeed(float speed)
    {
    	float dir = getRadDirection();
        speedX = (float) (speed * +Math.cos(dir));
        speedY = (float) (speed * -Math.sin(dir));
        
        if (Math.abs(speedX) < 0.0001f) speedX = 0;
        if (Math.abs(speedY) < 0.0001f) speedY = 0;
    }
    
    /**
     * Gibt die Richtung in Degre zurück.
     * <pre>Intervall: 0 &lt;= d &lt; 360</pre>
     * @return Richtung in Degre.
     */
    public float getDirection()
    {
        return direction;
    }
    
    /**
     * Gibt die Richtung in Radiant zurück.
     * <pre>Intervall: 0 &lt;= d &lt; 2pi</pre>
     * @return Richtung in Radiant.
     */
    public float getRadDirection()
    {
        return direction * (float)Math.PI / 180f;
    }
 
    public void setDirection(float direction)
    {
        while(direction >= 360) direction -= 360;
        while(direction < 0) direction += 360;
        
        this.direction = direction;
    }
 
    @Override
    public void update()
    {
        if (driver != null) driver.update();
        
        updateMove();
    }
    
	private void updateMove()
	{
		if (speedX != 0 || speedY != 0)
		{
			speedX = (1 - friction) * speedX;
			speedY = (1 - friction) * speedY;
		}
		
		if (force != null && force.value != 0)
		{
			float dir = force.direction * (float) Math.PI / 180f;
			speedX += force.value * +Math.cos(dir);
			speedY += force.value * -Math.sin(dir);
		}
		
		if (speedX != 0 || speedY != 0) move(speedX, speedY);
	}
    
    public boolean isMoving()
    {
        return moving;
    }
    
    public boolean moveDir(float direction, boolean turn)
    {
        return moveDir(direction, getSpeed(), turn);
    }
    
    public float turn(BaseObject obj, float value)
    {
        if (obj == null) return -1;
        
        float dir = MovementUtil.getDirection(this.object, obj);
        if (Float.isNaN(dir)) return dir;
        return turn(dir, value);
    }
    
    public float turn(float targetDirection, float value)
    {
        float angle = dirTo(targetDirection);
        
        if (angle > value)
        {
            angle = value;
        }
        else if (angle < -value)
        {
            angle = -value;
        }
        
        setDirection(this.direction + angle);
        return angle;
    }
    
    /**
     * Gibt die Richtung zur Zielrichtung im Intervall:
     * <pre>-180 &lt; x &lt;= 180</pre> an.
     * @param targetDir Zielrichtung.
     * @return Richtung zum Ziel.
     */
    private float dirTo(float targetDir)
    {
        targetDir -= this.direction;
        
        while(targetDir > 180) targetDir -= 360;
        while(targetDir <= -180) targetDir += 360;
        
        return targetDir;
    }
    
    /**
     * Gibt die X-Position in der Entfernung dist in der aktuellen Richtung zurück.
     * Damit lässt sich eine zukünftige Position herausfinden.
     * <p>
     * Die Methode gibt dasselbe zurück wie:<p>
     * <code>dist * Math.cos(getRadDirection());</code>
     * </p>
     * @param dist Entfernung zur aktuellen Position.
     * @return X-Position in angegebener Entfernung in der aktuellen Richtung.
     * @see #getDY(float)
     * @see #getRadDirection()
     */
    public int getDX(float dist)
    {
        return (int) Math.round( dist * +Math.cos(getRadDirection()) );
    }
    
    /**
     * Gibt die Y-Position in der Entfernung dist in der aktuellen Richtung zurück.
     * Damit lässt sich eine zukünftige Position herausfinden.
     * <p>
     * Die Methode gibt dasselbe zurück wie:<p>
     * <code>dist * -Math.sin(getRadDirection());</code>
     * </p>
     * @param dist Entfernung zur aktuellen Position.
     * @return Y-Position in angegebener Entfernung in der aktuellen Richtung.
     * @see #getDX(float)
     * @see #getRadDirection()
     */
    public int getDY(float dist)
    {
        return (int) Math.round( dist * -Math.sin(getRadDirection()) );
    }
    
    /**
     * Bewegt das Objekt zur absoluten Position x/y.
     * @param x X-Poition in 1/32 Pixel
     * @param y Y-Poition in 1/32 Pixel
     * @param turn Bestimmt ob der Char in jedem Fall gedreht werden soll.
     * @return true, wenn keine Kollision statt fand, andernfalls false.
     */
    public boolean moveTo(float x, float y, boolean turn)
    {
        return move(x - object.getX(), y - object.getY(), turn);
    }
    
    /**
     * Bewegt das Objekt relativ um dx/dy.
     * @param dx X-Poition in 1/32 Pixel
     * @param dy Y-Poition in 1/32 Pixel
     * @param turn Bestimmt ob der Char in jedem Fall gedreht werden soll.
     * @return true, wenn keine Kollision statt fand, andernfalls false.
     * (Auch bei Kollision kann eine Bewegung statt gefunden haben.)
     */
    public boolean move(float dx, float dy, boolean turn)
    {
        boolean result = move(dx, dy);
        if (result || turn) setDirection(MovementUtil.getDirection(0, 0, dx, dy));
        return result;
    }
    
    public boolean moveDir(float dir, float dist, boolean turn)
    {
        double rDir = dir * Math.PI / 180d;
        int dx = (int) Math.round(dist * +Math.cos(rDir));
        int dy = (int) Math.round(dist * -Math.sin(rDir));
        
//      System.out.println("Bewege Motor in Richtung: " + direction + " X/Y: " + dx + " / " + dy);
        
        boolean result = move(dx, dy);
        if (result || turn) setDirection(dir);
        return result;
    }
    
    private boolean move(float dx, float dy)
    {
        float x = object.getX();
        float y = object.getY();
        float newX;
        float newY;
        
        CuinaMask box = (CuinaMask) object.getExtension(CuinaMask.EXTENSION_KEY);
        if (box != null)
        {
            // Prüfe Kollisions-Maske
            newX = (x + dx);
            newY = (y + dy);
            if (box.move(newX, newY, true))
            {
                object.setX(x + dx);
                object.setY(y + dy);
                moving = true;
                return true;
            }
            else
            {
                if (dx != 0 && dy != 0)
                {
                    newX = x;
                    newY = (y + dy);
                    if (box.move(newX, newY, false))
                    {
                        object.setY(y + dy);
                        moving = true;
                        return false;
                    }
                    newX = (x + dx);
                    newY = y;
                    if (box.move(newX, newY, false))
                    {
                        object.setX(x + dx);
                        moving = true;
                        return false;
                    }
                }
            }
            moving = false;
            if ((dx != 0 || dy != 0) && driver != null) driver.blocked();
            return false;
        }
        else
        {
            object.setX(x + dx);
            object.setY(y + dy);
            moving = true;
            return true;
        }
    }
}