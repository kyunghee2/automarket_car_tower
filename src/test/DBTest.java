package test;

import java.util.List;

import org.junit.Test;

import application.service.CarService;
import application.vo.CarVO;

public class DBTest {
	
	@Test
	public void CarSelectjdbc() {
		CarService service = new CarService();
		//List<CarVO> list = service.getAllUsers();
//		List<CarVO> list = service.getCarSel();
//		for(CarVO vo : list) {
//			System.out.println(vo.getCarId()+" "+vo.getCarStatus());
//		}
	}
}
