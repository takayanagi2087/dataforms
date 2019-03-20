package dataforms.app.dao.enumeration;

import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.field.base.FieldList;

/**
 * 特定の列挙型名称のサブクエリ。
 */
public class EnumOptionNameSubQuery extends SubQuery {
	/**
	 * QueryFormで指定した条件で行う問い合わせクラスです。
	 */
	private static class EnumOptionNameTableQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param enumTypeCode 列挙型コード。
		 * @param langCode 言語コード。
		 */
		public EnumOptionNameTableQuery(final String enumTypeCode, final String langCode) {
			EnumOptionNameTable table = new EnumOptionNameTable();
			FieldList flist = new FieldList();
			flist.addAll(table.getFieldList());
			flist.marge(table.getFieldList());
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setCondition("m.enum_type_code='" + enumTypeCode + "' and m.lang_code='" + langCode + "'");
		}
	}

	/**
	 * コンストラクタ。
	 * @param enumTypeCode 列挙型コード。
	 * @param langCode 言語コード。
	 */
	public EnumOptionNameSubQuery(final String enumTypeCode, final String langCode) {
		super(new EnumOptionNameTableQuery(enumTypeCode, langCode));
	}
}
