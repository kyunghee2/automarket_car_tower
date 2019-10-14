package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import application.service.CarService;
import application.vo.CarVO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

public class CarTowerController implements Initializable {
	@FXML
	private Button btnServerStart;
	@FXML
	private Button btnServerStop;
	@FXML
	private Button btnRecovery;
	@FXML
	private Button btnUserDisc;
	@FXML
	private ListView<Car> lvCar;
	@FXML
	private ListView<User> lvUser;
	@FXML
	private TextArea textarea;
	@FXML
	private TableView<CarDetail> tableCarDetail;

	ServerSocket server;
	ExecutorService executorService = Executors.newCachedThreadPool();
	SharedObject sharedObject = new SharedObject();

	private ObservableList<Car> lvCarData = FXCollections.observableArrayList();
	private ObservableList<User> lvUserData = FXCollections.observableArrayList();
	private ObservableList<CarDetail> lvCarDetailData = FXCollections.observableArrayList();
	private String selected_carid, selected_userid;

	public CarTowerController() {
		BasicConfigurator.configure();// log4j 기본설정
	}

	class SharedObject {
		List<ClientRunnable> clients = new ArrayList<ClientRunnable>();
		List<ClientRunnable> users = new ArrayList<ClientRunnable>();
		List<ClientRunnable> cars = new ArrayList<ClientRunnable>();
		List<ClientRunnable> car_tablets = new ArrayList<ClientRunnable>();
		ObjectMapper objectMapper = new ObjectMapper();

		public SharedObject() {
		}

		public void broadcast(String msg) {
			clients.stream().forEach(t -> {
				t.out.println(msg);
				t.out.flush();
			});

		}
		// 차량 테블릿 소겟저장
		public synchronized void carTabletAdd(ClientRunnable runnable) {
			
			long cnt = car_tablets.stream().filter(f->f.id.equals(runnable.id)&&f.type.equals(runnable.type)).count();
			if (cnt == 0) {
				car_tablets.add(runnable);
			}
				
		}
		// 차량 소겟저장
		public synchronized void carAdd(ClientRunnable runnable) {
			
			long cnt = cars.stream().filter(f->f.id.equals(runnable.id)&&f.type.equals(runnable.type)).count();
			if (cnt == 0) {
				cars.add(runnable);
				currentThread(() -> {
					lvCarData.add(new Car(runnable.id, runnable.id));
				});
			}
				
		}

		// 해당차량에 메시지 전송
		public void carSendMsg(String carid, String msg) {

			cars.stream().filter(t -> t.id.equals(carid)).forEach(f -> {
				f.out.println(msg);
				f.out.flush();
			});

		}

		// 사용자 접속해제
		public synchronized void userDisconnect(String userid) {
			int index = -1, i = 0;
			for (ClientRunnable c : users) {
				if (c.id.equals(userid)) {
					index = i;
				}
				i++;
			}

			users.stream().filter(f -> f.id.equals(userid)).forEach(t -> {
				// 소켓연결해제
				t.currentExit();

			});
			users.remove(index);
			lvUser.getItems().remove(index);

		}

		// 사용자 소켓저장
		public synchronized void userAdd(ClientRunnable runnable) {
			
			long cnt = users.stream().filter(f->f.id.equals(runnable.id)&&f.type.equals(runnable.type)).count();
			if (cnt == 0) {
				users.add(runnable);
				
				currentThread(() -> {
					lvUserData.add(new User(runnable.id, runnable.id));
				});
			}
			
		}

		// 해당 사용자에게 메세지 전송
		public void userSendMsg(String userid, String msg) {

			users.stream().filter(t -> t.id.equals(userid)).forEach(f -> {
				f.out.println(msg);
				f.out.flush();
			});

		}

	}

	class ClientRunnable implements Runnable {
		private SharedObject sharedObject; // 공유객체
		private Socket socket; // 클라이언트와 연결된 socket
		private BufferedReader br;
		private PrintWriter out;
		private String id;
		private String type;

		public ClientRunnable(SharedObject sharedObject, Socket socket) {
			super();
			this.sharedObject = sharedObject;
			this.socket = socket;
			try {
				this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = new PrintWriter(socket.getOutputStream());

			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
		}

		@Override
		public void run() {
			String msg = "", carid = "", userid = "", json = "", ttype="";
			Double carLati=0.0,carLong=0.0;
			String[] arr_msg;
			ObjectMapper objectMapper = new ObjectMapper();
			CarService service = new CarService();
			
			try {
				while ((msg = br.readLine()) != null) {

					if (msg.contains("/EXIT/")) {
						currentExit();

						break;
					}else if (msg.contains("/10000000/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 3) {
							printMsg("car 차량등록 :형식이 맞지 않습니다.예)C/10000000/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							carid = arr_msg[2];
							ttype = arr_msg[0];
							
							this.id = carid;
							this.type = ttype;
							if(ttype.equals("C")) {
								sharedObject.carAdd(this);

								printMsg("car 차량등록 carid:" + carid);
								
							}else if(ttype.equals("T")) {
								sharedObject.carTabletAdd(this);
								printMsg("car Tablet 등록 carid:" + carid);
							}							
							sharedObject.clients.remove(this);

						}
						
					} else if (msg.contains("/10000001/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 3) {
							printMsg("tower->car 차량회수 요청 :형식이 맞지 않습니다.예)C/10000001/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							carid = arr_msg[2];
							printMsg("tower->car 차량회수 요청 carid:" + carid);

							sharedObject.carSendMsg(carid, "/10000001/" + carid);
						}
						
					} else if (msg.contains("/10000002/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 3) {
							printMsg("car -> tower 회수처리 완료 :형식이 맞지 않습니다.예)C/10000002/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							carid = arr_msg[2];
							//CarService service = new CarService();
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("carid", carid);
							map.put("carstatus", "04");
							int result = service.setCarStatus(map);

							printMsg("car -> tower 회수처리 완료 carid:" + carid + " result:" + result);
						}
						
					} else if (msg.contains("/10000100/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 3) {
							printMsg("app -> tower 사용자 등록 :형식이 맞지 않습니다.예)U/10000100/UserId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							userid = arr_msg[2];
							this.id = userid;
							this.type = "U";
							sharedObject.userAdd(this);
							sharedObject.clients.remove(this);

							printMsg("app -> tower 사용자 등록 userid:" + userid);
						}
						
					} else if (msg.contains("/10000101/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 5) {
							printMsg("app -> tower 자동차 선정 :형식이 맞지 않습니다.예)U/10000101/UserId/latitude/longitude");
							printMsg("arr_msg:" + arr_msg);

						} else {
							userid = arr_msg[2];
							carLati = Double.parseDouble(arr_msg[3]);
							carLong = Double.parseDouble(arr_msg[4]);
							
							//CarService service = new CarService();
							CarVO vo = service.getCarSel(carLati,carLong);
							if(vo == null)
								sharedObject.userSendMsg(userid, "/10000101/-1");
							else
								sharedObject.userSendMsg(userid, "/10000101/" + vo.getCarId()+"/"+vo.getDestLati()+"/"+vo.getDestLong());
							printMsg("app -> tower 자동차 선정 userid:" + userid+" carid:"+vo.getCarId());
						}						
						
					} else if (msg.contains("/10000102/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 4) {
							printMsg("app -> tower 자동차 이동요청 :형식이 맞지 않습니다.예)U/10000102/UserId/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							userid = arr_msg[2];
							carid = arr_msg[3];
							//CarService service = new CarService();
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("carid", carid);
							map.put("carstatus", "01");
							int result = service.setCarStatus(map);
							printMsg("app -> tower 자동차 이동요청 userid:" + userid + " carid:" + carid + " result:" + result);
						}

					} else if (msg.contains("/10000103/")) {
						arr_msg = msg.split("/");						
						if (arr_msg.length < 3) {
							printMsg("car -> tower 이동완료 :형식이 맞지 않습니다.예)C/10000103/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							carid = arr_msg[2];
							//CarService service = new CarService();
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("carid", carid);
							map.put("carstatus", "02");
							int result = service.setCarStatus(map);

							printMsg("car -> tower 이동완료 carid:" + carid + " result:" + result);
						}
						

					} else if (msg.contains("/10000104/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 4) {
							printMsg("tablet -> tower 제품수령완료 :형식이 맞지 않습니다.예)U/10000104/UserId/CarId");
							printMsg("arr_msg:" + arr_msg);

						} else {
							userid = arr_msg[2];
							carid = arr_msg[3];
							//CarService service = new CarService();
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("carid", carid);
							map.put("carstatus", "00");
							int result = service.setCarStatus(map);

							printMsg("tablet -> tower 제품수령완료 carid:" + carid + " userid:"+userid+" result:" + result);

						}
						
					}else if (msg.contains("/10000202/")) {
						arr_msg = msg.split("/");
						if (arr_msg.length < 4) {
							printMsg("car -> tower 차량정보 저장 요청 :형식이 맞지 않습니다.예)C/10000202/CarId/{JSON Data}");
							printMsg("arr_msg:" + arr_msg);

						} else {
							carid = arr_msg[2];
							json = arr_msg[3];
							CarDetail cardetail = objectMapper.readValue(json, new TypeReference<CarDetail>() {});
							cardetail.setCarid(carid);
							// DB저장					
							int result = service.setCarInsert(cardetail);
							
							// TABLE VIEW 표시
							int findidx = -1, i = 0;
							for (CarDetail c : lvCarDetailData) {
								if (c.getCarid().equals(carid)) {
									findidx = i;
								}
								i++;
							}
							if (findidx == -1)
								lvCarDetailData.add(cardetail);
							else
								lvCarDetailData.set(findidx, cardetail);

							printMsg("car -> tower 차량정보 저장 요청 result:"+result);

						}

					}
					

				}
			} catch (IOException e) {
				printMsg("연결해제 userid:" + this.id + " type:" + this.type);

			} catch (Exception e) {
				System.out.println(e.getStackTrace());
			}
		}

		public void currentExit() {
			printMsg("연결해제 userid:" + this.id + " type:" + this.type);
			try {
				br.close();
				socket.close();
				
				if(this.type.equals("U")) {
					sharedObject.users.removeIf(t->t.id.equals(this.id)&&t.type.equals(this.type));
				}else if(this.type.equals("C")) {
					sharedObject.cars.removeIf(t->t.id.equals(this.id)&&t.type.equals(this.type));
				}else if(this.type.equals("T")) {
					sharedObject.users.removeIf(t->t.id.equals(this.id)&&t.type.equals(this.type));
				}				
				
			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
			if (!Thread.currentThread().isInterrupted())
				Thread.currentThread().interrupt();
		}

	}

	private void printMsg(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}

	private void currentThread(Runnable runnable) {
		Platform.runLater(runnable);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnServerStart.setOnAction(e -> {
			textarea.clear();
			printMsg("[채팅서버기동 - 6789]");
			// 서버소캣을 만들어서 클라이언트 접속을 대기
			// JavaFx thread가 blocking되지 않도록 새로운 Thread를 만들어서 클라이언트 접속 대기해야 함
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						server = new ServerSocket(6789);
						while (true) {
							printMsg("[클라이언트 접속 대기중]");
							Socket socket = server.accept();
							printMsg("[클라이언트 접속 성공]");

							ClientRunnable cRunnable = new ClientRunnable(sharedObject, socket);
							sharedObject.clients.add(cRunnable);
							printMsg("[현재 클라이언트 수]:" + sharedObject.clients.size());
							printMsg("Car:" + sharedObject.cars.size() + " User:" + sharedObject.users.size());
							executorService.execute(cRunnable);

						}

					} catch (Exception e) {
						System.out.println(e.getStackTrace());
					}
				}
			};
			executorService.execute(runnable);

		});
		btnServerStop.setOnAction(e -> {
			try {
				server.close();
			} catch (IOException ex) {
			}
			executorService.shutdownNow();
		});
		btnRecovery.setOnAction(e -> {
			// 차량회수

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("확인");
			alert.setHeaderText(null);
			alert.setContentText("선택한 [" + selected_carid + "]차량 회수 요청하시겠습니까?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				sharedObject.carSendMsg(selected_carid, "/10000001/" + selected_carid);
			} else {

			}
		});
		btnUserDisc.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("확인");
			alert.setHeaderText(null);
			alert.setContentText("선택한 [" + selected_userid + "]사용자 접속 해제하시겠습니까?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				sharedObject.userDisconnect(selected_userid);
			} else {

			}
		});

		lvCar.setItems(lvCarData);
		lvCar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Car>() {
			@Override
			public void changed(ObservableValue<? extends Car> observable, Car oldValue, Car newValue) {
				selected_carid = newValue.getCarid().toString();
			}
		});
		lvUser.setItems(lvUserData);
		lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {

			@Override
			public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
				selected_userid = newValue.getUserid().toString();
			}
		});
		
		//table view 설정
		tableCarDetail.setItems(lvCarDetailData);
		tableViewInit(tableCarDetail);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void tableViewInit(TableView<CarDetail> tb) {
		TableColumn col_carid = new TableColumn("Car Id");
		col_carid.setCellValueFactory(new PropertyValueFactory("carid"));
		TableColumn col_startflag = new TableColumn("시동");
		col_startflag.setCellValueFactory(new PropertyValueFactory("startflag"));
		TableColumn col_battery = new TableColumn("배터리");
		col_battery.setCellValueFactory(new PropertyValueFactory("battery"));
		TableColumn col_temp = new TableColumn("온도");
		col_temp.setCellValueFactory(new PropertyValueFactory("temp"));
		TableColumn col_status = new TableColumn("상태");
		col_status.setCellValueFactory(new PropertyValueFactory("carstatus"));
		TableColumn col_lati = new TableColumn("위도");
		col_lati.setCellValueFactory(new PropertyValueFactory("latitude"));
		TableColumn col_long = new TableColumn("경도");
		col_long.setCellValueFactory(new PropertyValueFactory("longitude"));
		TableColumn col_error = new TableColumn("에러");
		col_error.setCellValueFactory(new PropertyValueFactory("error"));

		tableCarDetail.getColumns().setAll(col_carid, col_startflag,col_battery,col_temp,col_status,col_lati,col_long,col_error);
	}

}
