<#macro nav pageView>
	<script type="text/javascript">
        function pageinationView(pageNum) {
            document.getElementById("pageNum").value = pageNum;
            document.getElementById("paginationForm").submit();
        }
    </script>
	<div>
        <label>共${pageView.totalpage}页，每页${pageView.itemCount}条数据，约${pageView.totalrecord}条数据</label>

		<#if (pageView.currentpage > 0)>
			<a href="javascript:pageinationView(0)" title="首页"><span>首页</span></a>
			<a href="javascript:pageinationView(${pageView.currentpage-1})" title="上一页"><span>上一页</span></a>
        <#else>
			<span>首页</span>
			<span>上一页</span>
        </#if>
        <#if (pageView.pageindex.startindex == pageView.pageindex.endindex)&&(pageView.pageindex.endindex == 0)>
            [0]
        <#else >
		<#list pageView.pageindex.startindex..pageView.pageindex.endindex-1 as index>
            <#if pageView.currentpage == index>
	        	[${index+1}]
            <#else>
	            <a href="javascript:pageinationView(${index})" title="第${index+1}页">${index+1}</a>
            </#if>
        </#list>
        </#if>
	    
		<#if pageView.currentpage < pageView.totalpage-1>
			<a href="javascript:pageinationView(${pageView.currentpage + 1})" title="下一页"><span>下一页</span></a>
			<a href="javascript:pageinationView(${pageView.totalpage-1})" title="未页"><span>未页</span></a>
        <#else>
			<span>下一页</span>
			<span>未页</span>
        </#if>
    </div>
</#macro>

<#macro form action name class="">
	<form action="${action}" class="${class}" id="paginationForm" method="post">
        <input type="hidden" name="${name}" id="pageNum"/>
        <#nested>
    </form>
</#macro>