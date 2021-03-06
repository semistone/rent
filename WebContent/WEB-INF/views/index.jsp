<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

  <title></title>
  <meta name="description" content="">
  <meta name="author" content="">

  <meta name="viewport" content="width=device-width,initial-scale=1">

  <!-- CSS concatenated and minified via ant build script-->
  <link href="<c:out value='${cdn}'/>/css/bootstrap.min.css" rel="stylesheet">
  <link href="<c:out value='${cdn}'/>/css/bootstrap-responsive.min.css" rel="stylesheet">
  <link href="<c:out value='${cdn}'/>/css/docs.css" rel="stylesheet">
  <link href="<c:out value='${cdn}'/>/css/rent.css" rel="stylesheet">  
  <!-- end CSS-->
</head>

<body class="preview" data-offset="80" data-target=".subnav" data-spy="scroll">
<div id="dialog"></div>
<div id="supersized-loader" style="display: none;"></div>
 <!-- Navbar
    ================================================== -->
<div id="navbar" class="navbar navbar-fixed-top">
 	<div class="navbar-inner">
 		<div class="container">
       		<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
         		<span class="icon-bar"></span>
         		<span class="icon-bar"></span>
         		<span class="icon-bar"></span>
       		</a>
       		<img class="brand" src="img/cat.png"></img>
       		<a class="brand" >Mobile Single Sign On</a>
       		<div class="nav-collapse" id="main-menu">
				<ul id="user_info" class="navbar-text pull-right"></ul>
				<ul class="nav" id="nav-menu"></ul>
       		</div>
		</div>
	</div>
</div>
<div id="main" class="container">        
</div> 
<hr>
<footer>
<div class="row">
    <div class="span4 offset4">
       Copyright (C) Siraya.org	
    </div>
</div>
</footer>
<script src="<c:out value='${cdn}'/>/js/libs/require/require.js"></script>  
<script>require.config({baseUrl:'<c:out value='${cdn}'/>/js/'});</script>
<script src="<c:out value='${cdn}'/>/js/main.js"></script>
</body>
</html>
