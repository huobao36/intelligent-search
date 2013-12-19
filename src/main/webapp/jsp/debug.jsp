<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">
        <script>
        function ajaxToURL(url, ifSend, params) {
                var xmlHttp=null;
                if (window.XMLHttpRequest)
                {// code for IE7, Firefox, Opera, etc.
                  xmlHttp=new XMLHttpRequest();
                }
                else if (window.ActiveXObject)
                {// code for IE6, IE5
                  xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
                }
                if (xmlHttp!=null)
                {
                  if(ifSend) {
                    xmlHttp.open("GET", url, true);
                  } else {
                    xmlHttp.open("POST", url, true);
                  }
                  xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                  xmlHttp.onreadystatechange = function() {
                       if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                             document.open();
                             document.write(xmlHttp.responseText);
                        }
                  }
                  xmlHttp.send(params);
                }
                else
                {
                  alert("Your browser does not support XMLHTTP.");
                }
                
            }
        function search() {
           if(event.keyCode == 13 || event.keyCode == 0) {
				params = "key=" + document.getElementById("key").value
        		ajaxToURL("?" + params, true, null)
         	}
       	}
       	</script>
    </head>
    <body>
    	<div class="container">
			<div class="row span8">
				<h2 id="input-groups-sizing">CMCC ROBOT</h2>
				<br><br><br>
				<p>
					N2-N4 grams with most frequency: <br>
					<ol class="breadcrumb">
						<c:forEach items="${sort_words}" var="sortWord">
						
						<li>
							<a>${sortWord.rawWord}</a><a>(${sortWord.count})</a>
						</li>
						</c:forEach> 
					</ol>
				</p>
				<div class="row">
					<div class="input-group">
            			<input type="text" class="form-control" id="key" onkeydown="search()" text="4g">
            			<span class="input-group-btn">
              				<button class="btn btn-default" type="button" onclick="search()" >Go!</button>
            			</span>
          			</div>
				</div>
				<br><br>
				<div class="row">	
					<div class="list-group">
          				<c:forEach items="${query_answers}" var="qAnda">
          				 <a href="#" class="list-group-item">
          					<h5 class="list-group-item-heading">问题：${qAnda.query}</h5>
          					<p class="list-group-item-text">答案：${qAnda.answer}</p>
        				  </a>
        				 <br>
          				 </c:forEach>
          			</div>
          		</div>
          	</div>
    	</div>
    </body>
</html>

