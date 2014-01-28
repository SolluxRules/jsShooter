import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener
{
	public static String version = "V0.04";
	
	public static int WIDTH = 600;
	public static int HEIGHT = 600;
	
	private Thread thread;
	private boolean running;
	private boolean help;
	
	private BufferedImage  image;
	private Graphics2D g;
	
	public static Player player;
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<PowerUp> powerups;
	public static ArrayList<Explosion> explosions;
	public static ArrayList<Text> texts;
	
	private long waveStartTimer;
	private long waveStartTimerDiff;
	private int waveNumber;
	private boolean waveStart;
	private int waveDelay = 3000;
	
	private long lowLifeTimer;
	private long lowLifeTimerDiff;
	private long lowLifeSwitchTime = 500;
	Color color1;
	
	private boolean credits;
	
	private int FPS = 35;	
	private double averageFPS;
	
	public GamePanel()
	{
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
		
		addKeyListener(this);
	}

	@Override
	public void run()
	{
		running = true;
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerups = new ArrayList<PowerUp>();
		explosions = new ArrayList<Explosion>();
		texts = new ArrayList<Text>();

		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;
		
		lowLifeTimer = 0;
		
		help = false;
		credits = false;
		
		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;

		long targetTime = 1000 / FPS;
		
		while(running)
		{
			startTime = System.nanoTime();
			
			gameUpdate();
			gameRender();
			gameDraw();
			
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			
			waitTime = targetTime - URDTimeMillis;
			
			if (waitTime > 0)
			{
				try
				{
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			
			if(frameCount == maxFrameCount)
			{
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
				averageFPS = Math.round(averageFPS*100) / 100.0;
				frameCount = 0;
				totalTime = 0;
			}
		}
		
		g.setStroke(new BasicStroke(10));
		drawString("G A M E  O V E R", WIDTH/2, HEIGHT/2);
		gameDraw();
		
	}
	
	private void gameUpdate()
	{
		if (!help && !credits)
		{
			//new wave
			if (waveStartTimer == 0 && enemies.size() == 0)
			{
				waveNumber++;
				waveStart = false;
				waveStartTimer = System.nanoTime();
			} else
			{
				waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
				if (waveStartTimerDiff > waveDelay)
				{
					waveStart = true;
					waveStartTimer = 0;
					waveStartTimerDiff = 0;
				}
			}
			
			// create enemies
			if (waveStart && enemies.size() == 0)
			{
				createNewEnemies();
			}
			
			// Player update
			player.update();
			
			// PowerUps update
			powerUpUpdate();

			// bullet update
			bulletUpdate();
			
			// enemy update
			enemyUpdate();
			
			// explosion update
			explosionUpdate();
			
			// text update
			textUpdate();
			
			// enemy-enemy collision
			//enemyEnemyCollision();
			
			//bullet-enemy collision
			bulletEnemyCollision();
			
			//player-enemy collision
			playerEnemyCollision();
			
			// player-PowerUp collision
			playerPowerUpCollision();
		}
	}

	private void gameRender()
	{
		// draw Background
		g.setColor(new Color(137, 104, 205));
		g.fillRect(0,0, WIDTH, HEIGHT);
		
		// draw the Rest ( not in HelpMode)
		if (!help && !credits)
		{
			// draw player
			player.draw(g);
			
			// draw enemies
			for (int i = 0; i < enemies.size(); i++)
			{
				enemies.get(i).draw(g);
			}
			
			// draw bullets
			for (int i = 0; i < bullets.size(); i++)
			{
				bullets.get(i).draw(g);
			}
			
			// draw PowerUps
			for(int i = 0; i < powerups.size(); i++)
			{
				powerups.get(i).draw(g);
			}
			
			// draw explosions
			for(int i = 0; i < explosions.size(); i++)
			{
				explosions.get(i).draw(g);
			}
			
			// draw Text
			for(int i = 0; i < texts.size(); i++)
			{
				texts.get(i).draw(g);
			}
			
			// draw wave number
			if(waveStartTimer != 0)
			{
				g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
				String s = "- W A V E   " + waveNumber + " -";
				int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
				int alpha = (int) (255 * Math.sin(Math.PI* waveStartTimerDiff / waveDelay));
				if(alpha > 255) 
					alpha = 255;
				g.setColor(new Color(255, 255, 255, alpha));
				g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
			}
		}
		// draw info
		g.setColor(Color.black);
		g.setFont(new Font("Century Gothic", Font.BOLD, 13));
		g.drawString("FPS: " + averageFPS, 2, 11);
		g.drawString("Enemies: " + enemies.size(), 2, 25);
		g.drawString("Press b for credits", 2, 39);
		g.drawString("Press h for help", 2, 53);
		
		//draw HUD
		// Low Life?
		if(player.getHull() == 0)
		{
			if(lowLifeTimer == 0)
			{
				lowLifeTimer = System.nanoTime();
				color1 = Color.darkGray;
			} else
			{
				lowLifeTimerDiff = (System.nanoTime() - lowLifeTimer) / 1000000;
				
				if(lowLifeTimerDiff < lowLifeSwitchTime)
				{
					color1 = Color.red;
				} else
				{
					color1 = Color.darkGray;
					if(lowLifeTimerDiff > lowLifeSwitchTime*2)
					{
						lowLifeTimer = 0;
					}
				}
			}
		}
		else
		{
			color1 = Color.darkGray;
		}
		
		g.setColor(color1);
		g.setFont(new Font("Century Gothic", Font.BOLD, 14));
		
		String str = "     Hull     ";
		int length = (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
		int height = (int) g.getFontMetrics().getStringBounds(str, g).getHeight();
		g.drawString(str, WIDTH - length, HEIGHT - 10);
		

		
		
		for(int i = 0; i < player.getMaxHull(); i++)
		{
			if(i < player.getHull())
			{
				g.setColor(Color.gray);
			}
			else
			{
				g.setColor(Color.lightGray);
			}
			g.fillRect(WIDTH - length + 10, (int) (HEIGHT - height*(3+i*1.5)), length - 20, height);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color1);
			g.drawRect(WIDTH - length + 10, (int) (HEIGHT - height*(3+i*1.5)), length - 20, height);
			g.setStroke(new BasicStroke(1));
			

		}
		
		// Score
		g.setColor(Color.black);
		g.setFont(new Font("Century Gothic", Font.BOLD, 14));
		str = "score: " + String.valueOf(player.getScore());
		length = (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
		height = (int) g.getFontMetrics().getStringBounds(str, g).getHeight();
			
		g.drawString(str, WIDTH - length - 10, height);
		
		g.setFont(new Font("Century Gothic", Font.PLAIN, 20));
		if(help)
		{
			str = "Arrows to move, c to shoot, r to respawn Enemies";
			String str2 = "Press 'h' to continue";
			length = (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
			int length2 = (int) g.getFontMetrics().getStringBounds(str2, g).getWidth();
			height = (int) g.getFontMetrics().getStringBounds(str2, g).getHeight();
			g.drawString(str, WIDTH/2 - length/2, HEIGHT/2);
			g.drawString(str2, WIDTH/2 - length2/2, HEIGHT/2 + height);
			g.drawString(version, 10, HEIGHT - 10);
		} else if(credits)
		{
			drawString("Credits", WIDTH/2, HEIGHT/4);
			drawString("Code: Joshua Schauer", WIDTH/2, HEIGHT/4 + 50);
			drawString("Art: Aaron Maier", WIDTH/2, HEIGHT/4 + 90);
			drawString("Music: Matti Sack", WIDTH/2, HEIGHT/4 + 130);
		}
	}
	
	private void gameDraw()
	{
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}
	
	private void resetGame()
	{
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerups = new ArrayList<PowerUp>();
		waveNumber = 0;
		waveStartTimer = 0;
	}
	
	private void createNewEnemies()
	{
		enemies.clear();
		
		switch(waveNumber)
		{
		case 1:
			for(int i = 0; i < 4; i++)
			{
				enemies.add(new Enemy(1,1));
			} break;
		case 2:
			for(int i = 0; i < 8; i++)
			{
				enemies.add(new Enemy(1,2));
			} break;
		case 3:
			for(int i = 0; i < 12; i++)
			{
				enemies.add(new Enemy(1,3));
			} break;
		case 4:
			for(int i = 0; i < 4; i++)
			{
				enemies.add(new Enemy(2,1));
			} 
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,3));
			break;
		case 5:
			for(int i = 0; i < 8; i++)
			{
				enemies.add(new Enemy(2,2));
			} 
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,1));
			enemies.add(new Enemy(1,1));
			break;
		case 6: // Boss
			enemies.add(new Enemy(-1,1));
			
			for(int i = 0; i < 4; i++)
			{
				enemies.add(new Enemy(1,3));
			} break;
		case 7:
			for(int i = 0; i < 8; i++)
			{
				enemies.add(new Enemy(3,1));
			} 
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(2,1));
			enemies.add(new Enemy(2,1));
			break;
		case 8:
			for(int i = 0; i < 8; i++)
			{
				enemies.add(new Enemy(3,2));
			} 
			enemies.add(new Enemy(2,3));
			enemies.add(new Enemy(2,2));
			enemies.add(new Enemy(3,1));
			enemies.add(new Enemy(3,1));
			break;
		case 9:
			for(int i = 0; i < 8; i++)
			{
				enemies.add(new Enemy(3,3));
			} 
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(1,1));
			enemies.add(new Enemy(1,1));
			break;
		case 10:
			for(int i = 0; i < 2; i++)
			{
				enemies.add(new Enemy(-1,2));
			} 
			enemies.add(new Enemy(3,3));
			enemies.add(new Enemy(3,3));
			enemies.add(new Enemy(2,3));
			enemies.add(new Enemy(1,3));
			break;
		default:
			waveNumber = 0;
			
		}
	}
	
	@Override
	public void keyTyped(KeyEvent key)
	{
				
	}

	@Override
	public void keyPressed(KeyEvent key)
	{
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT)
			player.setLeft(true);
		if(keyCode == KeyEvent.VK_RIGHT)
			player.setRight(true);
		if(keyCode == KeyEvent.VK_UP)
			player.setUp(true);
		if(keyCode == KeyEvent.VK_DOWN)
			player.setDown(true);
		if(keyCode == KeyEvent.VK_C)
			player.setFiring(true);
		if(keyCode == KeyEvent.VK_R)
			resetGame();
		if(keyCode == KeyEvent.VK_H)
			toggleHelp();
		if(keyCode == KeyEvent.VK_B)
			toggleCredits();
	}

	private void toggleCredits()
	{
		credits = !credits;
	}

	private void toggleHelp()
	{
		help = !help;
	}

	@Override
	public void keyReleased(KeyEvent key)
	{
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT)
			player.setLeft(false);
		if(keyCode == KeyEvent.VK_RIGHT)
			player.setRight(false);
		if(keyCode == KeyEvent.VK_UP)
			player.setUp(false);
		if(keyCode == KeyEvent.VK_DOWN)
			player.setDown(false);
		if(keyCode == KeyEvent.VK_C)
			player.setFiring(false);
	}

	private void drawString(String str,int x,int y)
	{
		int length = (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
		int height = (int) g.getFontMetrics().getStringBounds(str, g).getHeight();
		g.drawString(str, x - length/2, y - height/2);
	}

	private void powerUpUpdate()
	{
		for (int i = 0; i < powerups.size(); i++)
		{
			boolean remove = powerups.get(i).update();
			if (remove)
			{
				powerups.remove(i);
				i--;
			}
		}
	}
	
	private void bulletUpdate()
	{
		for (int i = 0; i < bullets.size(); i++)
		{
			boolean remove = bullets.get(i).update();
			if (remove)
			{
				bullets.remove(i);
				i--;
			}
		}
	}
	
	private void enemyUpdate()
	{
		for (int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).update();
		}
	}
	
	private void explosionUpdate()
	{
		for(int i = 0; i < explosions.size(); i++)
		{
			Boolean remove = explosions.get(i).update();
			
			if(remove)
				explosions.remove(i);
		}
	}
	
	private void textUpdate()
	{
		for(int i = 0; i < texts.size(); i++)
		{
			Boolean remove = texts.get(i).update();
			if(remove)
			{
				texts.remove(i);
			}
		}
	}
	
	private void enemyEnemyCollision()
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e1 = enemies.get(i);
			double e1x = e1.getX();
			double e1y = e1.getY();
			double e1r = e1.getR();
			
			for(int j = 0; j < enemies.size(); j++)
			{
				Enemy e2 = enemies.get(j);
				double e2x = e2.getX();
				double e2y = e2.getY();
				double e2r = e2.getR();
				
				double distx = e1x - e2x;
				double disty = e1y - e2y;
				double dist = Math.sqrt(distx * distx + disty * disty);
				
				if(dist < e1r + e2r && i != j)
				{
					e1.setDir(-e1.getDx(), -e1.getDy());
				}
			}
		}
	}
	
	private void bulletEnemyCollision()
	{
		for (int i = 0; i < bullets.size(); i++)
		{
			Bullet b = bullets.get(i);
			double bx = b.getX();
			double by = b.getY();
			double br = b.getR();

			for (int j = 0; j < enemies.size(); j++)
			{
				Enemy e = enemies.get(j);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double distx = bx - ex;
				double disty = by - ey;
				double dist = Math.sqrt(distx * distx + disty * disty);

				if (dist < br + er)
				{
					e.hit();
					bullets.remove(i);
					i--;
					
					if (e.isDead())
					{
						killEnemy(j);
						j--;
						
						e.explode();
					}
					break;
				}
			}
		}
	}	
	
	private void playerEnemyCollision()
	{
		if (!player.isRecovering())
		{
			int px = player.getX();
			int py = player.getY();
			int pr = player.getR();

			for (int i = 0; i < enemies.size(); i++)
			{
				Enemy e = enemies.get(i);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double distx = px - ex;
				double disty = py - ey;
				double dist = Math.sqrt(distx * distx + disty * disty);

				if (dist <= pr + er)
				{
					player.loseLife();
					killEnemy(i);
					
					if(player.isDead())
					{
						running = false;
					}
					break;
				}
			}
		}
	}	
	
	private void playerPowerUpCollision()
	{
		int px = player.getX();
		int py = player.getY();
		int pr = player.getR();
		for (int i = 0; i < powerups.size(); i++)
		{
			PowerUp pu = powerups.get(i);
			double pux = pu.getX();
			double puy = pu.getY();
			double pur = pu.getR();

			double distx = px - pux;
			double disty = py - puy;
			double dist = Math.sqrt(distx * distx + disty * disty);

			if (dist <= pur + pr)
			{
				if(pu.getType() == PowerUp.PowerUpType.REPAIR)
				{
					player.repair(1);
				} 
				else if(pu.getType() == PowerUp.PowerUpType.POWERUP)
				{
					player.upgradeWeapon();
				}
				else if(pu.getType() == PowerUp.PowerUpType.SHIELDUPGRADE)
				{
					player.upgradeShield();
				}
				texts.add(new Text(player.getX() + 20, player.getY() - 20, 3000, "+Repair"));
				powerups.remove(i);
				i--;
			}
		}
	}	

	private void killEnemy(int ind)
	{
		Enemy e = enemies.get(ind);
		// chance for power
		double rand = Math.random();
		if (rand < 0.05)
		{
			powerups.add(new PowerUp(
					PowerUp.PowerUpType.REPAIR, e.getX(),
					e.getY()));
		}
		player.addScore(e.getRank() + e.getType());
		
		explosions.add(new Explosion(e.getX(), e.getY(), e.getR(), e.getR()*3));
		
		enemies.remove(ind);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}