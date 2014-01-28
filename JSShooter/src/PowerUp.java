/**
  * <Beschreibung>
  *
  *
  * @version 1.0 from 25 Jan 2014
  * @author Joshua
  */

import java.awt.*;

public class PowerUp
{
	public enum PowerUpType
	{
		REPAIR,
		POWERUP,
		SHIELDUPGRADE;
	}
	
	double x;
	double y;
	int r;
	
	Color color1;
	
	private PowerUpType type;
	
	public PowerUp(PowerUpType pt, double x, double y)
	{
		switch(pt)
		{
		case REPAIR: color1 = Color.gray; break;
		case POWERUP: color1 = Color.red; break;
		case SHIELDUPGRADE: color1 = Color.yellow; break;
		}
		
		this.type = pt;
		
		this.x = x;
		this.y = y;
		
		r = 7;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public int getR()
	{
		return r;
	}

	public PowerUpType getType() 
	{ 
		return type;
	}

	public boolean update()
	{
		 y += 2;
		 
		 if(y > GamePanel.HEIGHT + r)
		 	return true;
		 else
			 return false;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(color1);
		g.fillRect((int)x, (int)y, 2*r, 2*r);
		g.setColor(color1.darker());
		g.setStroke(new BasicStroke(3));
		g.drawRect((int)x, (int)y, 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
	}

	public Color getColor1()
	{
		return color1;
	}
}
