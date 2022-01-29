package ru.city.citybanktesttask;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import ru.city.citybanktesttask.bean.ReportBean;

/**
 * This is the main common implementation of Subscriber. Subscribers are very
 * similar and differ only in handling time.
 */
public abstract class Subscriber implements PriceProcessor {

	private static final Long MILLISECONDS_IN_SECOND = 1000L;
	final String name;
	final AtomicBoolean workIsInProgress = new AtomicBoolean(false);
	final ConcurrentHashMap<String, AtomicReference<Double>> rateValue = new ConcurrentHashMap<>();
	final Set<String> ccyPairSet = new LinkedHashSet<>();
	final Consumer<ReportBean> dataConsumer;

	public Subscriber(String name, Consumer<ReportBean> dataConsumer) {
		this.name = name;
		this.dataConsumer = dataConsumer;
	}

	@Override
	public void onPrice(String ccyPair, double rate) {
		synchronized (ccyPairSet) { // This matters in concept of happens before.
			ccyPairSet.add(ccyPair); // We should synchronize these variables because in other case we are able to miss last values.
			rateValue.putIfAbsent(ccyPair, new AtomicReference<>());
			rateValue.get(ccyPair).set(rate); // 6) It is important not to miss rarely changing prices. I.e. it is important to deliver EURRUB if it ticks once per day but you may skip some EURUSD ticking every second
		}
		Double currentRate;
		if (!workIsInProgress.getAndSet(true)) {
			while (true) {
				String currentCcyPair;
				synchronized (ccyPairSet) {
					if (!ccyPairSet.isEmpty()) {
						currentCcyPair = ccyPairSet.iterator().next(); // Iterator is not thread safe, we'll synchronize it.
					} else {
						workIsInProgress.set(false);
						break; // If we take all the values, current thread will be finished.
					}
				}
				currentRate = rateValue.get(currentCcyPair).get();
				try {
					doSomeWork(currentRate, getWorkingTime());  // Let's assume that we are doing some operations that may throw some exceptions.
				} finally {
					workIsInProgress.set(false);
				}
				printTheReport(currentCcyPair, currentRate);
				synchronized (ccyPairSet) {
					Double lastValue = rateValue.get(currentCcyPair).get(); // If we take this value earlie outside of synchonized section, we may take old value and it may coincide with the current value. We'll get the wrong result in this case and may miss the last rate value.
					if (lastValue.equals(currentRate)) { // 5) ONLY LAST PRICE for each ccyPair matters for subscribers. I.e. if a slow subscriber is not coping with updates for EURUSD - it is only important to deliver the latest rate.
						ccyPairSet.remove(currentCcyPair);
						if (ccyPairSet.isEmpty()) { // If we don't check it, other thread may be finished and we'll miss the last rate.
							workIsInProgress.set(false);
							break; // If we take all the values, current thread will be finished.
						}
					}
				}
			}
		}
	}

	@Override
	public void subscribe(PriceProcessor priceProcessor) {
		priceProcessor.subscribe(priceProcessor);
	}

	@Override
	public void unsubscribe(PriceProcessor priceProcessor) {
		priceProcessor.unsubscribe(priceProcessor);
	}

	protected void doSomeWork(Double currentRate, Integer secondsToWait) {
		try {
			Thread.sleep(secondsToWait.longValue() * MILLISECONDS_IN_SECOND);
		} catch (InterruptedException ex) {
			// We'll just eat this exception. It's made for testing purpose only.
		}
	}

	protected void printTheReport(String ccyPair, Double currentRate) {
		dataConsumer.accept(new ReportBean(name, ccyPair, currentRate));
	}

	protected abstract int getWorkingTime();

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Subscriber other = (Subscriber) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}
}
