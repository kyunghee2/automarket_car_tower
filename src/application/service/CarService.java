package application.service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import application.mappers.CarMapper;
//import util.JDBCUtil;
import application.vo.CarVO;
import util.LocationUtil;

public class CarService {

	public List<CarVO> getAllUsers() {
		
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
			return carMapper.getAllCars();

		} finally {
			sqlSession.close();
		}

	}
	//차량선택
	public CarVO getCarSel(Double targetLati,Double targetLong) {
				
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
			
			List<CarVO> list = carMapper.getAllCars();
			Double carLati=0.0,carLong=0.0,distance=0.0;
			for(CarVO c: list) {
				carLati = c.getDestLati();
				carLong = c.getDestLong();
				c.setDistance(LocationUtil.distance(targetLati,targetLong,carLati,carLong,"meter"));
			}
			Comparator<CarVO> comparator = Comparator.comparingDouble( CarVO::getDistance );
			CarVO returnVO = list.stream().sorted().min(comparator).get();
			return returnVO;
			//return carMapper.getCarSel();

		} finally {
			sqlSession.close();
		}

	}

	public int setCarStatus(HashMap<String, Object> map) {
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
			return carMapper.setCarStatus(map);

		} finally {
			sqlSession.commit();
			sqlSession.close();
		}
	}

}
