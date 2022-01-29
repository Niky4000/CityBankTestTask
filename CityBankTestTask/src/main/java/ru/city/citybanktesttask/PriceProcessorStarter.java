package ru.city.citybanktesttask;

import ru.city.citybanktesttask.bean.ReportBean;

public class PriceProcessorStarter {

	// It could be put to some configs or even changed dynamically if it's required.
	private static final int AMOUNT_OF_FAST_SUBSCRIBERS = 10;
	private static final int AMOUNT_OF_SLOW_SUBSCRIBERS = 10;

	/**
	 * It's just a demo scenario of usage.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		PriceThrottler priceThrottler = new PriceThrottler();
		// 8) You don't know in advance which of the subscribers are slow and which are fast.
		// In any case whether it's slow or fast subscriber it'll get the last value in any case and most likely some intermediate values too.
		for (int i = 0; i < AMOUNT_OF_FAST_SUBSCRIBERS; i++) {
			priceThrottler.subscribe(new VeryFastSubscriber("VeryFastSubscriber_" + i, PriceProcessorStarter::printTheReportForTheFastSubscriber));
		}
		// 9) Slow subscribers should not impact fast subscribers
		// And they won't because they are isolated from each other in different threads.
		for (int i = 0; i < AMOUNT_OF_SLOW_SUBSCRIBERS; i++) {
			priceThrottler.subscribe(new VerySlowSubscriber("VerySlowSubscriber_" + i, PriceProcessorStarter::printTheReportForTheSlowSubscriber));
		}
		CcyPairsTestGenerator ccyPairsTestGenerator = new CcyPairsTestGenerator(priceThrottler);
		ccyPairsTestGenerator.startGeneration();
	}

	/**
	 * It's made for clarity and simplification. Instead of this could be any
	 * implementation.
	 *
	 * @param reportBean
	 */
	private static void printTheReportForTheFastSubscriber(ReportBean reportBean) {
		System.out.println("Work is done by FAST subscriber named " + reportBean.getSubscriberName() + "! ccyPair = " + reportBean.getCcyPair() + " and rate = " + reportBean.getCurrentRate() + "!");
	}

	/**
	 * It's made for clarity and simplification. Instead of this could be any
	 * implementation.
	 *
	 * @param reportBean
	 */
	private static void printTheReportForTheSlowSubscriber(ReportBean reportBean) {
		System.out.println("Work is done by SLOW subscriber named " + reportBean.getSubscriberName() + "! ccyPair = " + reportBean.getCcyPair() + " and rate = " + reportBean.getCurrentRate() + "!");
	}
}
