package dataforms.app.dao.user;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.app.dao.enumeration.EnumOptionNameTable;
import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.sqlfunc.AliasField;
import dataforms.util.UserAdditionalInfoTableUtil;
import net.arnx.jsonic.JSON;

/**
 * ユーザ問い合わせクラス。
 *
 */
public class UserQuery extends Query {


	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(UserQuery.class);

	/**
	 * 指定されたユーザタイプを取得する問い合わせクラス。
	 *
	 */
	private static class UserAttributeTypeQuery extends Query {
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


	// TODO:クラス名が重複しているので、クラス名の変更を検討。
	/**
	 * ユーザ属性副問合せ。
	 */
	public static class UserAttributeSubQuery extends SubQuery {
		/**
		 * コンストラクタ。
		 * @param type ユーザ属性コード。
		 * @param langCode 言語コード。
		 */
		public UserAttributeSubQuery(final String type, final String langCode) {
			super(new UserAttributeTypeQuery(type, langCode));
		}
	}

	/**
	 * コンストラクタ。
	 * @param flist 問い合わせフォームのフィールドリスト。
	 * @param data 問い合わせフォームの入力データ。
	 */
	public UserQuery(final FieldList flist, final Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<String> atlist = (List<String>) data.get("userAttributeList");
		logger.debug("atlist=" + JSON.encode(atlist));
		this.setDistinct(true);
		UserInfoTable mtbl = new UserInfoTable();
		FieldList fl = new FieldList();
		fl.addAll(mtbl.getFieldList());
		// 該当するユーザ属性のサブクエリ
		SubQuery ua = new SubQuery(new UserAttributeQuery(data));
		ua.setAlias("ua");
		// 追加情報テーブルが存在する場合、問合せに組み込む
		Table atable = null;
		TableList tl = new TableList();
		try {
			Class<? extends Table> clazz = UserAdditionalInfoTableUtil.getUserAdditionalInfoTable();
			if (clazz != null) {
				atable = clazz.newInstance();
				atable.setAlias("ai");
				tl.add(atable);
				for (Field<?> f: atable.getFieldList()) {
					if (UserAdditionalInfoTableUtil.isExcludedField(f)) {
						continue;
					}
					fl.addField(f);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// ユーザレベルの名称を取得するサブクエリ
		int idx = 0;
		for (String at: atlist) {
			UserAttributeSubQuery ul = new UserAttributeSubQuery(at, (String) data.get("currentLangCode"));
			ul.setAlias(at);
			tl.add(ul);
			fl.add(new AliasField("attribute" + (idx++), ul.getField("attributeName")));
		}

		this.setFieldList(fl);
		this.setMainTable(mtbl);

		this.setJoinTableList(new TableList(ua));
		this.setLeftJoinTableList(tl);
		this.setQueryFormFieldList(flist);
		this.setQueryFormData(data);
	}
}

