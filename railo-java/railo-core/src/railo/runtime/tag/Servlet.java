package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* Executes a Java servlet on a JRun engine. This tag is used in conjunction with the 
*   cfserletparam tag, which passes data to the servlet.
*
*
*
**/
public final class Servlet extends TagImpl {

	private boolean debug;
	private String code;
	private boolean writeoutput;
	private double timeout;
	private String jrunproxy;


	/**
	* constructor for the tag class
	 * @throws ExpressionException
	**/
	public Servlet() throws ExpressionException {
		throw new ExpressionException("tag cfservlet is deprecated");
	}

	/** set the value debug
	*  Boolean specifying whether additional information about the JRun connection status and 
	* 		activity is to be written to the JRun error log
	* @param debug value to set
	**/
	public void setDebug(boolean debug)	{
		this.debug=debug;
	}

	/** set the value code
	*  The class name of the Java servlet to execute.
	* @param code value to set
	**/
	public void setCode(String code)	{
		this.code=code;
	}

	/** set the value writeoutput
	* @param writeoutput value to set
	**/
	public void setWriteoutput(boolean writeoutput)	{
		this.writeoutput=writeoutput;
	}

	/** set the value timeout
	*  Specifies how many seconds JRun waits for the servlet to complete before timing out.
	* @param timeout value to set
	**/
	public void setTimeout(double timeout)	{
		this.timeout=timeout;
	}

	/** set the value jrunproxy
	* @param jrunproxy value to set
	**/
	public void setJrunproxy(String jrunproxy)	{
		this.jrunproxy=jrunproxy;
	}


	@Override
	public int doStartTag()	{
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		debug=false;
		code="";
		writeoutput=false;
		timeout=0d;
		jrunproxy="";
	}
}