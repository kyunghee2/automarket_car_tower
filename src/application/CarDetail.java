package application;

public class CarDetail {
	private String carid;
	private String startflag;
	private Integer battery;
	private Integer temp;
	private String carstatus;
	private Double latitude;
	private Double longitude;
	private String error;
	
	public CarDetail() {
		super();
	}

	public CarDetail(String carid, String startflag, Integer battery, Integer temp, String carstatus, Double latitude,
			Double longitude, String error) {
		super();
		this.carid = carid;
		this.startflag = startflag;
		this.battery = battery;
		this.temp = temp;
		this.carstatus = carstatus;
		this.latitude = latitude;
		this.longitude = longitude;
		this.error = error;
	}

	public String getCarid() {
		return carid;
	}

	public void setCarid(String carid) {
		this.carid = carid;
	}

	public String getStartflag() {
		return startflag;
	}

	public void setStartflag(String startflag) {
		this.startflag = startflag;
	}

	public Integer getBattery() {
		return battery;
	}

	public void setBattery(Integer battery) {
		this.battery = battery;
	}

	public Integer getTemp() {
		return temp;
	}

	public void setTemp(Integer temp) {
		this.temp = temp;
	}

	public String getCarstatus() {
		return carstatus;
	}

	public void setCarstatus(String carstatus) {
		this.carstatus = carstatus;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "CarDetail [carid=" + carid + ", startflag=" + startflag + ", battery=" + battery + ", temp=" + temp
				+ ", carstatus=" + carstatus + ", latitude=" + latitude + ", longitude=" + longitude + ", error="
				+ error + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((battery == null) ? 0 : battery.hashCode());
		result = prime * result + ((carid == null) ? 0 : carid.hashCode());
		result = prime * result + ((carstatus == null) ? 0 : carstatus.hashCode());
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((startflag == null) ? 0 : startflag.hashCode());
		result = prime * result + ((temp == null) ? 0 : temp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarDetail other = (CarDetail) obj;
		if (battery == null) {
			if (other.battery != null)
				return false;
		} else if (!battery.equals(other.battery))
			return false;
		if (carid == null) {
			if (other.carid != null)
				return false;
		} else if (!carid.equals(other.carid))
			return false;
		if (carstatus == null) {
			if (other.carstatus != null)
				return false;
		} else if (!carstatus.equals(other.carstatus))
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (startflag == null) {
			if (other.startflag != null)
				return false;
		} else if (!startflag.equals(other.startflag))
			return false;
		if (temp == null) {
			if (other.temp != null)
				return false;
		} else if (!temp.equals(other.temp))
			return false;
		return true;
	}
	
	
}
