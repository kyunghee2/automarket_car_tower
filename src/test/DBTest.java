package test;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import application.service.CarService;
import application.vo.CarVO;

public class DBTest {
	public DBTest() {
		BasicConfigurator.configure();
	}
	
	//@Test
	public void CarSelectjdbc() {
		
		CarService service = new CarService();
		//List<CarVO> list = service.getAllUsers();
		List<CarVO> list = service.getAllUsers();
		for(CarVO vo : list) {
			System.out.println(vo.getCarId()+" "+vo.getCarStatus());
		}
	}
	@Test
	public void setCarStatus() {
		
		CarService service = new CarService();
//		CarVO vo = new CarVO();
//		vo.setCarId("1");
//		vo.setCarStatus("01");
		HashMap<String,Object> map =new HashMap<String,Object>();
		map.put("carid", "1");
		map.put("carstatus", "01");
		int result = service.setCarStatus(map);
		System.out.println("result:"+result);
	}
}
