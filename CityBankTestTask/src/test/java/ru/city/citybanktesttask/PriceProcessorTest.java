package ru.city.citybanktesttask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import ru.city.citybanktesttask.bean.ReportBean;

/**
 * We test here that everything works fine.
 */
public class PriceProcessorTest {

	private static final int AMOUNT_OF_FAST_SUBSCRIBERS = 8;
	private static final int AMOUNT_OF_SLOW_SUBSCRIBERS = 0;

	private static final List<ReportBean> reportBeanListForFastSubscribers = Collections.synchronizedList(new ArrayList<>());
	private static final List<ReportBean> reportBeanListForSlowSubscribers = Collections.synchronizedList(new ArrayList<>());

	@Test
	public void test() {
		for (int j = 0; j < 100; j++) {
			PriceProcessorTest priceProcessorTest = new PriceProcessorTest();
			PriceThrottler priceThrottler = new PriceThrottlerTest();
			List<String> fastSubscribersNameList = new ArrayList<>(AMOUNT_OF_FAST_SUBSCRIBERS);
			for (int i = 0; i < AMOUNT_OF_FAST_SUBSCRIBERS; i++) {
				priceThrottler.subscribe(new VeryFastSubscriber("VeryFastSubscriber_" + i, priceProcessorTest::printTheReportForTheFastSubscriber));
				fastSubscribersNameList.add("VeryFastSubscriber_" + i);
			}
			List<String> slowSubscribersNameList = new ArrayList<>(AMOUNT_OF_SLOW_SUBSCRIBERS);
			for (int i = 0; i < AMOUNT_OF_SLOW_SUBSCRIBERS; i++) {
				priceThrottler.subscribe(new VerySlowSubscriber("VerySlowSubscriber_" + i, priceProcessorTest::printTheReportForTheSlowSubscriber));
				slowSubscribersNameList.add("VerySlowSubscriber_" + i);
			}
			CcyPairsTestGeneratorForTesting ccyPairsTestGenerator = new CcyPairsTestGeneratorForTesting(priceThrottler);
			Map<String, Double> lastValuesMap = ccyPairsTestGenerator.startGeneration(); // We saved the last sent values.
			// These variables store values that were received by subscribers. These variables are groupped by subscriber name and ccyPairs.
			Map<String, Map<String, List<ReportBean>>> reportBeanMapForFastSubscribers = reportBeanListForFastSubscribers.stream().collect(Collectors.groupingBy(ReportBean::getSubscriberName, Collectors.groupingBy(ReportBean::getCcyPair)));
			Map<String, Map<String, List<ReportBean>>> reportBeanMapForSlowSubscribers = reportBeanListForSlowSubscribers.stream().collect(Collectors.groupingBy(ReportBean::getSubscriberName, Collectors.groupingBy(ReportBean::getCcyPair)));
			for (Entry<String, Double> entry : lastValuesMap.entrySet()) {
				String ccyPair = entry.getKey();
				Double rate = entry.getValue();
				checkAssertion(reportBeanMapForFastSubscribers, fastSubscribersNameList, ccyPair, rate);
				checkAssertion(reportBeanMapForSlowSubscribers, slowSubscribersNameList, ccyPair, rate);
			}
		}
	}

	/**
	 * This is the main check function. Here we are checking that every
	 * subscriber has handled the last rate for every ccyPair!
	 *
	 * @param reportBeanMap - all values that were received by subscribers.
	 * @param subscribersNameList - subscriber name value.
	 * @param ccyPair - ccyPair value.
	 * @param rate - expected value of the rate that was sent last time.
	 */
	private void checkAssertion(Map<String, Map<String, List<ReportBean>>> reportBeanMap, List<String> subscribersNameList, String ccyPair, Double rate) {
		for (String name : subscribersNameList) {
			List<ReportBean> reportBeanListLocal = reportBeanMap.get(name).get(ccyPair);
			Double lastRate = reportBeanListLocal.get(reportBeanListLocal.size() - 1).getCurrentRate();
			boolean mainCondition = lastRate.equals(rate);
			if (!mainCondition) {
				System.out.println(name + " " + ccyPair + " " + lastRate + " is not equal to " + rate + "!");
			}
			Assert.assertTrue(mainCondition);
		}
	}

	private void printTheReportForTheFastSubscriber(ReportBean reportBean) {
		reportBeanListForFastSubscribers.add(reportBean); // We hook here value that fast subscriber has handled.
		System.out.println("Work is done by FAST subscriber named " + reportBean.getSubscriberName() + "! ccyPair = " + reportBean.getCcyPair() + " and rate = " + reportBean.getCurrentRate() + "!");
	}

	private void printTheReportForTheSlowSubscriber(ReportBean reportBean) {
		reportBeanListForSlowSubscribers.add(reportBean); // We hook here value that slow subscriber has handled.
		System.out.println("Work is done by SLOW subscriber named " + reportBean.getSubscriberName() + "! ccyPair = " + reportBean.getCcyPair() + " and rate = " + reportBean.getCurrentRate() + "!");
	}
}
