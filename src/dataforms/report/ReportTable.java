package dataforms.report;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;

/**
 * レポートテーブルクラス。
 */
public class ReportTable {

	/**
	 * Logger.
	 */
//	private static Logger log = Logger.getLogger(HtmlTable.class.getName());

	/**
	 * テーブルID。
	 */
	private String id = null;

	/**
	 * フィールドリスト.
	 */
	private FieldList fieldList = new FieldList();


	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param flist フィールドリスト。
	 */
	public ReportTable(final String id, final FieldList flist) {
		this.setId(id);
		this.fieldList.addAll(flist);
	}


	/**
	 * コンストラクタ。
	 * @param id ID。
	 * @param flist フィールドリスト。
	 */
	public ReportTable(final String id, final Field<?>... flist) {
		this.setId(id);
		for (Field<?> f : flist) {
			this.fieldList.add(f);
		}
	}


	/**
	 * フィールドリストを取得します。
	 * @return  フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}

	/**
	 * テーブルIDを取得します。
	 * @return テーブルID。
	 */
	public String getId() {
		return id;
	}

	/**
	 * テーブルIDを設定します。
	 * @param id テーブルID。
	 */
	public void setId(final String id) {
		this.id = id;
	}



}
