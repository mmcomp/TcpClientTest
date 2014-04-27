package com.example.tcpclienttest;

public class UpdateClass {
	public int order = -1;
	public String linkAddr = "";
	public String localAddr = "";
	public int len = 3;
	public UpdateClass(int inOrder,String inLinkAddr,String inLocalAddr,int inLen) {
		order = inOrder;
		linkAddr = inLinkAddr;
		localAddr = inLocalAddr;
		len = inLen;
	}
	public UpdateClass(int inOrder,String inLinkAddr,int inLen) {
		order = inOrder;
		linkAddr = inLinkAddr;
		len = inLen;
	}
}
