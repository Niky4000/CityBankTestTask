package ru.city.citybanktesttask;

import java.util.function.Consumer;
import ru.city.citybanktesttask.bean.ReportBean;

public class VerySlowSubscriber extends Subscriber implements PriceProcessor { // 3) Some subscribers are very slow

	private static final int WORKING_TIME_OF_SLOW_SUBSCRIBER = 20; // It's really tedious to wait for 30 minutes. I decided to reduce this value a little but it does'n matter.

	public VerySlowSubscriber(String name, Consumer<ReportBean> dataConsumer) {
		super(name, dataConsumer);
	}

	@Override
	protected int getWorkingTime() {
		return WORKING_TIME_OF_SLOW_SUBSCRIBER;
	}
}
