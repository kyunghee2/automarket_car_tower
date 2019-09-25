package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.service.CarService;
import application.vo.CarVO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class CarTowerController implements Initializable{
	@FXML
	private Button btnServerStart;
	@FXML
	private Button btnServerStop;
	@FXML
	private ListView<Car> lvCar;
	@FXML
	private ListView<User> lvUser;
	@FXML
	private TextArea textarea;
	
	ServerSocket server;
	ExecutorService executorService = Executors.newCachedThreadPool(); 
	SharedObject sharedObject = new SharedObject();
	
	private ObservableList<Car> lvCarData = FXCollections.observableArrayList();
	private ObservableList<User> lvUserData = FXCollections.observableArrayList();
	
	private String selected_carid;
	class SharedObject {
		List<ClientRunnable> clients = new ArrayList<ClientRunnable>();	
		List<ClientRunnable> users = new ArrayList<ClientRunnable>();
		List<ClientRunnable> cars = new ArrayList<ClientRunnable>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		public SharedObject(){}
		public void broadcast(String msg) {
			
			clients.stream().forEach(t-> {
				t.out.println(msg);
				t.out.flush();
			});
			
		}
		//차량등록
		public synchronized void carAdd(ClientRunnable runnable) {
			boolean exists =false;
			for(ClientRunnable c: cars) {
				if(c.id==runnable.id && c.type==runnable.type) {
					exists=true;
				}
			}
			if(!exists)
				cars.add(runnable);
		}
		//해당차량에 메시지 전송
		public void carSendMsg(String carid,String msg) {
			
			cars.stream().filter(t->t.id.equals(carid)).forEach(f->{
				f.out.println(msg);
				f.out.flush();
			});
//			for(ClientRunnable r : cars) {
//				if(r.id.equals(carid)) {
//					r.out.println(msg);
//					r.out.flush();
//				}
//			}
		}
		//사용자등록
		public synchronized void userAdd(ClientRunnable runnable) {
			boolean exists =false;
			for(ClientRunnable c: users) {
				if(c.id==runnable.id && c.type==runnable.type) {
					exists=true;
				}
			}
			if(!exists)
				users.add(runnable);
		}
		//해당 사용자에게 메세지 전송
		public void userSendMsg(String userid,String msg) {
			
			users.stream().filter(t->t.id.equals(userid)).forEach(f->{
				f.out.println(msg);
				f.out.flush();
			});

		}
				
	}
	class ClientRunnable implements Runnable {
		private SharedObject sharedObject; //공유객체
		private Socket socket; //클라이언트와 연결된 socket
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
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String msg = "",carid="",userid="";
			String[] arr_msg;
			try {
				while((msg = br.readLine())!=null){
					
					if(msg.contains("/EXIT/")) {
						if(!Thread.currentThread().isInterrupted())
							Thread.currentThread().interrupt();
						
						break;
					}
					else if(msg.contains("/10000000/")) {						
						arr_msg = msg.split("/");
						carid = arr_msg[2];	
						
						this.id=carid;
						this.type="C";
						sharedObject.carAdd(this);
						
						String tcarid=carid;
						currentThread(()->{							
							//lvCarData.clear();								
							lvCarData.add(new Car(tcarid,tcarid));
							
						});
						printMsg("car 차량등록 carid:"+carid);
					}
					else if(msg.contains("/10000001/")) {
						arr_msg = msg.split("/");
						carid = arr_msg[2];	
						printMsg("tower->car 차량회수 요청 carid:"+carid);
												
						sharedObject.carSendMsg(carid,"/10000001/"+carid);
					}
					else if(msg.contains("/10000002/")) {
						arr_msg = msg.split("/");
						carid = arr_msg[2];	
						printMsg("car -> tower 회수처리완료 carid:"+carid);
					}
					else if(msg.contains("/10000100/")) {
						arr_msg = msg.split("/");
						userid = arr_msg[2];	
						this.id=userid;
						this.type="U";
						sharedObject.userAdd(this);
						
						String tuserid=userid;
						currentThread(()->{										
							lvUserData.add(new User(tuserid,tuserid));							
						});
						printMsg("app -> tower 사용자 등록 userid:"+userid);
					}
					else if(msg.contains("10000101")) {
						arr_msg = msg.split("/");
						userid = arr_msg[2];	
						printMsg("app -> tower 자동차선정 userid:"+userid);
						CarService service = new CarService();
						CarVO vo = service.getCarSel();
						
						sharedObject.userSendMsg(userid,"/10000101/"+vo.getCarId());
					}
					else if(msg.contains("10000102")) {
						printMsg("app -> tower 자동차이동요청 userid:"+userid);
					}
					//sharedObject.broadcast(msg);
					
				}
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		
	}
	private void printMsg(String msg) {
		Platform.runLater(()->{
			textarea.appendText(msg+"\n");
		});
	}
	private void currentThread(Runnable runnable) {
		Platform.runLater(runnable);
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnServerStart.setOnAction(e->{
			textarea.clear();
			printMsg("[채팅서버기동 - 6789]");
			//서버소캣을 만들어서 클라이언트 접속을 대기
			//JavaFx thread가 blocking되지 않도록 새로운 Thread를 만들어서 클라이언트 접속 대기해야 함
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						server = new ServerSocket(6789);
						while(true) {
							printMsg("[클라이언트 접속 대기중]");
							Socket socket = server.accept();
							printMsg("[클라이언트 접속 성공]");
							
							ClientRunnable cRunnable = new ClientRunnable(sharedObject, socket);
							sharedObject.clients.add(cRunnable);
							printMsg("[현재 클라이언트 수:]"+sharedObject.clients.size());
							executorService.execute(cRunnable);
							
						}
						
						
					}catch(Exception e) {
						e.getStackTrace();
					}
				}				
			};
			executorService.execute(runnable);
			
		});
		btnServerStop.setOnAction(e->{
			
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
    	    	//selected_userid = newValue.getUserid().toString();
    	    }
    	});
    	
	}
	
}


