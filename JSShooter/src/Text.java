import java.awt.*;

public class Text
{
	private double x;
	private double y;
	private long time;
	private String s;
	
	private long start;
	
	public Text(double x, double y, long time, String s)
	{
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;
		
		start = System.nanoTime();
	}
	
	public boolean update()
	{
		x = GamePanel.player.getX() + GamePanel.player.getR();
		y = GamePanel.player.getY() - GamePanel.player.getR();
		long elapsed = (System.nanoTime() - start)/1000000;
		if(elapsed > time)
		{
			return true;
		}
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		
		long elapsed = (System.nanoTime() - start)/1000000;
		int alpha = (int) (255* Math.sin(Math.PI * elapsed/time));
		if(alpha > 255) 
			alpha = 255;
		if(alpha < 0)
			alpha = 0;
		
		g.setColor(new Color(0, 0, 0, alpha));
		g.drawString(s, (int)x, (int)y);
	}
}