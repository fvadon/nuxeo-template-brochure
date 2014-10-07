<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<div style="margin: 10px 10px 10px 10px">
<p>
	Welcome to the brochure browser. </br>
	Here is the list of brochures you have access to:
</p>

<#list availableHandbooks as handbook>
	
	<div>
		<a href="${Context.getBaseURL()}${Context.getBasePath()}/brochure/${handbook.id}">${handbook.title}</a>
	</div>
</#list>

</div>

</@block>
</@extends>
