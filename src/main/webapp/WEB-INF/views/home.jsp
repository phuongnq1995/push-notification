<%@page import="java.util.List"%>
<%@page import="org.phuongnq.demo.model.NotificationEntity"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-3d.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h3>Notification</h3><div id="sseDiv"></div>
<div class="container">
<div id="render-div"></div>
<!-- <div id="sliders">
    <table>
        <tr>
        	<td>Alpha Angle</td>
        	<td><input id="alpha" type="range" min="0" max="45" value="15"/> <span id="alpha-value" class="value"></span></td>
        </tr>
        <tr>
        	<td>Beta Angle</td>
        	<td><input id="beta" type="range" min="-45" max="45" value="15"/> <span id="beta-value" class="value"></span></td>
        </tr>
        <tr>
        	<td>Depth</td>
        	<td><input id="depth" type="range" min="20" max="100" value="50"/> <span id="depth-value" class="value"></span></td>
        </tr>
    </table>
</div> -->
</div>	


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	sendRequest();
});
var jsonArray = ${notifications};
var result = [];
for(var i in jsonArray){
	result.push([jsonArray[i].name, jsonArray[i].hight]);
}
console.log(result);
var chart = new Highcharts.Chart({
	
	 chart: {
	     renderTo: 'render-div',
	     type: 'column',
	     options3d: {
	         enabled: true,
	         alpha: 0,
	         beta: 5,
	         depth: 20,
	         viewDistance: 25
	     }
	 },
	 title: {
	     text: 'Notification price'
	 },
	 subtitle: {
	     text: 'Chart for something'
	 },
	 xAxis: {
		 type: 'category',
	        labels: {
	            rotation: -5,
	            style: {
	                fontSize: '13px',
	                fontFamily: 'Verdana, sans-serif'
	            }
	        }
	 },
	 yAxis: {
	     title: {
	         text: 'Name'
	     }
	 },
	 plotOptions: {
	     column: {
	         depth: 25
	     }
	 },
     legend: {
         enabled: false
     },
     tooltip: {
         pointFormat: 'Population in 2008: <b>{point.y:.1f} millions</b>'
     },
	 series: [{
	     data: result
	 }],
	 dataLabels: {
         enabled: true,
         rotation: -90,
         color: '#FFFFFF',
         align: 'right',
         format: '{point.y:.1f}', // one decimal
         y: 10, // 10 pixels down from the top
         style: {
             fontSize: '13px',
             fontFamily: 'Verdana, sans-serif'
         }
     }
});

function sendRequest(){
	if(typeof(EventSource) !== "undefined") {
	    var source = new EventSource("http://localhost:8083/demo/sse");
	    source.onmessage = function(event) {
	        document.getElementById("sseDiv").innerHTML = event.data;
	        if(event.data != ""){
	        	var jsonArraySend = JSON.parse(event.data);
	        	console.log(jsonArraySend);
 		        var resultSend = [];
		        for(var y in jsonArraySend){
		        	resultSend.push([jsonArraySend[y].name, jsonArraySend[y].hight]);
		        }
		        console.log(resultSend);
		        chart.series[0].setData(resultSend); 
	        }
	    };
	} else {
	    document.getElementById("sseDiv").innerHTML =
	                     "Your browser does not support server-sent events.";
	}
}
</script>
</body>
</html>