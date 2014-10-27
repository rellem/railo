package railo.commons.sql;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import railo.commons.io.SystemUtil;
import railo.commons.lang.ParserString;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.sql.BlobImpl;
import railo.runtime.type.sql.ClobImpl;

public class SQLUtil {

	private static final String ESCAPE_CHARS="\\{}[]^$*.?+";
	
	public static Pattern pattern(String pstr,boolean ignoreCase) {
		
		char[] carr = pstr.toCharArray();
		char c;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<carr.length;i++) {
			c=carr[i];
			if(ESCAPE_CHARS.indexOf(c)!=-1) {
				sb.append('\\');
				sb.append(c);
			}
			else if(c=='%') {
				sb.append(".*");
			}
			else if(c=='_') {
				sb.append(".");
			}
			else {
				if(ignoreCase) {
					sb.append('[');
					sb.append(Character.toLowerCase(c));
					sb.append('|');
					sb.append(Character.toUpperCase(c));
					sb.append(']');
				}
				else sb.append(c);
			}
		}
		return Pattern.compile(sb.toString());
	}
	
	public static boolean match(Pattern pattern, String string) {
		return pattern.matcher(string).matches();
		
	}

	public static String removeLiterals(String sql) {
		if(StringUtil.isEmpty(sql)) return sql;
		return removeLiterals(new ParserString(sql),true);
	}

	private static String removeLiterals(ParserString ps, boolean escapeMysql) {
		StringBuilder sb=new StringBuilder();
		char c,p=(char)0;
		boolean inside=false;
		do {
			c=ps.getCurrent();
			
			if(c=='\''){
				if(inside){
					if(escapeMysql && p=='\\'){}
					else if(ps.hasNext() && ps.getNext()=='\'')ps.next();
					else inside=false;
				}
				else {
					inside=true;
				}
			}
			else {
				if(!inside && c!='*' && c!='=' && c!='?')sb.append(c);
			}
			p=c;
			ps.next();
		}while(!ps.isAfterLast());
		if(inside && escapeMysql) {
			ps.setPos(0);
			return removeLiterals(ps, false);
		}
		return sb.toString();
	}

	/**
	 * create a blog Object
	 * @param conn
	 * @param value
	 * @return
	 * @throws PageException
	 * @throws SQLException 
	 */
	public static Blob toBlob(Connection conn, Object value) throws PageException, SQLException {
		if(value instanceof Blob) return (Blob) value;
		
		// Java >= 1.6
		if(SystemUtil.JAVA_VERSION>=SystemUtil.JAVA_VERSION_1_6) {
			try {
				Blob blob = conn.createBlob();
				blob.setBytes(1, Caster.toBinary(value));
				return blob;
			}
			catch(Throwable t){
				return BlobImpl.toBlob(value);
			}
		}
		
		// Java < 1.6
		if(isOracle(conn)){
			Blob blob = OracleBlob.createBlob(conn,Caster.toBinary(value),null);
			if(blob!=null) return blob;
		}
		return BlobImpl.toBlob(value);
		
		
	}

	/**
	 * create a clob Object
	 * @param conn
	 * @param value
	 * @return
	 * @throws PageException 
	 * @throws SQLException 
	 */
	public static Clob toClob(Connection conn, Object value) throws PageException, SQLException {
		if(value instanceof Clob) return (Clob) value;
		
		// Java >= 1.6
		if(SystemUtil.JAVA_VERSION>=SystemUtil.JAVA_VERSION_1_6) {
			Clob clob = conn.createClob();
			clob.setString(1, Caster.toString(value));
			return clob;
		}
		
		// Java < 1.6
		if(isOracle(conn)){
			Clob clob = OracleClob.createClob(conn,Caster.toString(value),null);
			if(clob!=null) return clob;
		}
		return ClobImpl.toClob(value);
	}
	
	public static boolean isOracle(Connection conn) {
		return StringUtil.indexOfIgnoreCase(conn.getClass().getName(), "oracle")!=-1;
	}

	public static void closeEL(Statement stat) {
		if(stat!=null){
			try {
				stat.close();
			} catch (SQLException e) {}
		}
	}
}
