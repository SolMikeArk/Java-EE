package com.ibm.library.servlets;

import java.io.Serializable;

import com.ibm.library.model.LoanedCopy;

/**
 *
 * Bean used to format status message based on properties in item
 * 
 */
public class RenewalStatusBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4704631600465109145L;
	private LoanedCopy item;
	private String color = "green";
	

	public String getStatusMarkup() {
		StringBuffer sb = new StringBuffer("");
		String renewMessage = item.getRenewMessage();
		if (item != null && item.isRenewRequested() && item.isRenewAccomplished()) {
			// Build Renewed message with bold colored font
			sb.append("<b>");
			sb.append("<font color =\"");
			sb.append(color);
			sb.append("\">");
			sb.append((renewMessage.equals(""))
					? "Renewed"
					: renewMessage);
			sb.append("</font>");
			sb.append("</b>");
		} else {
			// Prevent table cells with no data (must have a least a non-breaking space)
			sb.append((renewMessage.equals(""))
					? "&nbsp;"
					: renewMessage);
		}
		return sb.toString();
	}

	public String getColor() {
		return color;
	}
	
	public LoanedCopy getItem() {
		return item;
	}
	
	public void setItem(LoanedCopy aItem) {
		item = aItem;
	}

	public void setColor(String aColor) {
		color = aColor;
	}

}
