<#-- 总页数，当前页 -->
<#macro pagination pageView>
	<script type="text/javascript">
        function pageinationView(pageNum) {
            document.getElementById("pageNum").value = pageNum;
            document.getElementById("paginationForm").submit();
        }
    </script>
	<div>
        <label>共${pageView.totalpage+1}页，约${pageView.totalrecord}条数据</label>

		<#if (pageView.currentpage > 0)>
			<a href="javascript:pageinationView(0)" title="首页"><span>首页</span></a>
			<a href="javascript:pageinationView(${pageView.currentpage-1})" title="上一页"><span>上一页</span></a>
        <#else>
			<span>首页</span>
			<span>上一页</span>
        </#if>
		<#list pageView.pageindex.startindex..pageView.pageindex.endindex as index>
            <#if pageView.currentpage == index>
	        	[${index+1}]
            <#else>
	            <a href="javascript:pageinationView(${index})" title="第${index+1}页">${index+1}</a>
            </#if>
        </#list>
	    
		<#if pageView.currentpage < pageView.totalpage>
			<a href="javascript:pageinationView(${pageView.currentpage + 1})" title="下一页"><span>下一页</span></a>
			<a href="javascript:pageinationView(${pageView.totalpage})" title="未页"><span>未页</span></a>
        <#else>
			<span>下一页</span>
			<span>未页</span>
        </#if>
    </div>
</#macro>