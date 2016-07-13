package dataforms.devtool.field.common;

import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import dataforms.dao.Query;

/**
 * 問合せクラス名フィールドクラス。
 *
 */
public class QueryClassNameField extends SimpleClassNameField {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(QueryClassNameField.class);
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "問合せクラス名";
	/**
	 * コンストラクタ。
	 */
	public QueryClassNameField() {
		this.setBaseClass(Query.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public QueryClassNameField(final String id) {
		super(id);
		this.setBaseClass(Query.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
	}
	
	@Override
	protected boolean isExcetionClass(final String classname) throws Exception {
		log.debug("classname=" + classname);
		@SuppressWarnings("unchecked")
		Class<? extends Query> c = (Class<? extends Query>) Class.forName(classname);
		if ((c.getModifiers() & Modifier.ABSTRACT) != 0) {
			return true;
		}
		if (c.getName().indexOf("$") > 0) {
			// インナークラスは除外。
			return true;
		}
		try {
			c.getConstructor();
		} catch (NoSuchMethodException e) {
			// デフォルトコンストラクタが存在しない場合はヒットさせない。
			return true;
		}
		boolean ret = super.isExcetionClass(classname);
		return ret;
	}
}
