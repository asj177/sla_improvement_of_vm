/**
 * 
 */

function createVM(){
	var data={};
	data["memory"]=$("#memory").val();
	data["vcpu"]=$("#vcpu").val();
	data["vmName"]=$("#vmname").val();
	data["os"]=$("#os").val();
	
	var request=JSON.stringify(data);
	
	var client = new XMLHttpRequest();
	var endPoint="http://localhost:8080/vm";//Need to change the localhost with the appliance ID mapping from the ES 
    
    client.open("POST", endPoint, false);//This Post will become put 
 
    client.setRequestHeader("Accept", "application/json");
    client.setRequestHeader("Content-Type","application/json");
	client.send(request);

}


/**
 * Need to check 
 */
function vmList(){
	var data=[];
	
	/*var client = new XMLHttpRequest();
	var endPoint="http://localhost:8080/vmlist";//Need to change the localhost with the appliance ID mapping from the ES 
    
    client.open("GET", endPoint, false);//This Post will become put 
 
    client.setRequestHeader("Accept", "application/json");
    client.setRequestHeader("Content-Type","application/json");
	client.send();
	data=JSON.parse(client.responseText);*/
	
	var ob={};
	ob["vmName"]="cmpe295";
	ob["status"]=1;
	
	data.push(ob);
	$("#vms").append('<table style="border:1px"></table>');
	var table=$('#vms').children();
	var col=2;
	
	for(var i=0;i<data.length;i++){
		
		table.append('<tr><td>'+data[i].vmName+'</td><td>'+data[i].status+'</td></tr>');
		
	
/*	var table=document.getElementById("vmList");//Need to change the Table ID to pcapRetrvlStatustableId


	var row = document.createElement("tr");

	 var td1 = document.createElement("td");
	 td1.innerHTML=str;
	 td1.setAttribute("id",data[i]+"_info");

	 var td2=document.createElement("td");
	td2.innerHTML=data_from_get_pcap_click.percentage_complete;
	td2.setAttribute("id",data_from_get_pcap_click.record_replacement_key); // replace with d.audit_log_row_key

	 var td3=document.createElement("td");*/
	
	}
	
}




function powerChange(vmName){
	var client = new XMLHttpRequest();
	var endPoint="http://localhost:8080/power/"+vmName;//Need to change the localhost with the appliance ID mapping from the ES 
    
    client.open("PUT", endPoint, false);//This Post will become put 
 
    client.setRequestHeader("Accept", "application/json");
    client.setRequestHeader("Content-Type","application/json");
	client.send();
	var powerstatus=client.responseText;

	var id="#"+vmname;
	
	if(powerstatus=="poweredOn"){
		$(id).css({"background-color": "red"});
		$(id).html("poweredOff");
		$(id).val("poweredOff");
		
	}else{
		$(id).css({"background-color": "green"});
		$(id).html("poweredOn");
		$(id).val("poweredOn");
		
	}
	
}

function cacheDetails(){
	
}

function IOStats(){
	
}

function hostInfo(){
	alert("hostinfo");
}


function iopsCompare(){
	var j=0;
	while(j!=2){

	var endpoint="";

	if(j==0){
	endpoint="http://130.65.157.206:9200/fio_benchmark_vm_cache_latest/fio_benchmark_vm_cache_latest/_search?size=1000";

	}else{
	endpoint="http://130.65.157.206:9200/fio_benchmark_vm_no_cache_latest/fio_benchmark_vm_no_cache_latest/_search?size=1000";

	}
	  var client = new XMLHttpRequest();
	    
	    var endPoint=endpoint;
	    var dataToDisplay=[]
	    var esDataJson;
	   
	     client.open("GET", endPoint, false);
	     client.send();
	     esDataJson=JSON.parse(client.responseText);

	      var hits=esDataJson.hits.hits;

	         
	      for(var i=0;i<hits.length;i++){

	         var es=hits[i]._source;
	        

	  	

	        
		var data=[];
	      
	         //data.push(new Date(es.timestamp));
	         data.push(new Date(es["@timestamp"]));
	         data.push(es.iops);
	         dataToDisplay.push(data);
	    }




	//Here we sort the data to display
	dataToDisplay.sort(function(a, b){
	    var keyA = new Date(a[0]),
	        keyB = new Date(b[0]);
	    // Compare the 2 dates
	    if(keyA < keyB) return -1;
	    if(keyA > keyB) return 1;
	    return 0;
	});


	var div="";

	if(j==0){
	div="graphdiv1";
	}else{

	div="graphdiv2";
	}
	var timestart=null;
	var timeend=null;
	  g2 = new Dygraph(
	    document.getElementById(div),
	    dataToDisplay, // path to CSV file
	     {
	       labels: [ "Date", "iops" ],
	       zoomCallback: function(minX, maxX, yRanges) {
	   // console.log("Zoomed to [", minX, ", ", maxX, "]");
	     

	}
	      
	     }   // options
	  );

	g2.ready(function() {
	  console.log("Data loaded. x-axis range is:", g2.xAxisRange());
	});
	j++;
	}
	bandwidht();
}




function bandwidht(){
	var j=0;
	while(j!=2){

	var endpoint="";

	if(j==0){
	endpoint="http://130.65.157.206:9200/fio_benchmark_vm_cache_latest/fio_benchmark_vm_cache_latest/_search?size=1000";

	}else{
	endpoint="http://130.65.157.206:9200/fio_benchmark_vm_no_cache_latest/fio_benchmark_vm_no_cache_latest/_search?size=1000";

	}
	  var client = new XMLHttpRequest();
	    
	    var endPoint=endpoint;
	    var dataToDisplay=[]
	    var esDataJson;
	   
	     client.open("GET", endPoint, false);
	     client.send();
	     esDataJson=JSON.parse(client.responseText);

	      var hits=esDataJson.hits.hits;

	         
	      for(var i=0;i<hits.length;i++){

	         var es=hits[i]._source;
	        

	  	

	        
		var data=[];
	      
	         //data.push(new Date(es.timestamp));
	         data.push(new Date(es["@timestamp"]));
	         data.push(es.bw);
	         dataToDisplay.push(data);
	    }




	//Here we sort the data to display
	dataToDisplay.sort(function(a, b){
	    var keyA = new Date(a[0]),
	        keyB = new Date(b[0]);
	    // Compare the 2 dates
	    if(keyA < keyB) return -1;
	    if(keyA > keyB) return 1;
	    return 0;
	});


	var div="";

	if(j==0){
	div="graphdiv3";
	}else{

	div="graphdiv4";
	}
	var timestart=null;
	var timeend=null;
	  g2 = new Dygraph(
	    document.getElementById(div),
	    dataToDisplay, // path to CSV file
	     {
	       labels: [ "Date", "iops" ],
	       zoomCallback: function(minX, maxX, yRanges) {
	   // console.log("Zoomed to [", minX, ", ", maxX, "]");
	     

	}
	      
	     }   // options
	  );

	g2.ready(function() {
	  console.log("Data loaded. x-axis range is:", g2.xAxisRange());
	});
	j++;
	}
}


function vmstats(){
	
	var vmname=$("#vmname").val();
	


	var endpoint="http://130.65.157.206:9200/io_stat_vm/_search?q=vm_name:"+vmaname;

	
	  var client = new XMLHttpRequest();
	    
	    var endPoint=endpoint;
	    var dataToDisplay=[]
	    var esDataJson;
	   
	     client.open("GET", endPoint, false);
	     client.send();
	     esDataJson=JSON.parse(client.responseText);

	      var hits=esDataJson.hits.hits;
	      
	      var data_rd_bytes=[];
	      var data_rd_req=[];
	      var data_wr_req=[];
	      var data_wr_bytes=[];

	         
	      for(var i=0;i<hits.length;i++){

	         var es=hits[i]._source;
	        

	  	

	        
	         var data_rd=[];
		      var data_rdreq=[];
		      var data_wrreq=[];
		      var data_wr[];
		      
	         //data.push(new Date(es.timestamp));
		      //Read Bytes
		      data_rd.push(new Date(es["@timestamp"]));
		      data_rd.push(es.rd_bytes);
		      
		      data_rd_bytes.push(data_rd);
		      
		      
		      //Read Request Issues
		      data_rdreq.push(new Date(es["@timestamp"]));
		      data_rdreq.push(es.rd_req);
		      
		      data_rd_req.push(data_rd);
		      
		      
		      //Write Bytes
		      data_wr.push(new Date(es["@timestamp"]));
		      data_wr.push(es.wr_bytes);
		      
		      data_wr_bytes.push(data_rd);
		      
		      //Writ Request issues
		      data_wrreq.push(new Date(es["@timestamp"]));
		      data_wrreq.push(es.wr_req);
		      
		      data_wr_req.push(data_rd);
		      
		      
		      
		      
		      
		      
	    }




	//Sort Read Bytes
	      data_rd_bytes.sort(function(a, b){
	    var keyA = new Date(a[0]),
	        keyB = new Date(b[0]);
	    // Compare the 2 dates
	    if(keyA < keyB) return -1;
	    if(keyA > keyB) return 1;
	    return 0;
	});
	
	
	//Sort Read Request
	      data_rd_req.sort(function(a, b){
	  	    var keyA = new Date(a[0]),
	  	        keyB = new Date(b[0]);
	  	    // Compare the 2 dates
	  	    if(keyA < keyB) return -1;
	  	    if(keyA > keyB) return 1;
	  	    return 0;
	  	});
	  	
	
	
	//Sort Write Bytes
	      data_wr_bytes.sort(function(a, b){
		  	    var keyA = new Date(a[0]),
		  	        keyB = new Date(b[0]);
		  	    // Compare the 2 dates
		  	    if(keyA < keyB) return -1;
		  	    if(keyA > keyB) return 1;
		  	    return 0;
		  	});
	
	
	//Sort Write Request
	      data_wr_req.sort(function(a, b){
		  	    var keyA = new Date(a[0]),
		  	        keyB = new Date(b[0]);
		  	    // Compare the 2 dates
		  	    if(keyA < keyB) return -1;
		  	    if(keyA > keyB) return 1;
		  	    return 0;
		  	});


	var div1="readbytes";
	var div2="readrequest";
	var div3="writebytes";
	var div4="writerequest";

	$("#diviostats").show();

	
	
	  g2 = new Dygraph(
	    document.getElementById(div1),
	    data_rd_bytes, // path to CSV file
	     {
	       labels: [ "Date", "readbytes" ],
	       zoomCallback: function(minX, maxX, yRanges) {
	   // console.log("Zoomed to [", minX, ", ", maxX, "]");
	     

	}
	      
	     }   // options
	  );
	  
	  
	  g3 = new Dygraph(
			    document.getElementById(div2),
			    data_rd_req, // path to CSV file
			     {
			       labels: [ "Date", "readrequest" ],
			       zoomCallback: function(minX, maxX, yRanges) {
			   // console.log("Zoomed to [", minX, ", ", maxX, "]");
			     

			}
			      
			     }   // options
			  );
	  
	  
	  g4 = new Dygraph(
			    document.getElementById(div3),
			    data_wr_bytes, // path to CSV file
			     {
			       labels: [ "Date", "write_bytes" ],
			       zoomCallback: function(minX, maxX, yRanges) {
			   // console.log("Zoomed to [", minX, ", ", maxX, "]");
			     

			}
			      
			     }   // options
			  );
	  
	  g5 = new Dygraph(
			    document.getElementById(div4),
			    data_wr_req, // path to CSV file
			     {
			       labels: [ "Date", "write_request" ],
			       zoomCallback: function(minX, maxX, yRanges) {
			   // console.log("Zoomed to [", minX, ", ", maxX, "]");
			     

			}
			      
			     }   // options
			  );
	  

	g2.ready(function() {
	  console.log("Data loaded. x-axis range is:", g2.xAxisRange());
	});
	
	
	
	
}