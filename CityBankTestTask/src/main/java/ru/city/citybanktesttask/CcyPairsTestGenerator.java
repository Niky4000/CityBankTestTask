package ru.city.citybanktesttask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class imitates the datasource of rate.
 *
 * @author me
 */
public class CcyPairsTestGenerator {

	private final PriceThrottler priceThrottler;
	public static final String EURUSD = "EURUSD";
	public static final String EURRUB = "EURRUB";
	public static final String USDJPY = "USDJPY";
	private static final Set<String> availableCcy = new HashSet<>(Arrays.asList(EURUSD, EURRUB, USDJPY));

	public CcyPairsTestGenerator(PriceThrottler priceThrottler) {
		this.priceThrottler = priceThrottler;
	}

	public void startGeneration() {
		for (int i = 0; i < 40; i++) {
			int randomValue = Double.valueOf(Math.random() * 100).intValue();
			double randomRate = Math.random() * 100;
			// 7) You don't know in advance which ccyPair are frequent and which are rare. Some might become more frequent at different time of a day
			// I think that according to our implementation of Subscriber it doesn't matter. It may be eigther frequent or rare.
			if (randomValue < 40) {
				priceThrottler.onPrice(EURUSD, randomRate);
				System.out.println(EURUSD + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
			} else if (randomValue >= 40 && randomValue < 90) {
				priceThrottler.onPrice(EURRUB, randomRate);
				System.out.println(EURRUB + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
			} else {
				priceThrottler.onPrice(USDJPY, randomRate);
				System.out.println(USDJPY + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				// Eat that exception!
			}
		}
		priceThrottler.shutdown();
	}
}
