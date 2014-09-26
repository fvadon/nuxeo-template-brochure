
The doc has UID ${doc.id} and the current user is: ${username}

Children :
	<#list brochure.getChildrenPartDocForCurrentUser() as part> 
	 ${part.id}
	 	 ${part['dc:title']}
		 	 ${part['part:text_content']}
	</#list>
brochure.getDocument test: (the line is added by the test iteself as the id is not know yet.)