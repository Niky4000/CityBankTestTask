package ru.city.citybanktesttask.bean;

public class ReportBean {

	private final String subscriberName;
	private final String ccyPair;
	private final Double currentRate;

	public ReportBean(String subscriberName, String ccyPair, Double currentRate) {
		this.subscriberName = subscriberName;
		this.ccyPair = ccyPair;
		this.currentRate = currentRate;
	}

	public String getSubscriberName() {
		return subscriberName;
	}

	public String getCcyPair() {
		return ccyPair;
	}

	public Double getCurrentRate() {
		return currentRate;
	}
}
