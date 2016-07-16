package com.example;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

public class VMCreation extends Thread{
	
	private Connect con;
	private String XML;
	
	VMCreation(Connect con,String XML){
		this.con=con;
		this.XML=XML;
		//Thread t=new Thread();
		//t.start();
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("Vm creation called");
			Domain m=con.domainCreateXML(XML, 0);
			System.out.println("Vm creation done, vm with id "+m.getID());
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
