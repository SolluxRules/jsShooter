import java.awt.*;

public class Enemy
{
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	private int health;
	private int type;
	private int rank;
	
	private Color color1;
	
	private boolean ready;
	private boolean dead;
	
	private boolean reallyDead;
	
	private boolean hit;
	private long hitTimer;
	
	public Enemy(int type, int rank)
	{
		this.type = type;
		this.rank = rank;
		
		if(type == 1) // normal Enemy
		{
			color1 = new Color(0, 0, 255, 128);
			switch(rank)
			{
			case 1: 
				speed = 2;
				r = 8;
				health = 1;
				break;
			case 2:
				speed = 2;
				r = 10;
				health = 2;
				break;
			case 3:
				speed = 1.5;
				r = 12;
				health = 4;
				break;
			}
				
			
		} else if(type == 2) // very fast and small
		{
			color1 = new Color(255, 0, 0, 128);
			switch(rank)
			{
			case 1: 
				speed = 4;
				r = 6;
				health = 1;
				break;
			case 2:
				speed = 6;
				r = 5;
				health = 1;
				break;
			case 3:
				speed = 8;
				r = 4;
				health = 2;
				break;
			}
		} else if(type == 3) // big and slow
		{
			color1 = new Color(0, 255, 0, 128);
			switch(rank)
			{
			case 1: 
				speed = 0.5;
				r = 20;
				health = 5;
				break;
			case 2:
				speed = 1;
				r = 25;
				health = 10;
				break;
			case 3:
				speed = 2;
				r = 30;
				health = 15;
				break;
			}
		}else if(type == -1) // boss
		{
			color1 = Color.DARK_GRAY;
			switch(rank)
			{
			case 1: 
				speed = 2;
				r = 40;
				health = 30;
				break;
			case 2:
				speed = 3;
				r = 50;
				health = 40;
				break;
			case 3:
				speed = 4;
				r = 60;
				health = 50;
				break;
			}
		}
		
		x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
		y = -r;
		
		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		ready = false;
		dead = false;
		reallyDead = false;
		
		hit = false;
		hitTimer = 0;
	}
	
	public void hit()
	{
		health--;
		if(health <= 0)
		{
			dead = true;
		}
		hit = true;
		hitTimer = System.nanoTime();
	}
	
	public void update()
	{
		x += dx;
		y += dy;
		
		if(!ready)
		{
			if(x > r && x < GamePanel.WIDTH - r && 
					y > r && y < GamePanel.HEIGHT - r)
			{
				ready = true;
			}
		}
		
		if(x < r && dx < 0) dx = -dx;
		if(y < r && dy < 0) dy = -dy;
		if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
		if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
		
		if(hit)
		{
			long elapsed = (System.nanoTime() - hitTimer)/1000000;
			if(elapsed > 50)
			{
				hit = false;
				hitTimer = 0;
			}
		}
	}
	
	public void draw(Graphics2D g)
	{
		Color colorHit;
		if(hit)
			colorHit = Color.white;
		else
			colorHit = color1;
		
		g.setColor(colorHit);
		g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(colorHit.darker());
		g.drawOval((int)(x-r), (int)(y-r), 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
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
	
	public boolean isDead()
	{
		return dead;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public void explode()
	{
		if(rank > 1)
		{
			int ammount = 0;
			if(type == 1)
			{
				ammount = 3;
			} else if(type == 3)
			{
				ammount = 2;
			}
			
			for(int i = 0; i < ammount; i++)
			{
				Enemy e = new Enemy(getType(), getRank() - 1);
				e.x = this.x;
				e.y = this.y;
				double angle = 0;
				if(!ready)
				{
					angle = Math.random()*360;
				}
				else
				{
					angle = Math.random()*360;
				}
				e.rad = Math.toRadians(angle);
				
				GamePanel.enemies.add(e);
			}
		}
		else
			reallyDead = true;
	}

	
	public double getDx()
	{
		return dx;
	}

	
	public double getDy()
	{
		return dy;
	}
	
	public void setDir(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}

	
	public boolean isReallyDead()
	{
		return reallyDead;
	}
}
