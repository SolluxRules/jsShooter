public class StatManager
{
	private int enemiesKilled;
	private int BulletsShot;
	private int Hulldamaged;
	
	private long lastPowerUpTime;
	
	private int score;
	
	public StatManager()
	{
		enemiesKilled = 0;
		BulletsShot = 0;
		Hulldamaged = 0;
		
		lastPowerUpTime = -1;
		score = 0;
	}

	public long getLastPowerUpTime()
	{
		return lastPowerUpTime;
	}

	public void gottenPowerUp()
	{
		lastPowerUpTime = System.nanoTime();
	}
}