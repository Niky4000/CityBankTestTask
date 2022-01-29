package ru.city.citybanktesttask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static ru.city.citybanktesttask.CcyPairsTestGenerator.EURRUB;
import static ru.city.citybanktesttask.CcyPairsTestGenerator.EURUSD;
import static ru.city.citybanktesttask.CcyPairsTestGenerator.USDJPY;

public class CcyPairsTestGeneratorForTesting {

	private final PriceThrottler priceThrottler;
	private static final Set<String> availableCcy = new HashSet<>(Arrays.asList("EURUSD", "EURRUB", "USDJPY"));

	public CcyPairsTestGeneratorForTesting(PriceThrottler priceThrottler) {
		this.priceThrottler = priceThrottler;
	}

	public Map<String, Double> startGeneration() {
		Map<String, Double> lastValuesMap = new HashMap<>(); // We'll save last values we have sent in this map.
		for (int i = 0; i < 40; i++) {
			int randomValue = Double.valueOf(Math.random() * 100).intValue();
			double randomRate = Math.random() * 100;
			if (randomValue < 40) {
				priceThrottler.onPrice(EURUSD, randomRate);
				System.out.println(EURUSD + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
				lastValuesMap.put(EURUSD, randomRate);
			} else if (randomValue >= 40 && randomValue < 90) {
				priceThrottler.onPrice(EURRUB, randomRate);
				System.out.println(EURRUB + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
				lastValuesMap.put(EURRUB, randomRate);
			} else {
				priceThrottler.onPrice(USDJPY, randomRate);
				System.out.println(USDJPY + " = " + randomRate + " time = " + new SimpleDateFormat("ss_SSS").format(new Date()));
				lastValuesMap.put(USDJPY, randomRate);
			}
			// We won't wait here at all!
		}
		priceThrottler.shutdown();
		return lastValuesMap;
	}
}
