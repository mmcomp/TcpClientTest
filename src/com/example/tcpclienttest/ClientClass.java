package com.example.tcpclienttest;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
			out += ((out!="")?",":"")+updates[i].order+".mp4"+"#"+updates[i].len;
		return out;
	}
	
	@JavascriptInterface
	public String showLog()
	{
		Toast.makeText(mContext, MainActivity.str, Toast.LENGTH_LONG).show();
		return MainActivity.str;
	}

	@JavascriptInterface
	public void killAll()
	{
		System.exit(0);
	}
	@JavascriptInterface
	public void toast(String str)
	{
		Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
	}
	
}
