package application.service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import application.mappers.CarMapper;
//import util.JDBCUtil;
import application.vo.CarVO;

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
	public CarVO getCarSel() {
		
		SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
		try {
			CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
			return carMapper.getCarSel();

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
			sqlSession.close();
		}
	}

}
