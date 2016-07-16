package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainBlockStats;
import org.libvirt.LibvirtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RestController {

	/**
	 * 
	 * @param request
	 * @param vmDetails
	 * @return
	 * @throws LibvirtException
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/vm", produces = { "application/json" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity createVM(HttpServletRequest request, @RequestBody VMDTO vmDetails)
			throws LibvirtException {
		ResponseEntity resp = null;
		Connect conn = Connection.getConnection();

		try {

			String imageSource = "";
			if (vmDetails.getOs().equalsIgnoreCase("ubuntu")) {
				imageSource = Constants.UBUNTU;
			}

			if (vmDetails.getOs().equalsIgnoreCase("centos")) {
				imageSource = Constants.CENTOS;
			}

			String XMLDESC = "<domain type='kvm'> " + "<name>" + vmDetails.getVmName() + "</name>" + "<memory>"
					+ vmDetails.getMemory() + "</memory> <vcpu>" + vmDetails.getVcpu() + "</vcpu>"
					+ " <os> <type arch='x86_64' machine='pc-i440fx-rhel7.0.0'>hvm</type> </os>"
					+ "<devices><disk type='file' device='disk'><source file='" + imageSource
					+ "'/><target dev='vda' bus='virtio'/>" +

					"</disk> </devices> <on_reboot>restart</on_reboot><on_poweroff>preserve</on_poweroff>"
					+ "<on_crash>restart</on_crash></domain>";

			
			VMCreation creation=new VMCreation(conn,XMLDESC);
			creation.start();

			resp = new ResponseEntity("VMCreation in process", HttpStatus.CREATED);
		

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}
	
	

	/**
	 * 
	 * @param request
	 * @return
	 * @throws LibvirtException
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/vmstats", produces = { "application/json" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getVmStats(HttpServletRequest request) throws LibvirtException {
		ResponseEntity resp = null;
		Connect conn = Connection.getConnection();
		for (int dom : conn.listDomains()) {
			Domain machine = conn.domainLookupByID(dom);

			JSONObject xmlJSONObj = XML.toJSONObject(machine.getXMLDesc(0));

			JSONObject dev = xmlJSONObj.getJSONObject("domain");

			JSONObject devices_disk = dev.getJSONObject("devices");
			JSONArray devices = devices_disk.getJSONArray("disk");

			for (int i = 0; i < devices.length(); i++) {
				JSONObject disks = devices.getJSONObject(i);

				if (disks.has("source")) {
					JSONObject device = disks.getJSONObject("target");

					String device_name = device.getString("dev");

					DomainBlockStats iops = machine.blockStats(device_name);

					System.out.println("Read io" + iops.rd_bytes);
					System.out.println("Read req" + iops.rd_req);
					System.out.println("write req" + iops.wr_req);
					System.out.println("write io" + iops.wr_bytes);
				}
			}

		}

		return resp;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws LibvirtException
	 * @throws IOException
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/vmlist", produces = { "application/json" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getVmlist(HttpServletRequest request) throws LibvirtException, IOException {
		ResponseEntity resp = null;
		Connect conn = Connection.getConnection();
		
		ArrayList<VirtualMachineList>vmDetails=new ArrayList<VirtualMachineList>();
		
		
		
		
		
	
		for(String domains:conn.listDefinedDomains()){
			VirtualMachineList vm=new VirtualMachineList();
			vm.setStatus(0);
			vm.setVmName(domains);
			vmDetails.add(vm);
		}
		
		for(int domains:conn.listDomains()){
			String dom=conn.domainLookupByID(domains).getName();
			VirtualMachineList vm=new VirtualMachineList();
			vm.setStatus(1);
			vm.setVmName(dom);
			vmDetails.add(vm);
			
		}
		
	

		resp=new ResponseEntity(vmDetails,HttpStatus.OK);
		
		return resp;

	}

	@RequestMapping(value = "/vmxml", produces = { "application/json" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getVMDetails(HttpServletRequest request) throws IOException, LibvirtException{
		ResponseEntity resp = null;
		
		Connect con=Connection.getConnection();
		
		Domain d=con.domainLookupByName("cmpe295");
		System.out.println(d.getXMLDesc(0));
		
		resp=new ResponseEntity(d.getXMLDesc(0),HttpStatus.OK);
		return resp;
		
		
	}
	/**
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cachedetails", produces = { "application/json" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getCacheDetails(HttpServletRequest request) throws IOException {
		ResponseEntity resp = null;
		Process p = Runtime.getRuntime().exec("dmsetup status /dev/mapper/home-cached");
		InputStream stdin = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(stdin);
		BufferedReader br = new BufferedReader(isr);
		
		String line = null;
		HashMap resu=new HashMap();
		while((line=br.readLine())!=null){
			String[]s=line.split(" ");
			
			resu.put("used metadata blocks/total metadata blocks", s[4]);
			resu.put("cache block size", s[5]);
			resu.put("<#used cache blocks>/<#total cache blocks>", s[6]);
			resu.put("read hits", s[7]);
			resu.put("read misses",s[8]);
			resu.put("write hits", s[9]);
			resu.put("write misses", s[10]);
			
		}
		
		resp=new ResponseEntity(resu,HttpStatus.OK);

		return resp;
	}
   
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/power/{vmName}", produces = { "application/json" }, method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity updatePower(HttpServletRequest request, @PathVariable("vmName") String vmName) throws LibvirtException {
		ResponseEntity resp = null;
		
		Connect conn = Connection.getConnection();
		
		Domain machine=conn.domainLookupByName(vmName);
		
		if(machine.isActive()==1){
			machine.shutdown();
			resp=new ResponseEntity("poweredOff",HttpStatus.OK);
		}else{
			machine.create();
			resp=new ResponseEntity("poweredOn",HttpStatus.OK);
		}
		

		return resp;
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sdk", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE }, method = RequestMethod.GET)
	public void getFile(
			HttpServletRequest request, HttpServletResponse response)
			{
        try{
		//fileName = "File.pcap";
		byte[] reportBytes = null;
		File result = new File("/root/Downloads/sdk.jar");
		
		if (result.exists()) {
			System.out.println("File exists ");
			InputStream inputStream = new FileInputStream("/root/Downloads/sdk.jar");
			String type = "jar";
			response.setHeader("Content-Disposition", "attachment; filename=sdk.jar"
					);
			response.setHeader("Content-Type", type);

			reportBytes = new byte[131072];// New change
			OutputStream os = response.getOutputStream();// New change
			int read = 0;
			while ((read = inputStream.read(reportBytes)) != -1) {
				os.write(reportBytes, 0, read);
			}
			System.out.println("Bytes sent" + reportBytes);
			os.flush();
			os.close();

			System.out.println("FIle sent ");

		}
		
		System.out.println("File does not exists ");
		
        }catch (IOException ioError){
        	System.out.println("IO Exception Error Occured in getFile method ");
        	ioError.printStackTrace();
			
        }catch(Exception error){
        	error.printStackTrace();
        }

	} 

}
