<%@ tag body-content="empty"%>
<%@ attribute name="loanedCopy" required="true" 
   type="com.ibm.library.model.LoanedCopy"" %>

<a class="details" href="#">?<span>Times Renewed = ${loanedCopy.timesRenewed}</span></a>

