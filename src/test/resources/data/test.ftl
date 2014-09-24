<html>
<body>
The doc has UID ${doc.id} and the current user is: ${username}



<div>
Children :
	<#list brochure.getChildrenPartDocForCurrentUser() as part> 
	 ${part.id}
	 	 ${part['dc:title']}
		 	 ${part['part:text_content']}
	</#list>
</div>
</body>
</html>