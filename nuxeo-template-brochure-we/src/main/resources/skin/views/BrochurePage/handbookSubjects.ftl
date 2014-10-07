<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<div style="margin: 10px 10px 10px 10px">
<p>
	Welcome to the brochure browser. </br>
	Here is the list of subjects available in the brochure you selected.
</p>

<form method="Post" action="${This.path}/submit/${id}" enctype="application/x-www-form-urlencoded">
<#list subjects as subject>
	<input type="checkbox" name="subject" value="${subject}"> ${subject}</br>
</#list>
    <button type="submit" class="btn btn-default" value="Submit">Submit</button>

</form>
</div>

</@block>
</@extends>
