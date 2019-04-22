package com.ibm.library.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ibm.library.datastore.exceptions.SystemUnavailableException;
import com.ibm.library.model.LoanedCopy;
import com.ibm.library.model.Patron;

/**
 * Servlet implementation class for Servlet: RenewItem
 * 
 */
public class RenewItems extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3164422802688234203L;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}


	private void processRequest(
		HttpServletRequest req,
		HttpServletResponse resp)
		throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		ServletContext context = getServletContext();

		// Check to see if patron has been logged in by checking 
		// for existence of patron object in the session.
		Patron patron = null;
		if (session != null) {
			patron = (Patron) session.getAttribute("PATRON");
		}

		// If patron logged in retrieve renewed items and 
		// forward to list items jsp
		// otherwise forward to login page.
		if (patron != null) {

			try {
				// Get list of items the patron has checked out 
				// Note: the bean that contains the list was saved 
				// in session by ProcessListItems
				LoanedCopyListBean listBean =
					(LoanedCopyListBean) session.getAttribute("listitems");
				ArrayList<LoanedCopy> loanList = listBean.getLoanedCopyList();

				// Get the list  of items to be renewed	from the form
				String[] renewList = req.getParameterValues("RENEW_ITEM");
				
				// Mark LoanedCopy objects in loanlist to be renewed if
				// they have an id (String format) in the renew list
				markRenewed(renewList, loanList);

				// Renew the items in the list that have been
				// marked with the method above..
				patron.renew(loanList);

				// Put the loaned item list in the loaned item list bean, 
				// save that bean in the session attribute and 
				// forward to the list items JSP
				listBean.setLoanedCopyList(loanList);
				session.setAttribute("listitems", listBean);
				context.getRequestDispatcher("/WEB-INF/jsp/ListItems.jsp").forward(
					req,
					resp);

			} catch (SystemUnavailableException e) {
				// System error - report error 500 and message
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"System unavaliable exception in RenewItems function");
			}

		} else {
			// Not logged in, so set destination to login servlet
			context.getRequestDispatcher("/Login").forward(req, resp);
		}
	}

	private void markRenewed(String[] renewList, ArrayList<LoanedCopy> loanList) {
		// Find each item on the renew list in the list 
		// of checked out items and mark it as being requested for renew
		if (renewList != null) {
			for (int i = 0; i < renewList.length; i++) {
				Iterator<LoanedCopy> e = loanList.iterator();
				while (e.hasNext()) {
					LoanedCopy copy = e.next();
					int itemId = copy.getItemId();
					//Renew list items are String,so convert 
					// to int to compare with the itemId
					if (Integer.valueOf(renewList[i]).intValue() == itemId) {
						copy.setRenewRequested(true);
						break;
					}
				}
			}
		}
	}

}
