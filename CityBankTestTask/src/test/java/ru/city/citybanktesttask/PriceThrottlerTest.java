package ru.city.citybanktesttask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.city.citybanktesttask.bean.ReportBean;

/**
 * It's a little bit changed PriceThrottler. The main idea here in changed
 * synchronization - we should wait for all threads to check and compare
 * returned values with each other.
 */
public class PriceThrottlerTest extends PriceThrottler {

	private List<Thread> threadList = Collections.synchronizedList(new ArrayList<>());

//	final Thread mainThread;
	final Exchanger<ReportBean> exchanger = new Exchanger<>();
	final LinkedBlockingQueue<ReportBean> linkedBlockingQueue = new LinkedBlockingQueue<>();
	final AtomicBoolean stop = new AtomicBoolean(false);

	public PriceThrottlerTest() {
//		mainThread = new Thread(() -> {
//			while (true) {
//				try {
////					ReportBean take = linkedBlockingQueue.take();
//					ReportBean take = exchanger.exchange(null);
//					synchronized (priceProcessorSet) {
//						CountDownLatch countDownLatch = new CountDownLatch(1);
//						priceProcessorSet.iterator().forEachRemaining(priceProcessor -> {
//							Thread thread = new Thread(() -> {
//								while (true) {
//									try {
//										countDownLatch.await();
//										break;
//									} catch (InterruptedException ex) {
//										Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
//									}
//								}
//								priceProcessor.onPrice(take.getCcyPair(), take.getCurrentRate());
//							});
//							thread.start();
//							threadList.add(thread);
//						});
//						countDownLatch.countDown();
//					}
//				} catch (InterruptedException ex) {
//					if (stop.get()) {
//						break;
//					}
//				}
//			}
//		});
//		mainThread.setName("mainThread");
//		mainThread.start();
	}

	@Override
	public void onPrice(String ccyPair, double rate) {
		//		linkedBlockingQueue.add(new ReportBean(null, ccyPair, rate));
//		while (true) {
//			try {
//				exchanger.exchange(new ReportBean(null, ccyPair, rate));
//				break;
//			} catch (InterruptedException ex) {
//				Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}
		priceProcessorSet.iterator().forEachRemaining(priceProcessor -> priceProcessor.onPrice(ccyPair, rate));
	}

	@Override
	public void shutdown() {
		super.shutdown();
//		while (!linkedBlockingQueue.isEmpty()) {
//			try {
//				Thread.sleep(1000L);
//			} catch (InterruptedException ex) {
//				Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}
//		stop.set(true);
//		mainThread.interrupt();
//		threadList.forEach(thread -> {
//			while (true) {
//				try {
//					thread.join();
//					break;
//				} catch (InterruptedException ex) {
//					Logger.getLogger(PriceThrottlerTest.class.getName()).log(Level.SEVERE, null, ex);
//				}
//			}
//		});
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
