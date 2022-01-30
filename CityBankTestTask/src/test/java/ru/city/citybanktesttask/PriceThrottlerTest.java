package ru.city.citybanktesttask;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It's a little bit changed PriceThrottler. The main idea here in changed
 * synchronization - we should wait for all threads to check and compare
 * returned values with each other.
 */
public class PriceThrottlerTest extends PriceThrottler {

	@Override
	public void shutdown() {
		super.shutdown();
		priceProcessorSet.iterator().forEachRemaining(priceProcessor -> {
			if (priceProcessor instanceof Subscriber) {
				while (true) {
					try {
						((Subscriber) priceProcessor).getThread().join();
						break;
					} catch (InterruptedException ex) {
						Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});
	}
}
