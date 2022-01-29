package ru.city.citybanktesttask;

import java.util.function.Consumer;
import ru.city.citybanktesttask.bean.ReportBean;

public class VeryFastSubscriber extends Subscriber implements PriceProcessor { // 3) Some subscribers are very fast

	private static final int WORKING_TIME_OF_SLOW_SUBSCRIBER = 1; // It can be changed.

	public VeryFastSubscriber(String name, Consumer<ReportBean> dataConsumer) {
		super(name, dataConsumer);
	}

	@Override
	protected int getWorkingTime() {
		return WORKING_TIME_OF_SLOW_SUBSCRIBER;
	}
}
