package application.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.CarDetail;
import application.vo.CarVO;

public interface CarMapper {
	public List<CarVO> getAllCars();
	
	public CarVO getCarSel();
	
	public int setCarStatus(HashMap<String, Object> map);
	//차량정보 저장
	public int setCarUpdate(CarDetail map);
	//차량로그 저장
	public int setCarLogInsert(CarDetail map);
}
