package dataforms.app.dao.enumeration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.app.field.enumeration.EnumTypeNameField;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.field.base.Field.MatchType;
import dataforms.field.base.FieldList;
import dataforms.field.sqlfunc.AliasField;
import dataforms.field.sqlfunc.SqlField;
import dataforms.util.StringUtil;


// TODO:EnumGrpupTableに登録する列挙型は全言語用意する必要があるが、その制限をなくしたい。
/**
 * 列挙型関連テーブルアクセスクラス。
 * <pre>
 * &lt;select&gt;タグの等の選択肢を管理するテーブル群をアクセスするためのクラスです。
 * </pre>
 *
 */
public class EnumDao extends Dao {

    /**
     * Logger.
     */
    //private static Logger log = Logger.getLogger(EnumDao.class.getName());


	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public EnumDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * 有効な列挙型を取得する問い合わせクラスです。
	 *
	 */
	private static class EnumTypeQuery extends Query {
		/**
		 * コンストラクタ.
		 */
		public EnumTypeQuery() {
			EnumOptionTable mtbl = new EnumOptionTable();
			this.setDistinct(true);
			this.setFieldList(new FieldList(mtbl.getEnumTypeCodeField()));
			this.setMainTable(mtbl);
		}
	}

	/**
	 * 列挙型オプションの問い合わせクラスです。
	 *
	 */
	private static class EnumGroupQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data パラメータ。
		 */
		public EnumGroupQuery(final Map<String, Object> data) {
			EnumGroupTable mtbl = new EnumGroupTable();
			EnumTypeNameTable mntbl = new EnumTypeNameTable();
			// 取得フィールドの設定.
			this.setFieldList(new FieldList(
				new AliasField("value", mtbl.getEnumTypeCodeField())
				, new AliasField("name", mntbl.getEnumTypeNameField())
			));
			this.setMainTable(mtbl);
			this.setJoinTableList(new TableList(new SubQuery(new EnumTypeQuery()), mntbl));
			this.setQueryFormFieldList(new FieldList(mtbl.getEnumGroupCodeField(), mntbl.getLangCodeField()));
			this.setQueryFormData(data);
			this.setOrderByFieldList(new FieldList(mtbl.getSortOrderField()));
		}
	}

	/**
	 * 指定された列挙型グループの列挙型一覧を取得します。
	 * @param enumGroupCode 列挙型グルーブコード。
	 * @param langCode 言語コード。
	 * @return 列挙型の一覧。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTypeList(final String enumGroupCode, final String langCode) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		EnumGroupTable.Entity e = new EnumGroupTable.Entity(data);
		EnumTypeNameTable.Entity ne = new EnumTypeNameTable.Entity(data);
		/*
		data.put("enumGroupCode", enumGroupCode);
		data.put("langCode", langCode);
		*/
		e.setEnumGroupCode(enumGroupCode);
		ne.setLangCode(langCode);
		EnumGroupQuery mq = new EnumGroupQuery(data);
		List<Map<String, Object>> list = this.executeQuery(mq);
		if (list.size() == 0) {
			//data.put("langCode", "default");
			ne.setLangCode("default");
			list = this.executeQuery(mq);
		}
		return list;
	}


	/**
	 * 列挙型オプションの問い合わせクラスです。
	 *
	 */
	private static class OptionQuery extends Query {
		/**
		 * コンストラクタ.
		 * @param data パラメータ.
		 */
		public OptionQuery(final Map<String, Object> data) {
			EnumOptionTable mtbl = new EnumOptionTable();
			EnumOptionNameTable mntbl = new EnumOptionNameTable();
			// 取得フィールドの設定.
			this.setFieldList(new FieldList(
				new AliasField("value", mtbl.getEnumOptionCodeField())
				, new AliasField("name", mntbl.getEnumOptionNameField())
			));
			this.setMainTable(mtbl);
			this.setJoinTableList(new TableList(mntbl));
			this.setQueryFormFieldList(new FieldList(mtbl.getEnumTypeCodeField(), mtbl.getEnumOptionCodeField(), mntbl.getLangCodeField()));
			this.setQueryFormData(data);
			this.setOrderByFieldList(new FieldList(mtbl.getSortOrderField()));
		}
	}

	/**
	 * オプションリストを取得します。
	 * @param enumTypeCode 列挙型コード。
	 * @param langCode 言語コード。
	 * @return オプションリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getOptionList(final String enumTypeCode, final String langCode) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		EnumOptionTable.Entity e = new EnumOptionTable.Entity(data);
		EnumOptionNameTable.Entity ne = new EnumOptionNameTable.Entity(data);
/*		data.put("enumTypeCode", enumTypeCode);
		data.put("langCode", langCode);
*/		e.setEnumTypeCode(enumTypeCode);
		ne.setLangCode(langCode);
		OptionQuery mq = new OptionQuery(data);
		List<Map<String, Object>> list = this.executeQuery(mq);
		if (list.size() == 0) {
			//data.put("langCode", "default");
			ne.setLangCode("default");
			list = this.executeQuery(mq);
		}
		return list;
	}


	/**
	 * オプション名称を取得します。
	 * @param enumTypeCode 列挙型コード。
	 * @param enumOptionCode 列挙型オプションコード。
	 * @param langCode 言語コード。
	 * @return オプション名称。
	 * @throws Exception 例外。
	 */
	public String getOptionName(final String enumTypeCode, final String enumOptionCode, final String langCode) throws Exception {
		if (StringUtil.isBlank(enumOptionCode)) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		EnumOptionNameTable.Entity e = new EnumOptionNameTable.Entity(data);
		e.setEnumTypeCode(enumTypeCode);
		e.setEnumOptionCode(enumOptionCode);
		e.setLangCode(langCode);
		
		OptionQuery query = new OptionQuery(data);
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			String optname = (String) rec.get("name");
			return optname;
		} else {
			return null;
		}
	}
	
	/**
	 * 列挙型コードの一覧を取得する問合せです。
	 *
	 */
	public static class EnumTypeCodeQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EnumTypeCodeQuery() {
			this.setDistinct(true);
			EnumOptionTable table = new EnumOptionTable();
			this.setFieldList(new FieldList(table.getEnumTypeCodeField()));
			this.setMainTable(table);
		}
	}
	
	/**
	 * 列挙型コードと名称の表を取得する問合せです。
	 * 
	 * <pre>
	 * ブラウザの言語コードをlangCodeというパラメータに指定することによって、
	 * 言語に応じた名称を取得します。
	 * </pre>
	 *
	 */
	public static class EnumTypeNameQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EnumTypeNameQuery() {
			EnumTypeNameTable dt = new EnumTypeNameTable();
			dt.setAlias("dt");
			EnumTypeNameTable ct = new EnumTypeNameTable();
			ct.setAlias("ct");
			SubQuery table = new SubQuery(new EnumTypeCodeQuery()) {
				@Override
				public String getJoinCondition(final Table joinTable, final String alias) {
					if ("dt".equals(alias)) {
						return this.getLinkFieldCondition(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE, joinTable, alias, EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)
								+ " and dt.lang_code='default'";
					}
					if ("ct".equals(alias)) {
						return this.getLinkFieldCondition(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE, joinTable, alias, EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)
								+ " and ct.lang_code=:lang_code";
					}
					return super.getJoinCondition(joinTable, alias);
				}
			};
			FieldList flist = new FieldList();
			flist.addAll(table.getFieldList());
			flist.add(new SqlField(new EnumTypeNameField(), "(case when ct.enum_type_name is not null then ct.enum_type_name else dt.enum_type_name end)"));
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setLeftJoinTableList(new TableList(dt, ct));
		}
	}
	
	/**
	 * EnumTypeCodeのオートコンプリート用の問合せを実行します。
	 * @param text 入力テキスト。
	 * @param langCode 言語コード。
	 * @return オートコンプリート用のリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryEnumTypeAutocomplateList(final String text, final String langCode) throws Exception {
		EnumTypeNameQuery query = new EnumTypeNameQuery();
		FieldList flist = new FieldList();
		flist.addField(new EnumTypeCodeField()).setMatchType(MatchType.PART);
		query.setQueryFormFieldList(flist);
		EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity();
		e.setEnumTypeCode(text);
		e.setLangCode(langCode);
		query.setQueryFormData(e.getMap());
		return this.executeQuery(query);
	}
	
	/**
	 * 列挙型の情報を取得します。
	 * 
	 * @param enumTypeCode 列挙型コード。
	 * @param langCode 言語コード。
	 * @return ヒットしたデータマップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryEnumType(final String enumTypeCode, final String langCode) throws Exception {
		EnumTypeNameQuery query = new EnumTypeNameQuery();
		query.setQueryFormFieldList(new FieldList(query.getFieldList().get(EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)));
		EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity();
		e.setEnumTypeCode(enumTypeCode);
		e.setLangCode(langCode);
		query.setQueryFormData(e.getMap());
		Map<String, Object> ret = this.executeRecordQuery(query);
		if (ret == null) {
			EnumTypeNameTable.Entity re = new EnumTypeNameTable.Entity();
			re.setEnumTypeCode(enumTypeCode);
			re.setEnumTypeName("");
			ret = re.getMap();
		}
		return ret;
		
	}
}
