package com.gserver.utils.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * 分页显示对象
 * 
 * @param <T>
 */
public class PageView<T> implements Serializable {
	/** 分页数据 **/
	private List<T> records;
	/** 总页数 **/
	private long totalpage = 1;
	/** 当前页 **/
	private int currentpage = 0;
	/** 总记录数 **/
	private long totalrecord=1;
	/** 页码开始索引和结束索引 **/
	@JsonIgnore
	private PageIndex pageindex;
	/** 每页显示记录数 **/
	@JsonIgnore
	private int itemCount = 12;
	/** 页码数量 **/
	@JsonIgnore
	private int pagecode = 10;

	/** 要获取记录的开始索引 **/
	public int getFirstResult() {

		return (this.currentpage - 1) * this.itemCount;

	}

	public int getPagecode() {
		return pagecode;
	}

	public void setPagecode(int pagecode) {
		this.pagecode = pagecode;
	}

	public PageView(int currentpage,int itemCount) {
		this.itemCount = itemCount;
		this.currentpage = currentpage;
	}

	public long getTotalrecord() {
		return totalrecord;
	}

	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
		int page = (int) (this.totalrecord / this.itemCount);
		setTotalpage(page);
	}

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}

	public PageIndex getPageindex() {
		return pageindex;
	}

	public long getTotalpage() {
		return totalpage;
	}

	private void setTotalpage(long totalpage) {
		this.totalpage = totalpage;
		if(currentpage>totalpage){
			currentpage= (int) totalpage;
		}
		this.pageindex = PageIndex.getPageIndex(pagecode, currentpage, totalpage);
	}

	/**
	 * 每页显示记录数
	 * 
	 * @return
	 */
	public int getItemCount() {
		return itemCount;
	}

	public int getCurrentpage() {
		return currentpage;
	}

	public void bindModel(ModelAndView model) {
		model.addObject("pageView",this);
	}
	public void bindModel(HttpServletRequest request){
		request.setAttribute("pageView",this);
	}
}