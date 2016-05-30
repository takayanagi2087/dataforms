package dataforms.app.dao.user;

import java.util.Map;

import dataforms.app.dao.enumeration.EnumOptionNameTable;
import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.dao.TableList;
import dataforms.field.base.FieldList;
import dataforms.field.sqlfunc.AliasField;

/**
 * ユーザ問い合わせクラス。
 *
 */
public class UserQuery extends Query {

	/**
	 * 指定されたユーザタイプを取得する問い合わせクラス。
	 *
	 */
	private class UserAttributeTypeQuery extends Query {
		/**
		 * コンストラクタ.
		 * @param type ユーザタイプ.
		 * @param langCode 言語コード.
		 */
		public UserAttributeTypeQuery(final String type, final String langCode) {
			UserAttributeTable tbl = new UserAttributeTable();
			EnumOptionNameTable ntbl = new EnumOptionNameTable();
			ntbl.setAlias("nm");
			FieldList fl = tbl.getFieldList();
			fl.add(new AliasField("attributeName", ntbl.getField("enumOptionName")));
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setJoinTableList(new TableList(ntbl));
			this.setCondition("m.user_attribute_type='" + type + "' and nm.lang_code='" + langCode + "' ");
		}
	}


	/**
	 * コンストラクタ。
	 * @param flist 問い合わせフォームのフィールドリスト。
	 * @param data 問い合わせフォームの入力データ。
	 */
	public UserQuery(final FieldList flist, final Map<String, Object> data) {
		this.setDistinct(true);
		// 該当するユーザ属性のサブクエリ
		SubQuery ua = new SubQuery(new UserAttributeQuery(data));
		ua.setAlias("ua");

		// ユーザレベルの名称を取得するサブクエリ
		SubQuery ul = new SubQuery(new UserAttributeTypeQuery("userLevel", (String) data.get("currentLangCode")));
		ul.setAlias("ul");

		UserInfoTable mtbl = new UserInfoTable();
		FieldList fl = mtbl.getFieldList();
		fl.add(new AliasField("userLevelName", ul.getField("attributeName")));
		this.setFieldList(fl);
		this.setMainTable(mtbl);

		this.setJoinTableList(new TableList(ua));
		this.setLeftJoinTableList(new TableList(ul));
		this.setQueryFormFieldList(flist);
		this.setQueryFormData(data);
	}
}

