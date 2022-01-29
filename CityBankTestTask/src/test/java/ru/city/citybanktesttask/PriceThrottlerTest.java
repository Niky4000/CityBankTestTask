package ru.city.citybanktesttask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It's a little bit changed PriceThrottler. The main idea here in changed
 * synchronization - we should wait for all threads to check and compare
 * returned values with each other.
 */
public class PriceThrottlerTest extends PriceThrottler {

	private List<Thread> threadList = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void onPrice(String ccyPair, double rate) {
		priceProcessorSet.iterator().forEachRemaining(priceProcessor -> {
			Thread thread = new Thread(() -> priceProcessor.onPrice(ccyPair, rate));
			thread.start();
			threadList.add(thread);
		});
	}

	@Override
	public void shutdown() {
		super.shutdown();
		threadList.forEach(thread -> {
			while (true) {
				try {
					thread.join();
					break;
				} catch (InterruptedException ex) {
					Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}
}
