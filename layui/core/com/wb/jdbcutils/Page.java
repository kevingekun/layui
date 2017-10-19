package com.wb.jdbcutils;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable{
	private int pagesize;
	private int pageno;
	private int totalrows;
	private int pagetotalcount;
	private List result;
	
	public Page(){}
	public Page(int pageno, int pagesize, int totalrows, int pagetotalcount){
		this.pageno = pageno;
		this.pagesize = pagesize;
		this.totalrows = totalrows;
		this.pagetotalcount = pagetotalcount;
	}
	
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getPageno() {
		return pageno;
	}
	public void setPageno(int pageno) {
		this.pageno = pageno;
	}
	public int getTotalrows() {
		return totalrows;
	}
	public void setTotalrows(int totalrows) {
		this.totalrows = totalrows;
	}
	public int getPagetotalcount() {
		return pagetotalcount;
	}
	public void setPagetotalcount(int pagetotalcount) {
		this.pagetotalcount = pagetotalcount;
	}
	public List getResult() {
		return result;
	}
	public void setResult(List result) {
		this.result = result;
	}
	
}
