package com.cmpe295.sdk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;


import com.cmpe295.sdk.utils.VMDTO;

public class KVM {

	public boolean createVM(VMDTO vmDetails){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Object json = objectMapper.readValue(
				     objectMapper.writeValueAsString(vmDetails), Object.class);
			URL url=new URL("http://localhost:8080/vm");
			HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("POST");
			
			OutputStreamWriter output=new OutputStreamWriter(httpcon.getOutputStream());
	
			output.write(objectMapper.writerWithDefaultPrettyPrinter()
				     .writeValueAsString(json));
			
			
			output.close();
			httpcon.connect();
			int code=httpcon.getResponseCode();
			
			if(code==201){
				return true;
			}
			System.out.println(code);
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public String getVMList(){
		String vmList="";
		try{
			
			URL url=new URL("http://localhost:8080/vmlist");
			HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("GET");
			httpcon.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(httpcon.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				vmList=output;
			}

			httpcon.disconnect();

			
		}catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vmList;
	}
	
	
	public String getCacheDetails(){
		String cache="";
		try{
			
			URL url=new URL("http://localhost:8080/cachedetails");
			HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("GET");
			httpcon.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(httpcon.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				cache=output;
			}

			httpcon.disconnect();

			
		}catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cache;
	}
	
	
	public boolean updateVMPower(String vmName){
		boolean check=false;
try{
			
			URL url=new URL("http://localhost:8080/power/"+vmName);
			HttpURLConnection httpcon=(HttpURLConnection)url.openConnection();
			httpcon.setDoOutput(true);
			httpcon.setRequestMethod("PUT");
			httpcon.connect();
			int code=httpcon.getResponseCode();
			

			httpcon.disconnect();
			if(code==200){
				return true;
			}

			
		}catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return check;
	}
}
