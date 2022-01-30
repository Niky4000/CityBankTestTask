package ru.city.citybanktesttask;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PriceThrottler implements PriceProcessor { // 1) Implement PriceProcessor interface

	Set<PriceProcessor> priceProcessorSet = new CopyOnWriteArraySet<>();
	ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	public void onPrice(String ccyPair, double rate) {
		// No synchronization is needed while traversing the iterator. It's thread safe.
		// 2) Distribute updates to its listeners which are added through subscribe() and removed through unsubscribe().
		priceProcessorSet.iterator().forEachRemaining(priceProcessor -> priceProcessor.onPrice(ccyPair, rate));
	}

	@Override
	public void subscribe(PriceProcessor priceProcessor) {
		priceProcessorSet.add(priceProcessor);
	}

	@Override
	public void unsubscribe(PriceProcessor priceProcessor) {
		priceProcessorSet.remove(priceProcessor);
	}

	public void shutdown() {
		executorService.shutdown();
	}
}
