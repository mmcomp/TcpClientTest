package com.example.tcpclienttest;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class ClientClass {
	Context mContext;
	public ClientClass(Context inConetxt) {
		mContext = inConetxt;
	}
	
	@JavascriptInterface
	public void reserve()
	{
		String comm = MainActivity.clientId+",9";
		MainActivity m = (MainActivity)mContext;
		m.sendCommand(comm);
	}
	
	@JavascriptInterface
	public void doVibrate(String ms)
	{
		MainActivity.v.vibrate(Integer.parseInt(ms));
	}

	@JavascriptInterface
	public void undoVibrate()
	{
		MainActivity.v.cancel();
	}
	
	@JavascriptInterface
	public String getUpdates()
	{
		UpdateClass[] updates = MainActivity.updates;
		String out = "";
		for(int i = 0;i < updates.length;i++)
			out += ((out!="")?",":"")+updates[i].localAddr;
		return out;
	}
}
