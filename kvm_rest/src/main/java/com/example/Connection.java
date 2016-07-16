package com.example;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;

public class Connection {
	
	static Connect conn=null;
	
	private Connection(){
		
	}
	
	
	public static Connect getConnection() throws LibvirtException{
		if(conn==null){
			conn=new Connect("qemu:///system",false);
		}
		
		return conn;
	}

}
