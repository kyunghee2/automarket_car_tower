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

import javafx.application.Platform;
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
	
	class SharedObject {
		List<ClientRunnable> clients = new ArrayList<ClientRunnable>();	
		List<User> users = new ArrayList<User>();
		List<Car> cars = new ArrayList<Car>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		public SharedObject(){}
		public synchronized void broadcast(String msg) {
			
			clients.stream().forEach(t-> {
				t.out.println(msg);
				t.out.flush();
			});
			
		}
				
	}
	class ClientRunnable implements Runnable {
		private SharedObject sharedObject; //공유객체
		private Socket socket; //클라이언트와 연결된 socket
		private BufferedReader br;
		private PrintWriter out;		
		private Integer idx;
		
		public ClientRunnable(SharedObject sharedObject, Socket socket) {
			super();
			this.sharedObject = sharedObject;
			this.socket = socket;
			this.idx = sharedObject.clients.size();
			try {
				this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = new PrintWriter(socket.getOutputStream());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String msg = "",carid="";
			String[] arr_msg;
			try {
				while((msg = br.readLine())!=null){
					if(msg.equals("/EXIT/")) {
						if(!Thread.currentThread().isInterrupted())
							Thread.currentThread().interrupt();
						
						break;
					}
					else if(msg.equals("/10000000/")) {
						//web -> tower 회수요청
						arr_msg = msg.split("/");
						carid = arr_msg[2];	
						printMsg("web -> tower 회수요청 carid:"+carid);
						
					}
					else if(msg.equals("/10000001/")) {
						//car -> tower 목적지 도착
						arr_msg = msg.split("/");
						carid = arr_msg[2];	
						printMsg("car -> tower 목적지 도착 carid:"+carid);
						
					}
					sharedObject.broadcast(msg);
					
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
	}
	
}
