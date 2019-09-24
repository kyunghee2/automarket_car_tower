package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class CarClientTest extends Application{
	TextArea textarea;
	Button connBtn, disConnBtn;//서버접속, 접속끊기 버튼
	TextField msgTf; //메시지 입력칸
	
	Socket socket;
	BufferedReader br ;
	PrintWriter out;
	//클라이언트 Thread는 1개만 ,ThreadPool을 사용할 경우 overhead발생
	ExecutorService executorService = Executors.newCachedThreadPool();
		
	public static void main(String[] args) {		
		launch();
	}
	private void printMsg(String msg) {
		Platform.runLater(()->{
			textarea.appendText(msg+"\n");
		});
	}
	//서버로부터 들어오는 메시지를 계속 받아서 화면에 출력하기 위한 용도의 Thread
	class ReceiveRunnable implements Runnable {
		//서버로 부터 들어오는 메시지를 받아들이는 역할을 수행
		//소켓에 대한 입력스트림만 있으면 됨
		private BufferedReader br;
		
		public ReceiveRunnable(BufferedReader br) {
			super();
			this.br = br;
		}

		@Override
		public void run() {
			String line = "";
			try {
				while((line=br.readLine())!=null) {
					printMsg(line);
				}
			}catch(Exception e) {
				
			}
		}
		
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Stage arg0 -> window를 지칭함
		//화면구성해서 window 띄우는 코드
		//화면기본 layout을 설정 => 화면을 동서남북중앙(5개의 영역)으로 분리
		BorderPane root = new BorderPane();
		//BorderPane의 크기를 설정=> 화면을 띄우는 window의 크기설정
		root.setPrefSize(700, 500);
		//Component생성해서 BorderPane에 부착
		textarea = new TextArea();
		root.setCenter(textarea);
		
		connBtn = new Button("서버 접속");
		connBtn.setPrefSize(250, 50);
		connBtn.setOnAction(t->{
			//버튼에서 Action이 발생했을때 호출
			try {
				socket = new Socket("127.0.0.1",6789);
				//socket = new Socket("70.12.115.80",6789);		
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());				
				printMsg("서버 접속 성공");
								
				ReceiveRunnable runnable = new ReceiveRunnable(br);
				executorService.execute(runnable);
			}catch(Exception e) {
				System.out.println(e);
			}
		});
		disConnBtn = new Button("접속 종료");
		disConnBtn.setPrefSize(250, 50);
		disConnBtn.setOnAction(t->{
			out.println("/EXIT/");
			out.flush();
			printMsg("접속 종료");
		});
		
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		//flowpane에 버튼 추가
		
//		idTf = new TextField();
//		idTf.setPrefSize(100, 50);
		
		msgTf = new TextField();
		msgTf.setPrefSize(200, 50);
		msgTf.setOnAction(t->{
			//입력상자(TextField)에서 엔터키가 입력되면 호출
			String msg = msgTf.getText();
			out.println(msg);//서버로 문자열 전송
			out.flush();
			msgTf.setText("");
						
		});
		
		flowpane.getChildren().add(connBtn);
		flowpane.getChildren().add(disConnBtn);
//		flowpane.getChildren().add(idTf);
		flowpane.getChildren().add(msgTf);
		root.setBottom(flowpane);
		
		//Scene객체가 필요
		Scene scene = new Scene(root); 
		primaryStage.setScene(scene);
		primaryStage.setTitle("Car Client Test");
		primaryStage.show();
	}

}