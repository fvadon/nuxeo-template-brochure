<@extends src="base.ftl">

<@block name="content">

<div class="alert alert-info" role="alert">Available subjets in selected brochure</div>

<form method="Post" action="${This.path}/submit/${id}" enctype="application/x-www-form-urlencoded">
<#list subjects as subject>
	<input type="checkbox" name="subject" value="${subject}"> ${subject}</br>
</#list>
</br>
    <button type="submit" class="btn btn-default" value="Submit">Submit</button>

</form>

</@block>
</@extends>
