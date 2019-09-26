package application;

public class Car {
	private String carid;
	private String carnm;
	private String carstatus;
	
	public Car() {
		super();
	}
	public Car(String carid, String carnm) {
		super();
		this.carid = carid;
		this.carnm = carnm;
	}
	
	public Car(String carid, String carnm, String carstatus) {
		super();
		this.carid = carid;
		this.carnm = carnm;
		this.carstatus = carstatus;
	}
	public String getCarstatus() {
		return carstatus;
	}
	public void setCarstatus(String carstatus) {
		this.carstatus = carstatus;
	}
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
