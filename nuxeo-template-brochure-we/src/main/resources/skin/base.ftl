<html>
<head>
  <title>
     <@block name="title">
     Nuxeo Brochure
     </@block>
  </title>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
  <link rel="stylesheet" href="${skinPath}/css/site.css" type="text/css" media="screen" charset="utf-8">
  <link rel="stylesheet" href="${skinPath}/css/bootstrap.min.css">
  <link rel="icon" href="${skinPath}/img/favicon.png" />
  <link rel="shortcut icon" href="${skinPath}/img/favicon.ico" />
  <@block name="stylesheets" />
  <@block name="header_scripts" />
</head>


<body>
  		<@block name="header">
		<div class="header page-header">
			<a href="${This.path}"><h1>Nuxeo Brochure <small>Dynamically generated brochures</small></h1></a>
		</div>
    	</@block>

	<div class="container-fluid">	
    		<@block name="content">The Content</@block>
	</div>

</body>
</html>