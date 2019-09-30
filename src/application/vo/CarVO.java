package application.vo;

public class CarVO {
	private String carId;
	private String carStatus;
	private Double destLati;
	private Double destLong;
	private Integer temp;
	private Integer battery;
	private String carStart;
	private String carError;
	private Double distance;//거리값
	
	public CarVO() {
		super();
	}	
	public CarVO(String carId, String carStatus, Double destLati, Double destLong, Integer temp, Integer battery,
			String carStart, String carError) {
		super();
		this.carId = carId;
		this.carStatus = carStatus;
		this.destLati = destLati;
		this.destLong = destLong;
		this.temp = temp;
		this.battery = battery;
		this.carStart = carStart;
		this.carError = carError;
		//this.distance = distance;
	}
	public Integer getTemp() {
		return temp;
	}
	public void setTemp(Integer temp) {
		this.temp = temp;
	}
	public Integer getBattery() {
		return battery;
	}
	public void setBattery(Integer battery) {
		this.battery = battery;
	}
	public String getCarStart() {
		return carStart;
	}
	public void setCarStart(String carStart) {
		this.carStart = carStart;
	}
	public String getCarError() {
		return carError;
	}
	public void setCarError(String carError) {
		this.carError = carError;
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
