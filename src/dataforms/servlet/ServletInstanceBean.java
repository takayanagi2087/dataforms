package dataforms.servlet;

import org.apache.log4j.Logger;

/**
 * サーブレットインスタンス設定クラスです。
 * <pre>
 * サーブレット初期化、破壊時に特殊な処理科必要な場合、このクラスから派生したクラスを作成し、destroyメソッドを実装します。
 * その派生クラスをweb.xmlのservlet-instance-beanに設定すれば、派生クラスのdestroyメソッドが呼び出されます。
 * </pre>
 */
public class ServletInstanceBean {
	
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(ServletInstanceBean.class);
	
	
	/**
	 * サーブレット初期化メソッド。
	 * <pre>
	 * DataFormsServlet#initメソットの中で呼び出されます。
	 * </pre>
	 * @throws Exception 例外。
	 */
	public void init() throws Exception {
		log.info("init DataFormsServlet instance.");
		
	}
	
	/**
	 * サーブレット破壊メソッド。
	 * <pre>
	 * DataFormsServlet#destroyメソットの中で呼び出されます。
	 * </pre>
	 * @throws Exception 例外。
	 */
	public void destroy()  throws Exception {
		log.info("destroy DataFormsServlet instance.");
	}
}
