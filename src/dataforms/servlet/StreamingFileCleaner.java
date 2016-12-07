package dataforms.servlet;

import java.io.File;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import dataforms.field.common.FileField;

/**
 * セッションタイムアウト時に残っているストリーミングファイルの残骸を削除するための
 * セッションリスナークラスです。
 *
 */
@WebListener
public class StreamingFileCleaner implements HttpSessionListener {

	/**
	 * Logger.
	 */
	private Logger log = Logger.getLogger(StreamingFileCleaner.class);
	
	@Override
	public void sessionCreated(final HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		HttpSession session = se.getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (Pattern.matches("^" + FileField.DOWNLOADING_FILE + ".+", name)) {
				log.debug("attribute name=" + name);
				String filename = (String) session.getAttribute(name);
				log.debug("filename=" + filename);
				File f = new File(filename);
				f.delete();
			}
		}
	}

}
