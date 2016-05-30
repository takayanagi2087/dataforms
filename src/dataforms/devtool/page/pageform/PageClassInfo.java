package dataforms.devtool.page.pageform;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import dataforms.controller.Page;
import dataforms.dao.Dao;
import dataforms.dao.Table;

/**
 * ページクラス情報取得クラス。
 *
 */
public class PageClassInfo {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(PageClassInfo.class);
	/**
	 * ページクラス。
	 */
	private Page page = null;

	/**
	 * コンストラクタ。
	 * @param page ページクラス。
	 */
	public PageClassInfo(final Page page) {
		this.page = page;
	}

	/**
	 * メソッドの値を取得します。
	 * @param p ページ。
	 * @param methodName メソッド名。
	 * @return メソッドの戻り値。
	 * @throws Exception 例外。
	 */
	private Object getMethodValue(final Page p, final String methodName) throws Exception {
		Object ret = null;
		try {
			Method m = p.getClass().getMethod(methodName);
			if (m != null) {
				Class<?> tblcls = (Class<?>) m.invoke(p);
				if (tblcls != null) {
					ret = tblcls;
				}
			}
		} catch (NoSuchMethodException e) {
			log.warn("Page class '" + this.page.getClass().getName() + "' does no have '" + methodName + "' method.");
		}
		return ret;
	}

	/**
	 * メソッドの文字列値を取得します。
	 * @param p ページ。
	 * @param methodName メソッド名。
	 * @return メソッドの戻り値。
	 * @throws Exception 例外。
	 */
	private Object getMethodStringValue(final Page p, final String methodName) throws Exception {
		Object ret = null;
		try {
			Method m = p.getClass().getMethod(methodName);
			if (m != null) {
				String value = (String) m.invoke(p);
				if (value != null) {
					ret = value;
				}
			}
		} catch (NoSuchMethodException e) {
			log.warn("Page class '" + this.page.getClass().getName() + "' does no have '" + methodName + "' method.");
		}
		return ret;
	}

	/**
	 * ページの操作するテーブルクラスを取得します。
	 * @return ページの操作するテーブルクラス。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Table> getTableClass() throws Exception {
		return (Class<? extends Table>) getMethodValue(this.page, "getTableClass");
	}

	/**
	 * テーブル操作を行うDaoクラスを取得します。
	 * @return テーブル操作を行うDaoクラス。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Dao> getDaoClass() throws Exception {
		return (Class<? extends Dao>) getMethodValue(this.page, "getDaoClass");
	}

	/**
	 * Pageが配置されるパスを返します。
	 * @return Pageが配置されるパス。
	 * @throws Exception 例外。
	 */
	public String getFunctionPath() throws Exception {
		return (String) getMethodStringValue(this.page, "getFunctionPath");
	}

}
