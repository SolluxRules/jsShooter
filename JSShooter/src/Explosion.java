import java.awt.*;

public class Explosion
{
	private double x;
	private double y;
	private int r;
	private int maxRadius;
	private int multipl;
	
	public Explosion(double x, double y, int r, int max)
	{
		this.x = x;
		this.y = y;
		this.r = r;
		maxRadius = max;
		
		multipl = 3;
	}
	
	public boolean update()
	{
		r ++;
		if(r >= maxRadius)
			return true;
		else
			return false;
	}
	
	public void draw(Graphics2D g)
	{
		g.setStroke(new BasicStroke(2));
		g.setColor(new Color(255, 255, 255, 128));
		g.drawOval((int)(x - r), (int)(y - r), multipl*r, multipl*r);
		g.setStroke(new BasicStroke(2));
	}
}