package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler extends Thread implements Runnable{

	//@Scheduled(fixedRate=1000)
	public  synchronized void getFioResult(){
		
		Runtime rt = Runtime.getRuntime();
		JSONObject output=new JSONObject();
		
String command="fio --name=randwrite --ioengine=libaio --iodepth=1 --rw=randwrite --bs=4k --direct=0 --size=256M --numjobs=4  --group_reporting";
		
		
		command=command+" | awk '/iops/ || /WRITE/'";
		
		
		
		Process p;
		try {
			
			String []cmd={"/bin/sh","-c",command};
			p = Runtime.getRuntime().exec(cmd);
			//System.out.println(p.exitValue());
			Date date=new Date(System.currentTimeMillis());
			Format format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			
			InputStream stdin = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			
		
			while((line= br.readLine())!=null){
			
				String []split=line.split(",");
				System.out.println(line);
				for(String sp:split){
					if(sp.contains("bw")){
						String []s=sp.split("=");
						
						output.put("bw", s[1].substring(0, s[1].length()-4).trim());
					}
					
					
					if(sp.contains("iops")){
						String []s=sp.split("=");
			
						output.put("iops", s[1].trim());
					}
					
					if(sp.contains("mint")){
						String []s=sp.split("=");
						
						output.put("mint", s[1].substring(0, s[1].length()-4).trim());
					}
					
					if(sp.contains("maxt")){
						String []s=sp.split("=");
						
						output.put("maxt", s[1].substring(0, s[1].length()-4).trim());
					}
					
					if(sp.contains("minb")){
						String []s=sp.split("=");
						
						output.put("minb", s[1].substring(0, s[1].length()-4).trim());
					}
					
					if(sp.contains("maxb")){
						String []s=sp.split("=");
					
						output.put("maxb", s[1].substring(0, s[1].length()-4).trim());
					}
					
				}
			}
			output.put("@timestamp", format.format(date));
				
			uploadDataViaHTTP(output);
		
				
				File files=new File("/root");
				File[]allFiles=files.listFiles();
				
				for(File f:allFiles){
					if(f.getName().startsWith("rand")){
						f.delete();
					}
				}
				
				
				
						
			
			
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
private void uploadDataViaHTTP(JSONObject data){
		
		
		try {
			URL url=new URL("http://130.65.157.206:9200/fio_benchmark_vm_cache_latest/fio_benchmark_vm_cache_latest");
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true){
		
			try {
				
				getFioResult();
				Thread.sleep(TimeUnit.SECONDS.toMillis(60));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
}
