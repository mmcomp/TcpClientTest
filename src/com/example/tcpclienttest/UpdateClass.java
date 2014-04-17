package com.example.tcpclienttest;

public class UpdateClass {
	public int order = -1;
	public String linkAddr = "";
	public String localAddr = "";
	public UpdateClass(int inOrder,String inLinkAddr,String inLocalAddr) {
		order = inOrder;
		linkAddr = inLinkAddr;
		localAddr = inLocalAddr;
	}
	public UpdateClass(int inOrder,String inLinkAddr) {
		order = inOrder;
		linkAddr = inLinkAddr;
	}
}
