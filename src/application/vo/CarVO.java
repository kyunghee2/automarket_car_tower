package application.vo;

public class CarVO {
	private String carId;
	private String carStatus;
	private String destLati;
	private String destLong;
	
	public CarVO() {
		super();
	}
	public CarVO(String carId, String carStatus, String destLati, String destLong) {
		super();
		this.carId = carId;
		this.carStatus = carStatus;
		this.destLati = destLati;
		this.destLong = destLong;
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
	public String getDestLati() {
		return destLati;
	}
	public void setDestLati(String destLati) {
		this.destLati = destLati;
	}
	public String getDestLong() {
		return destLong;
	}
	public void setDestLong(String destLong) {
		this.destLong = destLong;
	}
	@Override
	public String toString() {
		return "Car [carId=" + carId + ", carStatus=" + carStatus + ", destLati=" + destLati + ", destLong=" + destLong
				+ "]";
	}
	
	
}
