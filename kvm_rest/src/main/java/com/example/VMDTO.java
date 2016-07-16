package com.example;

public class VMDTO {

	private String os;
	private int vcpu;
	private long memory;
	private String vmName;
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public int getVcpu() {
		return vcpu;
	}
	public void setVcpu(int vcpu) {
		this.vcpu = vcpu;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
}
