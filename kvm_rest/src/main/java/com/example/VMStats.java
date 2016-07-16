package com.example;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainBlockStats;
import org.libvirt.LibvirtException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VMStats {
	
	
	
	@Scheduled(fixedRate=60000)
	public void getStats() throws LibvirtException{
		Connect conn = Connection.getConnection();
		JSONObject data=new JSONObject();
		for (int dom : conn.listDomains()) {
			Domain machine = conn.domainLookupByID(dom);

			JSONObject xmlJSONObj = XML.toJSONObject(machine.getXMLDesc(0));

			JSONObject dev = xmlJSONObj.getJSONObject("domain");

			JSONObject devices_disk = dev.getJSONObject("devices");
			JSONArray devices = devices_disk.getJSONArray("disk");

			for (int i = 0; i < devices.length(); i++) {
				JSONObject disks = devices.getJSONObject(i);
				Date date=new Date(System.currentTimeMillis());
				Format format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

				if (disks.has("source")) {
					JSONObject device = disks.getJSONObject("target");

					String device_name = device.getString("dev");

					DomainBlockStats iops = machine.blockStats(device_name);
					data.put("rd_bytes", iops.rd_bytes);
					data.put("rd_req", iops.rd_req);
					data.put("wr_req", iops.wr_req);
					data.put("wr_bytes", iops.wr_bytes);
					data.put("vm_name", machine.getName());
					data.put("@timestamp", format.format(date));
					uploadData(data);

					/*System.out.println("Read io" + iops.rd_bytes);
					System.out.println("Read req" + iops.rd_req);
					System.out.println("write req" + iops.wr_req);
					System.out.println("write io" + iops.wr_bytes);*/
				}
			}

		}

	}

	private void uploadData(JSONObject data){
		try {
			URL url=new URL("http://130.65.157.206:9200/io_stat_vm/io_stat_vm");
			HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("POST");
			
			OutputStreamWriter output=new OutputStreamWriter(httpcon.getOutputStream());
	
			output.write(data.toString());
			
			
			output.close();
			httpcon.connect();
			int code=httpcon.getResponseCode();
			System.out.println(code);
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
