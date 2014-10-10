<@extends src="base.ftl">

<@block name="content">

<div class="alert alert-info" role="alert">Brochures you have access to</div>
<ul>
<#list availableHandbooks as handbook>
	<li>	
		<a href="${Context.getBaseURL()}${Context.getBasePath()}/brochure/${handbook.id}">${handbook.title}</a>
	</li>
</#list>
</ul>
</@block>
</@extends>
