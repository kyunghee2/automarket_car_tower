package application.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.vo.CarVO;

public interface CarMapper {
	public List<CarVO> getAllCars();
	
	public CarVO getCarSel();
	
	public int setCarStatus(HashMap<String, Object> map);
}
