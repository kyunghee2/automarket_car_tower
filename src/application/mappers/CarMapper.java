package application.mappers;

import java.util.List;

import application.vo.CarVO;

public interface CarMapper {
	public List<CarVO> getAllCars();
	
	public CarVO getCarSel();
}
