import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Player
{
	private int x;
	private int y;
	private int r;
	
	private int dx;
	private int dy;
	private int speed;

	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	
	private int weaponLevel;
	
	private boolean recovering;
	private long recoveryTimer;
	private int recoverTimeDur;
	
	private int maxShield;
	private int shield;
	
	private int maxHull;
	private int hull;
	private boolean dead;
	
	private Color color1;
	private Color color2;
	
	private int score;
	
	public Player()
	{
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2 + GamePanel.HEIGHT/4;
		
		r = 12;
		
		dx = 0;
		dy = 0;
		speed = 6;
		
		maxHull = 3;
		hull = 3;
		dead = false;
		
		color1 = Color.white;
		color2 = Color.red;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 200;
		
		weaponLevel = 0;
		
		recoverTimeDur = 2000;
		
		score = 0;
	}
	
	public void update()
	{		
		if(left)
			dx = -speed;
		if(right)
			dx = speed;
		if(up)
			dy = -speed;
		if(down)
			dy = speed;
		
		x += dx;
		y += dy;
		
		if(x < r) x = r;
		if(y < r) y = r;
		if(x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
		if(y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
		
		if(firing)
		{
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if(elapsed > firingDelay)
			{
				GamePanel.bullets.add(new Bullet(270,x,y, this.dx, this.dy));
				firingTimer = System.nanoTime();
			}
		}
		
			if (recovering)
			{
				long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
				if (elapsed > recoverTimeDur)
				{
					recovering = false;
					recoveryTimer = 0;
				}
			}
		// langsames abbremsen!
		if(dx != 0)
		{
			if(dx > 0)
				dx -= 1;
			else
				dx += 1;
		}
		if(dy != 0)
		{
			if(dy > 0)
				dy -= 1;
			else
				dy += 1;
		}
	}
	
	public void draw(Graphics2D g)
	{
		if (recovering)
		{
			g.setColor(color2);
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.drawOval(x - r, y - r, r * 2, r * 2);
			g.setStroke(new BasicStroke(1));
		}
		else
		{
			g.setColor(color1);
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval(x - r, y - r, r * 2, r * 2);
			g.setStroke(new BasicStroke(1));
		}

	}

	public void setLeft(boolean left)
	{
		this.left = left;
	}

	public void setRight(boolean right)
	{
		this.right = right;
	}

	public void setUp(boolean up)
	{
		this.up = up;
	}

	public void setDown(boolean down)
	{
		this.down = down;
	}

	public void resetPlayerPos()
	{
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2;
		
		dx = 0;
		dy = 0;
	}

	public void setFiring(boolean b)
	{
		firing = b;
	}

	public void repair(int n)
	{
		while(n > 0 && hull < maxHull)
		{
			hull++;
			n--;
		}
	}
	
	public void upgradeWeapon()
	{
		weaponLevel++;
	}
	
	public void upgradeShield()
	{
		speed++;
	}
	
	public int getHull()
	{
		return hull;
	}
	
	public boolean isRecovering()
	{
		return recovering;
	}
	
	public void loseLife()
	{
		if(hull > 0)
		{
			hull--;
			recovering = true;
			recoveryTimer = System.nanoTime();
		}
		else
		{
			dead = true;
		}
	}

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}

	public int getR()
	{
		return r;
	}
	
	public void addScore(int n)
	{
		score += n;
	}
	
	public int getScore()
	{
		return score;
	}

	public boolean isDead()
	{
		return dead;
	}

	public int getMaxHull()
	{
		return maxHull;
	}
}