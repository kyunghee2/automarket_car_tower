package application;

public class User {
	private String userid;
	private String usernm;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsernm() {
		return usernm;
	}
	public void setUsernm(String usernm) {
		this.usernm = usernm;
	}
	@Override
	public String toString() {
		return "User [userid=" + userid + ", usernm=" + usernm + "]";
	}
	
}
