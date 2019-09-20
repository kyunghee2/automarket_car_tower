package application;

public class Car {
	private String carid;
	private String carnm;
	public String getCarid() {
		return carid;
	}
	public void setCarid(String carid) {
		this.carid = carid;
	}
	public String getCarnm() {
		return carnm;
	}
	public void setCarnm(String carnm) {
		this.carnm = carnm;
	}
	@Override
	public String toString() {
		return "Car [carid=" + carid + ", carnm=" + carnm + "]";
	}
	
}
