package application.vo;

public class CarVO {
	private String carId;
	private String carStatus;
	private Double destLati;
	private Double destLong;
	private Double distance;
	
	public CarVO() {
		super();
	}
	public CarVO(String carId, String carStatus, Double destLati, Double destLong) {
		super();
		this.carId = carId;
		this.carStatus = carStatus;
		this.destLati = destLati;
		this.destLong = destLong;
	}
	
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getCarStatus() {
		return carStatus;
	}
	public void setCarStatus(String carStatus) {
		this.carStatus = carStatus;
	}
	public Double getDestLati() {
		return destLati;
	}
	public void setDestLati(Double destLati) {
		this.destLati = destLati;
	}
	public Double getDestLong() {
		return destLong;
	}
	public void setDestLong(Double destLong) {
		this.destLong = destLong;
	}
	@Override
	public String toString() {
		return "Car [carId=" + carId + ", carStatus=" + carStatus + ", destLati=" + destLati + ", destLong=" + destLong
				+ "]";
	}
	
	
}
