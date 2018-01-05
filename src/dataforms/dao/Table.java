package dataforms.dao;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationError;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.CreateTimestampField;
import dataforms.field.common.CreateUserIdField;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.RecordIdField;
import dataforms.field.common.UpdateTimestampField;
import dataforms.field.common.UpdateUserIdField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.ClassFinder;
import dataforms.util.FileUtil;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;


/**
 * テーブルの基本クラスです。
 *
 */
public class Table  {
	/**
	 * Log.
	 */
	private static Logger logger = Logger.getLogger(Table.class.getName());


	/**
	 * フィールドリスト。
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * 主キーフィールドのリスト。
	 */
	private FieldList pkFieldList = new FieldList();

	/**
	 * テーブルコメント。
	 */
	private String comment = null;

	/**
	 * 別名。
	 */
	private String alias = null;

	/**
	 * ID自動生成フラグ。
	 */
	private boolean autoIncrementId = false;


	/**
	 * シーケンスの初期値。
	 * TODO:MySQLのAuto_incrementは0を設定できないので、1開始の方が良いかも。
	 */
	private Long sequenceStartValue = Long.valueOf(0);
	/**
	 * コンストラクタ。
	 */
	public Table() {

	}

	/**
	 * テーブルクラスの名前を指定して、インスタンスを作成します。
	 * @param classname クラス名。
	 * @return テーブルクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	public static Table newInstance(final String classname) throws Exception {
		try {
			Class<?> cls = Class.forName(classname);
			if ((cls.getModifiers() & Modifier.PUBLIC) != 0 && (cls.getModifiers() & Modifier.ABSTRACT) == 0) {
				Table tbl = (Table) cls.newInstance();
				return tbl;
			} else {
				return null;
			}
		} catch (IllegalAccessException ex) {
			logger.error(ex.getMessage(), ex);
			throw new ApplicationError(ex);
		}
	}

	/**
	 * IDフィールドを取得します。
	 * <pre>
	 * ID自動生成フラグがtrueの場合、IDフィールド情報を取得するために呼び出されます。
	 * 通常PKの先頭項目を返します。
	 * </pre>
	 * @return IDフィールド。
	 */
	public Field<?> getIdField() {
		if (this.getPkFieldList() != null) {
			return this.getPkFieldList().get(0);
		} else {
			return this.getFieldList().get(0);
		}
	}


	/**
	 * テーブルの別名を取得します。
	 * <pre>
	 * テーブルの別名が明示的に設定されない場合、問い合わせSql生成時に自動的に割り当てられます。
	 * </pre>
	 * @return テーブルの別名。
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * テーブルの別名を設定します。
	 * <pre>
	 * 同一のテーブルを複数回Joinする場合、異なる別名を指定した別インスタンスを作成し、
	 * getJoinConditionの条件生成の判定に利用します。
	 * </pre>
	 * @param alias テーブルの別名。
	 */
	public void setAlias(final String alias) {
		this.alias = alias;
	}

	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return this.fieldList;
	}

	/**
	 * フィールドリストを設定します。
	 * @param flist フィールドリスト。
	 */
	protected void setFieldList(final FieldList flist) {
		this.fieldList = flist;
	}

	/**
	 * 主キーフィールドリストを取得します。
	 * @return 主キーフィールドリスト。
	 */
	public final FieldList getPkFieldList() {
		return this.pkFieldList;
	}

	/**
	 * 更新対象フィールドリストを取得します。
	 * <pre>
	 * PK以外のフィールドのリストを取得します。
	 * </pre>
	 * @return 更新対象フィールドリスト。
	 */
	public final FieldList getUpdateFieldList() {
		FieldList pklist = this.getPkFieldList();
		FieldList list = new FieldList();
		for (Field<?> f: this.getFieldList()) {
			if (pklist.get(f.getId()) != null) {
				continue;
			}
			list.add(f);
		}
		return list;
	}

	/**
	 * フィールドを追加します。
	 * @param field 追加するフィールドのインスタンス。
	 * @return 追加したフィールド。
	 */
	public final  Field<?> addField(final Field<?> field) {
		this.fieldList.add(field);
		field.setTable(this);
		return field;
	}


	/**
	 * テーブルコメントを取得します。
	 * @return テーブルコメント。
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * テーブルコメントを設定します。
	 * @param comment テーブルコメント。
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * フィールドを追加します。
	 * @param field 追加するフィールドのインスタンス。
	 * @return 追加したフィールド。
	 */
	public final Field<?> addPkField(final Field<?> field) {
		this.pkFieldList.add(field);
		this.fieldList.add(field);
		field.setTable(this);
		field.setNotNull(true);
		return field;
	}

	/**
	 * 更新情報フィールドを追加します。
	 * <pre>
	 * 以下のフィールドが追加されます。
	 * </pre>
	 * <table>
	 *  <caption>追加されるフィールド一覧</caption>
	 * 	<thead>
	 * 		<tr>
	 * 			<th>フィールドID</th><th>説明</th>
	 * 		</tr>
	 * 	</thead>
	 * 	<tbody>
	 * 		<tr>
	 * 			<td>createUserId</td><td>作成者ユーザID</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>createTimestamp</td><td>作成時刻</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>updateUserId</td><td>更新者ユーザID</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>updateTimestamp</td><td>更新時刻</td>
	 * 		</tr>
	 * 	</tbody>
	 * </table>
	 */
	public void addUpdateInfoFields() {
		this.addField(new CreateUserIdField()).setNotNull(true);
		this.addField(new CreateTimestampField()).setNotNull(true);
		this.addField(new UpdateUserIdField()).setNotNull(true);
		this.addField(new UpdateTimestampField()).setNotNull(true);
	}


	/**
	 * テーブル名称を取得します。
	 * <pre>
	 * dataforms.dao.Tableから直接派生したクラスの名前(getSimpleName()で取得)から
	 * クラス名の末尾のTableを除いた文字列をSnake記法に変換した文字列を返します。
	 * </pre>
	 * @return テーブル名称。
	 */
	public String getTableName() {
		Class<?> cls = this.getClass();
		String clsname = cls.getSimpleName();
		while (!Pattern.matches(".+\\.dao\\.Table$", cls.getSuperclass().getName())) {
			cls = cls.getSuperclass();
			clsname = cls.getSimpleName();
		}
		return StringUtil.camelToSnake(clsname.replaceAll("Table$", ""));
	}


	/**
	 * テーブルに対応するSquence名を取得します。
	 * <pre>
	 * テーブル名に"_seq"を追加した文字列を返します。
	 * </pre>
	 * @return テーブルに対応するSquence名。
	 */
	public String getSequenceName() {
		return this.getTableName() + "_seq";
	}

	/**
	 * バックアップテーブル名を取得します。
	 * <pre>
	 * テーブル名に"_bak"を追加した文字列を返します。
	 * </pre>
	 * @return バックアップテーブル名。
	 */
	public String getBackupTableName() {
		return this.getTableName() + "_bak";
	}

	/**
	 * PK定義のSQLを取得します。
	 * @return PK定義のSQL。
	 */
	public String getPkSql() {
		StringBuilder sb = new StringBuilder();
		for (Field<?> pk : this.pkFieldList) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(StringUtil.camelToSnake(pk.getId()));
		}
		return "primary key(" + sb.toString() + ")";
	}

	/**
	 * 指定されたIDに対応するフィールドを取得します。
	 * @param id フィールドID。
	 * @return フィールド。
	 */
	public Field<?> getField(final String id) {
		return this.fieldList.get(id);
	}

	/**
	 * 対応するカラム情報を取得します。
	 * @param dbcol DBのカラム名。
	 * @param collist DBカラムリスト。
	 * @return カラム情報。
	 */
	private Map<String, Object> getColumnInfo(final String dbcol, final List<Map<String, Object>> collist) {
		Map<String, Object> ret = null;
		for (Map<String, Object> m: collist) {
			String c = (String) m.get("columnName");
			if (dbcol.equals(c)) {
				ret = m;
			}
		}
		return ret;
	}

	/**
	 * 対応するDB中のテーブルとカラム構造の違いがあるかを確認します。
	 * @param gen SQLジェネレータ。
	 * @param collist カラムリスト。
	 * @return 同じ場合true。
	 */
	private boolean columnListAccords(final SqlGenerator gen, final List<Map<String, Object>> collist) {
		if (this.fieldList.size() != collist.size()) {
			logger.warn(this.getTableName() + ":column count missmatch.(" + this.fieldList.size() +"," + collist.size() + ")");
			return false;
		} else {
			for (int i = 0; i < this.fieldList.size(); i++) {
				Field<?> f = this.fieldList.get(i);
				String colname0 = f.getDbColumnName();
				Map<String, Object> m = this.getColumnInfo(colname0, collist);
				if (m == null) {
					logger.warn(this.getTableName() + ":column " + colname0 + " is not found.");
					return false;
				} else {
					String colname1 = (String) m.get("columnName");
					if (!colname0.equals(colname1)) {
						logger.warn(this.getTableName() + ":column name missmatch.(" + colname0 + "," + colname1 + ")");
						return false;
					} else {
						String type0 = gen.getDatabaseType(f);
						String type1 = (String) m.get("dataType");
						if (!type0.equals(type1)) {
							logger.warn(this.getTableName() + ":column " + colname0 + " type missmatch.(" + type0 + "," + type1 + ")");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 対応するDB中のテーブルとカラム構造の違いがあるかを確認します。
	 * @param dao データアクセスオブジェクト。
	 * @return 一致する場合true。
	 * @throws Exception 例外。
	 */
	public boolean structureAccords(final Dao dao) throws Exception {
		SqlGenerator gen = dao.getSqlGenerator();
		List<Map<String, Object>> collist = dao.getTableColumnList(this.getTableName());
		List<String> pklist = dao.getTablePkList(this);
		if (this.columnListAccords(gen, collist)) {
			if (this.pkFieldList.size() != pklist.size()) {
				logger.warn(this.getTableName() + ":pk column count missmatch.");
				return false;
			} else {
				for (int i = 0; i < this.pkFieldList.size(); i++) {
					Field<?> f = this.pkFieldList.get(i);
					String colname0 = f.getDbColumnName();
					String colname1 = pklist.get(i);
					if (!colname0.equals(colname1)) {
						logger.warn(this.getTableName() + ":pk missmatch.(" + colname0 + ","  + colname1 + ")");
						return false;
					}
				}
			}
		} else {
			return false;
		}
		List<Index> ilist = this.getIndexList();
		for (Index idx: ilist) {
			List<Map<String, Object>> iflist = dao.getIndexFieldList(this, idx.getIndexName());
			if (!idx.structureAccords(iflist)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * ID自動生成フラグを取得します。
	 * @return ID自動生成フラグ。
	 */
	public boolean isAutoIncrementId() {
		return autoIncrementId;
	}

	/**
	 * ID自動生成フラグを設定します。
	 * @param autoIncrementId ID自動生成フラグ。
	 */
	public void setAutoIncrementId(final boolean autoIncrementId) {
		this.autoIncrementId = autoIncrementId;
	}

	/**
	 * シーケンスの初期値を取得します。
	 * @return シーケンスの初期値。
	 */
	public Long getSequenceStartValue() {
		return sequenceStartValue;
	}

	/**
	 * シーケンスの初期値を設定します。
	 * @param sequenceStartValue シーケンスの初期値。
	 */
	public void setSequenceStartValue(final Long sequenceStartValue) {
		this.sequenceStartValue = sequenceStartValue;
	}

	/**
	 * フィールドリスト中に、RecordIdFieldのインスタンスが存在するかチェックします。
	 *
	 * @return レコードIDが存在する場合true。
	 */
	public boolean recordIdExists() {
		FieldList flist = this.getFieldList();
		boolean ret = false;
		for (Field<?> f : flist) {
			if (f instanceof RecordIdField) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * テーブルの結合条件を取得します。
	 * <pre>
	 * このテーブルとjoinTableとの結合条件を作成します。
	 * 通常は指定されたjoinTableのクラスをinstansof演算子で判定し結合条件を作成します。
	 * 一回の問い合わせで、同じテーブルを複数回結合する場合は、それぞれのテーブルを
	 * 別インスタンスで作成し別のaliasを設定し、そのaliasで判定します。
	 * </pre>
	 * @param joinTable 結合対象テーブル。
	 * @param alias 結合対象テーブルの別名。
	 * @return 結合条件。
	 */
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}


	/**
	 * フィールド単位のリンク条件を作成します。
	 * <pre>
	 * 以下のような結合条件を作成します。
	 * alias.field = alias.link_field
	 * </pre>
	 * @param table リンク元テーブルのインスタンス。
	 * @param field フィールド。
	 * @param joinTable 結合テーブル。
	 * @param joinTableAlias テーブルの別名。
	 * @param linkField 結合テーブルのフィールド。
	 * @return 結合条件。
	 */
	public final String getLinkFieldCondition(final Table table, final String field, final Table joinTable, final String joinTableAlias, final String linkField) {
		StringBuilder sb = new StringBuilder();
		sb.append(table.getAlias() + ".");
		sb.append(table.getField(field).getDbColumnName());
		sb.append("=");
		sb.append(joinTableAlias);
		sb.append(".");
		sb.append(joinTable.getField(linkField).getDbColumnName());
		return sb.toString();
	}

	
	/**
	 * フィールド単位のリンク条件を作成します。
	 * <pre>
	 * 以下のような結合条件を作成します。
	 * alias.field = alias.link_field
	 * </pre>
	 * @param field フィールド。
	 * @param joinTable 結合テーブル。
	 * @param joinTableAlias テーブルの別名。
	 * @param linkField 結合テーブルのフィールド。
	 * @return 結合条件。
	 */
	public final String getLinkFieldCondition(final String field, final Table joinTable, final String joinTableAlias, final String linkField) {
		return this.getLinkFieldCondition(this, field, joinTable, joinTableAlias, linkField);
	}

	/**
	 * 削除フラグフィールドを取得します。
	 * @return 削除フラグフィールド。
	 */
	public DeleteFlagField getDeleteFlagField() {
		DeleteFlagField ret = null;
		for (Field<?> field: this.fieldList) {
			if (field instanceof DeleteFlagField) {
				ret = (DeleteFlagField) field;
				break;
			}
		}
		return ret;
	}

	/**
	 * 削除フラグが存在する場合、trueを返します。
	 * @return 削除フラグが存在かどうか。
	 */
	public boolean hasDeleteFlag() {
		boolean ret = (this.getDeleteFlagField() != null);
		return ret;
	}

	/**
	 * 作成日時フィールドを取得します。
	 * @return 作成日時フィールド。
	 */
	public CreateTimestampField getCreateTimestampField() {
		CreateTimestampField ret = null;
		for (Field<?> field: this.fieldList) {
			if (field instanceof CreateTimestampField) {
				ret = (CreateTimestampField) field;
				break;
			}
		}
		return ret;
	}

	
	/**
	 * 更新日時フィールドを取得します。
	 * @return 更新日時フィールド。
	 */
	public UpdateTimestampField getUpdateTimestampField() {
		UpdateTimestampField ret = null;
		for (Field<?> field: this.fieldList) {
			if (field instanceof UpdateTimestampField) {
				ret = (UpdateTimestampField) field;
				break;
			}
		}
		return ret;
	}

	/**
	 * 作成ユーザIDフィールドを取得します。
	 * @return 作成ユーザIDフィールド。
	 */
	public CreateUserIdField getCreateUserIdField() {
		CreateUserIdField ret = null;
		for (Field<?> field: this.fieldList) {
			if (field instanceof CreateUserIdField) {
				ret = (CreateUserIdField) field;
				break;
			}
		}
		return ret;
	}

	
	/**
	 * 更新ユーザIDフィールドを取得します。
	 * @return 更新ユーザIDフィールド。
	 */
	public UpdateUserIdField getUpdateUserIdField() {
		UpdateUserIdField ret = null;
		for (Field<?> field: this.fieldList) {
			if (field instanceof UpdateUserIdField) {
				ret = (UpdateUserIdField) field;
				break;
			}
		}
		return ret;
	}


	/**
	 * 初期化データを取得します。
	 * <pre>
	 * クラスと同一パスにある&lt;TableClassName&gt;.data.jsonというファイルを読み込みます。
	 * </pre>
	 * @return 初期化データ。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getInitialData() throws Exception {
		List<Map<String, Object>> list = null;
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream("data/" + cls.getSimpleName() + ".data.json");
		if (is != null) {
			try {
				String json = new String(FileUtil.readInputStream(is), DataFormsServlet.getEncoding());
				list = (List<Map<String, Object>>) JSON.decode(json);
			} finally {
				is.close();
			}
		}
		return list;
	}

	/**
	 * 指定パス以下のテーブルに対応した、ファイルのパスを取得します。
	 * @param path 指定パス。
	 * @return インポートデータのファイルパス。
	 * @throws Exception 例外。
	 */
	public String getImportData(final String path) throws Exception {
		Class<?> cls = this.getClass();
		String jsonfile = path + "/" + cls.getName().replaceAll("\\.", "/") + ".data.json";
		logger.debug("jsonfile=" + jsonfile);
		File f = new File(jsonfile);
		if (f.exists()) {
			;
		} else {
			jsonfile = null;
		}
		return jsonfile;
	}

	/**
	 * インポート時のデータ変換処理。
	 * <pre>
	 * インポート時にデータの変換が必要な場合オーバーライドします。
	 * json形式でエスケープされたデータを変換した場合などに使用します。
	 * </pre>
	 * @param data インポートデータ。
	 * @return 変換されたインポートデータ。
	 */
	public Map<String, Object> convertImportData(final Map<String, Object> data) {
		return data;
	}
	
	/**
	 * テーブルに付随するインデックスの一覧を取得します。
	 * @return テーブルに付随するインデックスの一覧。
	 * @throws Exception 例外。
	 */
	public List<Index> getIndexList() throws Exception {
		String pkgname = this.getClass().getPackage().getName();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> list = finder.findClasses(pkgname, Index.class);
		List<Index> ret = new ArrayList<Index>();
		for (Class<?> c: list) {
			Index index = (Index) c.newInstance();
			Table table = index.getTable();
			if (this.getClass().getName().equals(table.getClass().getName())) {
				ret.add(index);
			}
		}
		return ret;
	}
}

