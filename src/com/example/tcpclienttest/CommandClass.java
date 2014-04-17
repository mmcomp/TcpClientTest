package com.example.tcpclienttest;

public class CommandClass {
	public Boolean isValid = false;
	public int clientId = -1;
	public int command = 0;
	public String data = "";
	public CommandClass(String cmd) {
			try {
				String[] cmdArr = cmd.split(",");
				if(cmdArr.length>=2)
				{
					isValid = true;
					clientId = Integer.valueOf(cmdArr[0].replaceAll("\\D+", ""));
					command = Integer.valueOf(cmdArr[1].replaceAll("\\D+", ""));
					data = "";
					if(cmdArr.length>2)
						data = cmdArr[2];				
				}
			} catch (Exception e) {
				data= "err = "+e.getMessage();
			}
	}
}
