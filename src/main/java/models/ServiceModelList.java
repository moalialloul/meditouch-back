package models;

import java.util.List;

public class ServiceModelList {
	private List<ServiceModel> serviceModels;

	public ServiceModelList(List<ServiceModel> serviceModels) {
		this.serviceModels = serviceModels;
	}

	public List<ServiceModel> getServiceModels() {
		return serviceModels;
	}

	public void setServiceModels(List<ServiceModel> serviceModels) {
		this.serviceModels = serviceModels;
	}
}
