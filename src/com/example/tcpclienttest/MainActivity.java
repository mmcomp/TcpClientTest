package com.example.tcpclienttest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
@SuppressWarnings("unused")
public class MainActivity extends Activity {
	public static UpdateClass[] updates;
	public static int UpdatesIndex = 0;
	public static Boolean Updated = true;
	private ServerSocket serverSocket;
	public static DownloadTask[] downloadTask;
	public static String[] str_tmp = new String[2];
	public static Boolean DownloadRunning  = false;
	public static VideoView vid1;
	public static VideoView last_vid;
	public static int playIndex = 0;
	public static Boolean canPlay = true;
	Socket socket;
	String SERVER_IP = "192.168.1.7";
	int SERVER_PORT=6000;
	int SERVERPORT = 6000;
	public static String str = "";
	static String clientId = "1";
	static String batteryLevel = "";
	public static String reserveNum = "0";
	Handler updateConversationHandler;
	Thread serverThread = null;
	static WebView mywebview;
	static Vibrator v;
	
	public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
	
	void createDirectory()
	{
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"artan");
		if(!directory.isDirectory())
			directory.mkdirs();
		str += "Create directory\n";
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
	    @Override
	    public void onReceive(Context ctxt, Intent intent) {
	      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
	      //batteryTxt.setText(String.valueOf(level) + "%");
	      batteryLevel = String.valueOf(level);
	    }
	  };
	
	  
	void startPlaying()
	{
		/*
		try {
			if(canPlay)
			{
				if(last_vid!=null)
					((ViewManager)last_vid.getParent()).removeView(last_vid);
				if(playIndex >= updates.length)
					playIndex = 0;
				VideoView vid_tmp = new VideoView(getApplicationContext());
				vid_tmp.setLayoutParams(vid1.getLayoutParams());
				vid_tmp.setVisibility(View.VISIBLE);
				RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl);
				vid_tmp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
						arg0.reset();
						playIndex++;
						startPlaying();
					}
				});
				vid_tmp.setVideoPath(Environment.getExternalStorageDirectory()+File.separator+"mohsen"+File.separator+updates[playIndex].order+".mp4");
				rl.addView(vid_tmp);
				vid_tmp.start();
				last_vid = vid_tmp;
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		*/
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		updateConversationHandler = new Handler();
		v = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
		createDirectory();
		String comm = clientId+",7,"+getIPAddress(true)+"|"+batteryLevel;
		sendCommand(comm);
		/*
		vid1 = (VideoView)findViewById(R.id.vid1);
		vid1.setMediaController(new MediaController(getApplicationContext()));
		vid1.setVisibility(View.GONE);
		String comm = clientId+",7,"+getIPAddress(true)+"|"+batteryLevel;
		sendCommand(comm);
		Button reserveBtn = (Button) findViewById(R.id.reserve);
		reserveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String comm = clientId+",9";
				sendCommand(comm);
			}
		});
		Button logBtn = (Button) findViewById(R.id.log);
		logBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
			}
		});
		*/
		mywebview = (WebView) findViewById(R.id.webview1);
		WebSettings webSettings = mywebview.getSettings();
		//Intent inten = getIntent();
		//int kala_miniGroup_id = inten.getIntExtra("kala_miniGroup_id", -1);
		mywebview.addJavascriptInterface(new ClientClass(this), "client");
		webSettings.setJavaScriptEnabled(true);
		mywebview.loadUrl("file:///android_asset/html/client.html");
		if(android.os.Build.VERSION.SDK_INT==Build.VERSION_CODES.JELLY_BEAN)
			fixPro();
		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
		str += "server started\n";

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void fixPro()
	{
		mywebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
		mywebview.getSettings().setAllowFileAccessFromFileURLs(true);
	}
	
	void sendCommand(String comm)
	{
		ClientThread ct = new ClientThread();
		ct.commandToSend = comm;
		new Thread(ct).start();
	}
	
	class ClientThread implements Runnable {
		public String commandToSend = ""; 
		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVER_PORT);
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				DataInputStream infromServer = new DataInputStream(socket.getInputStream());
				outToServer.writeChars(commandToSend+"\n");
				int bytes = infromServer.read(buffer);
				String buf = new String(buffer, 0, bytes);
				//str += commandToSend+" -> "+buf+"\n";
				if(commandToSend.contains("1,9"))
				{
					reserveNum = buf;
					updateConversationHandler.post(new updateUIThread("res",""));
				}
				socket.close();
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
		}
	}
	
	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {

				try {

					socket = serverSocket.accept();

					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;

		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {
					String read = input.readLine();
					//char[] target = new char[1024];
					//String read = String.valueOf(target);
					read = read.trim();
					String cIp = clientSocket.getInetAddress().toString().replace("/", "");
					//DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
					//str += "Ip : "+cIp+"\n";
					if(read!=null && !read.isEmpty())
					{
						str += "cmd :"+read+"\n";
						CommandClass cc = new CommandClass(read);
						if(cc.isValid)
						{
							switch (cc.command) {
								case 1:
									str+="Reserve = "+cc.data+"\n";
									read = "res";
									canPlay = true;
									//outToClient.writeChars("khaak");
									break;
								case 2:
									str+="Call"+cc.data+"\n";
									read = "call";
									//canPlay = false;
									//outToClient.writeChars("khaak");
									break;
								case 3:
									str+="Reset\n";
									read = "reset";
									canPlay = true;
									//outToClient.writeChars("khaak");
									break;
								case 4:
									str+="Update ["+cc.data+"]\n";
									String[] updateAllTmp = cc.data.split("\\|");
									updates = new UpdateClass[updateAllTmp.length];
									downloadTask = new DownloadTask[updateAllTmp.length];//(MainActivity.this);
									for(int i = 0;i < updateAllTmp.length;i++)
									{
										downloadTask[i] = new DownloadTask(MainActivity.this);
										String zero_shit = String.valueOf((char)0);
										updateAllTmp[i] = updateAllTmp[i].replaceAll(zero_shit, "");
										String[] updateTmp = updateAllTmp[i].split("\\#");
										updates[i] = new UpdateClass(Integer.valueOf(updateTmp[0].trim()), updateTmp[1].trim(),Integer.valueOf(updateTmp[2].trim()));
										downloadTask[i].execute(updateTmp[1].trim(),Integer.valueOf(updateTmp[0].trim())+".mp4");
										//str += "update order='"+updates[i].order+"' addr='"+updates[i].linkAddr+"'\n";
									}
									Updated = false;
									read = "update";
									//outToClient.writeChars("khaak");
									break;
							}
						}
//						else						
//							outToClient.writeChars("0,0");
					}
					updateConversationHandler.post(new updateUIThread(read,cIp));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class updateUIThread implements Runnable {
		private String msg;
		private String cIp;
		public updateUIThread(String str,String cIpp) {
			this.msg = str;
			this.cIp = cIpp;
		}

		@Override
		public void run() {
		
			if(msg!=null && msg.trim()!="")
			{
				//Toast.makeText(getApplicationContext(), "msg:"+msg, Toast.LENGTH_SHORT).show();
				if(msg=="call")
				{
					//v.vibrate(2500);
					//str+="Vibrate\n";
					//if(last_vid!=null)
						//((ViewManager)last_vid.getParent()).removeView(last_vid);
					mywebview.loadUrl("javascript:call_client();");
				}
				else if(msg=="res")
				{
					/*
					TextView t1 = (TextView)findViewById(R.id.reserveNum);
					t1.setText(reserveNum);
					startPlaying();
					*/
					//Toast.makeText(getApplicationContext(), "javascript:reserveDone(100);", Toast.LENGTH_SHORT).show();
					str += "Reserve !\n";
					mywebview.loadUrl("javascript:reserveDone('"+reserveNum+"');");
				}
				else if(msg=="reset")
				{
					str+="reset\n";
					//v.cancel();
					mywebview.loadUrl("javascript:reset();");
				}
				else if(msg=="update")
				{
					str+="Update!";
					
				}
			}
		}
	}
/*	
	static void startDownloadFiles()
	{
		try {
			if(!Updated)
			{
				if(UpdatesIndex < updates.length)
				{
					UpdateClass tmpUpdate=null;
					for(int i = 0;i < updates.length;i++)
					{
						if(UpdatesIndex == updates[i].order)
							tmpUpdate = updates[i];
					}
					if(tmpUpdate!=null)
					{
						str+="link ='"+ updates[UpdatesIndex].linkAddr+"'\n";
						downloadTask.execute(updates[UpdatesIndex].linkAddr,String.valueOf(updates[UpdatesIndex].order)+".mp4");
					}
					UpdatesIndex++;
				}
				else
				{	
					UpdatesIndex = 0;
					Updated = true;
					DownloadRunning = false;
				}
			}
			
		} catch (Exception e) {
			str += "download error : "+e.getMessage()+"\n";
		}
	}
	static void startDownloadFiles(int updatesIndex)
	{
		try {
			if(!Updated)
			{
				if(updatesIndex < updates.length)
				{
					UpdateClass tmpUpdate=null;
					for(int i = 0;i < updates.length;i++)
					{
						if(updatesIndex == updates[i].order)
							tmpUpdate = updates[i];
					}
					if(tmpUpdate!=null)
					{
						str+="link ='"+ updates[updatesIndex].linkAddr+"'\n";
						downloadTask.execute(updates[updatesIndex].linkAddr,String.valueOf(updates[updatesIndex].order)+".mp4");
					}
				}
			}
			
		} catch (Exception e) {
			str += "download error : "+e.getMessage()+"\n";
		}
	}
*/
}
