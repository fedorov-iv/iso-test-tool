package ru.somecompany.bankcards.loadscripts.encounter;

import java.util.Random;

public class Environment {
	
	static Random rand = new Random();
	static int maxSTAN = 999999;
	public static String ZMK = "8BFFB70AB8A6506595FAAA18395F8E4B";
	
	public static String getSTAN()
	{
		return String.format("%06d", rand.nextInt(maxSTAN));
	}

}
