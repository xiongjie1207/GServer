package com.gserver.utils.web;

/**
 * 算出页码的开始索引和结束索引
 */

public class PageIndex {

	/** 开始索引 */
	private long startindex;

	/** 结束索引 */
	private long endindex;

	public PageIndex(long startindex, long endindex) {
		this.startindex = startindex;
		this.endindex = endindex;
	}

	public long getStartindex() {
		return startindex;
	}

	public void setStartindex(long startindex) {
		this.startindex = startindex;
	}

	public long getEndindex() {
		return endindex;
	}

	public void setEndindex(long endindex) {
		this.endindex = endindex;
	}

	/**
	 * 算出页码的开始索引和结束索引
	 * 
	 * @param viewpagecount
	 *            页码数量
	 * @param currentPage
	 *            当前页数
	 * @param totalpage
	 *            总页数
	 * @return
	 */

	public static PageIndex getPageIndex(long viewpagecount, int currentPage, long totalpage) {
		long startpage = currentPage - (viewpagecount % 2 == 0 ? viewpagecount / 2 - 1 : viewpagecount / 2);
		long endpage = currentPage + viewpagecount / 2;
		if (startpage < 0) {
			startpage = 0;
			if (totalpage >= viewpagecount)
				endpage = viewpagecount;
			else
				endpage = totalpage;
		}

		if (endpage > totalpage) {
			endpage = totalpage;
			if ((endpage - viewpagecount) > 0)
				startpage = endpage - viewpagecount + 1;
			else
				startpage = 0;
		}
		if(startpage>endpage){
			startpage=endpage;
		}
		return new PageIndex(startpage, endpage);

	}
}