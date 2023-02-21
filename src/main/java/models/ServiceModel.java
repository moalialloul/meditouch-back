package models;

import Enums.CurrencyUnits;

public class ServiceModel {
	int serviceId;
	int businessAccountFk;
	double servicePrice;
	String serviceName;
	CurrencyUnits currencyUnit;

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getBusinessAccountFk() {
		return businessAccountFk;
	}

	public void setBusinessAccountFk(int businessAccountFk) {
		this.businessAccountFk = businessAccountFk;
	}

	public ServiceModel(int serviceId, int businessAccountFk, double servicePrice, String serviceName,
			CurrencyUnits currencyUnit) {
		super();
		this.serviceId = serviceId;
		this.businessAccountFk = businessAccountFk;
		this.servicePrice = servicePrice;
		this.serviceName = serviceName;
		this.currencyUnit = currencyUnit;
	}
	
	public ServiceModel(int businessAccountFk, double servicePrice, String serviceName,
			CurrencyUnits currencyUnit) {
		super();
		this.businessAccountFk = businessAccountFk;
		this.servicePrice = servicePrice;
		this.serviceName = serviceName;
		this.currencyUnit = currencyUnit;
	}

	public ServiceModel() {

	}

	@Override
	public String toString() {
		return "\"serviceId\":" + serviceId + ", \"businessAccountFk\":" + businessAccountFk + ", \"servicePrice\":"
				+ servicePrice + ", \"serviceName\":\"" + serviceName + "\" , \"currencyUnit\":\"" + currencyUnit
				+ "\"}";
	}

	public CurrencyUnits getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnits currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public double getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(double servicePrice) {
		this.servicePrice = servicePrice;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
