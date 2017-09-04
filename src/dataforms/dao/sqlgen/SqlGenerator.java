package dataforms.dao.sqlgen;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.SqlGeneratorImpl;
import dataforms.dao.Index;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.QueryPager;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.dao.sqldatatype.SqlBigint;
import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.dao.sqldatatype.SqlChar;
import dataforms.dao.sqldatatype.SqlClob;
import dataforms.dao.sqldatatype.SqlDate;
import dataforms.dao.sqldatatype.SqlDouble;
import dataforms.dao.sqldatatype.SqlInteger;
import dataforms.dao.sqldatatype.SqlNumeric;
import dataforms.dao.sqldatatype.SqlSmallint;
import dataforms.dao.sqldatatype.SqlTime;
import dataforms.dao.sqldatatype.SqlTimestamp;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.field.base.Field;
import dataforms.field.base.Field.MatchType;
import dataforms.field.base.FieldList;
import dataforms.field.common.CreateTimestampField;
import dataforms.field.common.DoNotUpdateField;
import dataforms.field.common.FileField;
import dataforms.field.common.RecordIdField;
import dataforms.field.common.UpdateTimestampField;
import dataforms.field.sqlfunc.AliasField;
import dataforms.field.sqlfunc.GroupSummaryField;
import dataforms.field.sqlfunc.MaxField;
import dataforms.field.sqlfunc.SqlField;
import dataforms.util.ClassFinder;
import dataforms.util.StringUtil;

/**
 * SQLジェネレータクラス。
 * <pre>
 * 定番のSQLを作成するクラスです。
 * DBMS毎の文法の違いは、このクラスのサブクラスで吸収します。
 * </pre>
 *
 */
public abstract class SqlGenerator implements JDBCConnectableObject {
    /**
     * Logger。
     */
    private static Logger log = Logger.getLogger(SqlGenerator.class.getName());


    /**
     * JDBC接続情報。
     */
    private Connection connection = null;

    /**
     * データベースのバージョン情報。
     */
    private String databaseVersion = null;

    /**
     * コンストラクタ。
     * @param conn JDBC接続情報。
     */
    public SqlGenerator(final Connection conn) {
    	this.connection = conn;
    }


    /**
     * コメントの構文。
     *
     */
    public enum CommentSyntax {
    	/**
    	 * サポートなし。
    	 */
    	NONE,
    	/**
    	 * comment on構文をサポート。
    	 */
    	COMMENT,
    	/**
    	 * create文中のcommentサポート。
    	 */
    	CREATE_COMMENT
    };

    /**
     * データベースの製品名を取得します。
     * @return データベースの製品名。
     */
    public abstract String getDatabaseProductName();
    
    /**
     * コメントがサポートされているかどうかを返します。
     * <pre>
     * DBMSに対応したサブクラスで実装します。
     * </pre>
     * @return サポートされている場合true。
     */
    protected abstract CommentSyntax getCommentSyntax();

    /**
     * シーケンスサポートフラグを取得します。
     * <pre>
     * MySQLのようにシーケンスをサポートしないDBMSの場合falseを返します。
     * この場合シーケンスフラグがtrueのテーブルの場合IDフィールドに、
     * auto_incrementを設定します。
     * </pre>
     * @return trueの場合シーケンスをサポートしている。
     */
    public abstract boolean isSequenceSupported();

    /**
     * DatabaseMetaDataのテーブル検索時にヒットするように名前を変換します。
     * <pre>
     * DBMSによって大文字、小文字違いでヒットしないことがあるので、テーブル名の大文字、
     * 小文字を調整します。
     * </pre>
     * @param tblname テーブル名。
     * @return 変換後のテーブル名。
     */
    public abstract String convertTableNameForDatabaseMetaData(final String tblname);


    /**
     * 別名を付ける際に使うasの文字列を返す。
     * <pre>
     * Oracle等でasを書くと、エラーするDBでは組み直します。
     * </pre>
     * @return " as "を返す。
     */
	protected String getAsAliasSql() {
		return " as ";
	}

    /**
     * DatabaseMetaDataで取得するデータタイプの名称を変換します。
     * @param type データタイプ。
     * @return 変換結果。
     */
    public String converTypeNameForDatabaseMetaData(final String type) {
    	return type.toLowerCase();
    }

    /**
     * コネクションに応じたSqlGeneratorを取得します。
     * @param conn JDBC接続。
     * @return SqlGeneratorのインスタンス。
     * @throws Exception 例外。
     */
	@SuppressWarnings("unchecked")
	public static SqlGenerator getInstance(final Connection conn) throws Exception {
		//SqlGenerator ret = null;
		if (SqlGenerator.sqlGeneratorClass == null) {
			log.info("databaseName=" + conn.getMetaData().getDatabaseProductName()
					+ "," + conn.getMetaData().getDatabaseProductVersion()
					);
			log.info("driver=" + conn.getMetaData().getDriverName() +
					"," + conn.getMetaData().getDriverVersion() +
					"," + conn.getMetaData().getDriverMajorVersion() +
					"," + conn.getMetaData().getDriverMinorVersion()
					);
			SqlGenerator.sqlGeneratorClass = (Class<SqlGenerator>) SqlGenerator.getSqlGeneratorClass(conn.getMetaData().getDatabaseProductName());
		}
		Constructor<?> c = SqlGenerator.sqlGeneratorClass.getConstructor(Connection.class);
		SqlGenerator gen = (SqlGenerator) c.newInstance(conn);
		gen.setDatabaseVersion(conn.getMetaData().getDatabaseProductVersion());
		return gen;
	}

	/**
	 * 対応するSqlGenaratorのクラス。
	 */
	private static Class<SqlGenerator> sqlGeneratorClass = null;

	/**
	 * SqlGeneratorのクラスを取得します。
	 * <pre>
	 *
	 * </pre>
	 * @param name DBの名称。
	 * @return SqlGeneratorのクラス名。
	 * @throws Exception 例外。
	 */
	private static Class<?> getSqlGeneratorClass(final String name) throws Exception {
		Class<?> ret = null;
		ClassFinder finder = new ClassFinder();
		List<Class<?>> classlist = finder.findClasses("dataforms.dao.sqlgen", SqlGenerator.class);
		for (Class<?> cls: classlist) {
			SqlGeneratorImpl sga = cls.getAnnotation(SqlGeneratorImpl.class);
			if (sga.databaseProductName().equals(name)) {
				ret = cls;
				break;
			}
		}
   		return ret;
	}

	@Override
	public final Connection getConnection() {
		return this.connection;
	}

	/**
	 * データベースのバージョンを取得します。
	 * @return データベースバージョン。
	 */
	public final String getDatabaseVersion() {
		return databaseVersion;
	}

	/**
	 * データベースバージョンを設定します。
	 * @param databaseVersion データベースバージョン。
	 */
	public final void setDatabaseVersion(final String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	/**
	 * SqlParserを作成します。
	 * @param sql SQL。
	 * @return SqlParser.
	 */
	public SqlParser newSqlParser(final String sql) {
		return new SqlParser(sql);
	}

	/**
	 * 指定したフィールドに対応した、データベースカラムの型名を取得します。
	 * @param field フィールド。
	 * @return 型名。
	 */
	public String getDatabaseType(final Field<?> field) {
		String ret = null;
		if (field instanceof SqlVarchar) {
			ret = "varchar(" + field.getLength() + ")";
		} else if (field instanceof SqlChar) {
			ret = "char(" + field.getLength() + ")";
		} else if (field instanceof SqlSmallint) {
			ret = "smallint";
		} else if (field instanceof SqlInteger) {
			ret = "integer";
		} else if (field instanceof SqlBigint) {
			ret = "bigint";
		} else if (field instanceof SqlDouble) {
			ret = "real";
		} else if (field instanceof SqlNumeric) {
			SqlNumeric nf = (SqlNumeric) field;
			ret = "numeric(" + nf.getPrecision() + "," + nf.getScale() + ")";
		} else if (field instanceof SqlDate) {
			ret = "date";
		} else if (field instanceof SqlTime) {
			ret = "time";
		} else if (field instanceof SqlTimestamp) {
			ret = "timestamp";
		} else if (field instanceof SqlBlob) {
			ret = "blob";
		} else if (field instanceof SqlClob) {
			ret = "clob";
		}
		if (field.isNotNull()) {
			ret += " not null";
		} else {
			//ret += " null";
		}
		return ret;
	}

	/**
	 * 値に対応したリテラル文字列を取得します。
	 * @param value 値。
	 * @return リテラル文字列。
	 */
	protected String getLiteral(final Object value) {
		if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof java.sql.Time) {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSS");
			return "\'" + fmt.format((java.util.Date) value) + "\'";
		} else if (value instanceof java.sql.Timestamp) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			return "\'" + fmt.format((java.util.Date) value) + "\'";
		} else if (value instanceof java.util.Date) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return "\'" + fmt.format((java.util.Date) value) + "\'";
		} else {
			return "\'" + value.toString() + "\'";
		}
	}


	/**
	 * キー自動生成カラムの設定文字列を生成します。
	 * <pre>
	 * 通常nullを返します。
	 * MySQLのようなシーケンスがサポートされず、auto_increment属性を使用するDBMSの場合
	 * その設定文字列を返します。
	 * </pre>
	 * @return キーの自動生成カラムの設定文字列。
	 */
    protected String generateAutoIncrementSql() {
    	return null;
    }


    /**
     * Insert実行時に生成されたauto_incrementカラムの値を取得します。
	 * <pre>
	 * 通常nullを返します。
	 * MySQLのようなシーケンスがサポートされず、auto_increment属性を使用するDBMSの場合
	 * insert時に生成された値を取得するSQLを生成します。
	 * </pre>
     * @param table テーブル。
     * @return 取得するSQL.
     */
    public String generateGetAutoIncrementValueSql(final Table table) {
    	return null;
    }

	/**
	 * 指定したテーブルのcreate文を作成します。
	 * @param table テーブル。
	 * @return 作成したSQL。
	 * @throws Exception 例外。
	 */
	protected String generateCreateTableSql(final Table table) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ");
		sb.append(table.getTableName());
		FieldList list = table.getFieldList();
		StringBuilder cols = new StringBuilder();
		boolean autoinc = false;
		for (Field<?> f : list) {
			if (cols.length() > 0) {
				cols.append(",");
			}
			String sqltype = this.getDatabaseType(f);
			cols.append(f.getDbColumnName());
			cols.append(" ");
			cols.append(sqltype);
			if (f instanceof RecordIdField) {
				if (!this.isSequenceSupported() && table.isAutoIncrementId()) {
					// SequenceがサポートされていないDBMSでシーケンスを使用する場合
					if (!autoinc) {
						String ai = this.generateAutoIncrementSql();
						if (ai != null) {
//							cols.append(" auto_increment ");
							cols.append(" " + ai + " ");
						}
						autoinc = true;
					}
				}
			}
			if (f.getDefaultValue() != null) {
				cols.append(" default ");
				cols.append(this.getLiteral(f.getDefaultValue()));
			}
			//cols.append(f.getColumnDefine(sqltype));
			if (this.getCommentSyntax() == CommentSyntax.CREATE_COMMENT) {
				if (f.getComment() != null) {
					cols.append(" comment '");
					cols.append(f.getComment());
					cols.append("'");
				}
			}
			cols.append("\n");
		}

		sb.append(" (\n");
		sb.append(cols);
		sb.append(",");
		sb.append(table.getPkSql());
		sb.append("\n)");
		if (this.getCommentSyntax() == CommentSyntax.CREATE_COMMENT) {
			if (table.getComment() != null) {
				sb.append(" comment '" + table.getComment() + "'");
			}
		}
//		log.info("sql=" + sb.toString());
		return sb.toString();
	};



	/**
	 * テーブルコメント作成用SQLを作成します。
	 * @param table 対象テーブル。
	 * @return テーブルコメント作成用SQL。
	 */
	private String generateTableCommentSql(final Table table) {
		StringBuilder sb = new StringBuilder();
		String comment = table.getComment();
		if (comment != null) {
			sb.append("comment on table ");
			sb.append(table.getTableName());
			sb.append(" is '");
			sb.append(comment);
			sb.append("'");
//			log.info("comment sql = " + sb.toString());
			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * カラムコメント作成用SQLを作成します。
	 * @param table テーブル。
	 * @param field フィールド。
	 * @return 例外。
	 */
	private String generateFieldCommentSql(final Table table, final Field<?> field) {
		StringBuilder sb = new StringBuilder();
		String comment = field.getComment();
		if (comment != null) {
			sb.append("comment on column ");
			sb.append(table.getTableName());
			sb.append(".");
			sb.append(field.getDbColumnName());
			sb.append(" is '");
			sb.append(comment);
			sb.append("'");
//			log.info("comment sql = " + sb.toString());
			return sb.toString();
		} else {
			return null;
		}

	}

	/**
	 * シーケンス作成用SQLを作成する.
	 * @param table テーブル.
	 * @return シーケンス作成用SQL.
	 * @throws Exception 例外.
	 */
	public String generateCreateSequenceSql(final Table table) throws Exception {
		String ret = null;
		if (table.recordIdExists() && table.isAutoIncrementId()) {
			ret = this.generateCreateSequenceSql(table.getSequenceName(), table.getSequenceStartValue());
		}
		return ret;
	}

	/**
	 * シーケンス作成用SQLを作成します。
	 * @param seqname シーケンス名。
	 * @param startValue 開始値。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	public String generateCreateSequenceSql(final String seqname, final Long startValue) throws Exception {
		String ret = "create sequence " + seqname + " start width " + startValue;
		return ret;
	}


	/**
	 * シーケンス削除用SQLを作成します。
	 * @param table テーブル。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	public String generateDropSequenceSql(final Table table) throws Exception {
		String ret = null;
		if (table.recordIdExists() && table.isAutoIncrementId()) {
			ret = this.generateDropSequenceSql(table.getSequenceName());
		}
		return ret;
	}

	/**
	 * シーケンス削除用SQLを作成します。
	 * @param sequenceName テーブル名。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	public String generateDropSequenceSql(final String sequenceName) throws Exception {
		String ret = "drop sequence " + sequenceName;
		return ret;
	}


	/**
	 * レコードIDの取得SQLを取得します。
	 * @param table テーブル。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	public String generateGetRecordIdSql(final Table table) throws Exception {
		return generateGetRecordIdSql(table.getTableName());
	}

	/**
	 * レコードIDの取得SQL.
	 * @param table テーブル.
	 * @return SQL.
	 * @throws Exception 例外.
	 */
	public abstract String generateGetRecordIdSql(final String table) throws Exception;


	/**
	 * テーブルを作成するためのSQLリストを取得します。
	 * @param table テーブル。
	 * @return sqlリスト。
	 * @throws Exception 例外。
	 */
	public List<String> generateCreateTableSqlList(final Table table) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add(this.generateCreateTableSql(table));
		if (this.getCommentSyntax() == CommentSyntax.COMMENT) {
			String tcom = this.generateTableCommentSql(table);
			if (tcom != null) {
				list.add(tcom);
			}
			FieldList flist = table.getFieldList();
			for (Field<?> f : flist) {
				String fcom = this.generateFieldCommentSql(table, f);
				if (fcom != null) {
					list.add(fcom);
				}
			}
		}
/*		String cseq = this.generateCreateSequenceSql(table);
		if (cseq != null) {
			list.add(cseq);
		}
*/		return list;
	}

	/**
	 * テーブルを削除するためのSQLを取得します。
	 * @param table テーブルクラス。
	 * @return SQL。
	 */
	public String generateDropTableSql(final Table table) {
		return this.generateDropTableSql(table.getTableName());
	}


	/**
	 * テーブルを削除するためのSQLを取得します。
	 * @param tablename テーブル名。
	 * @return SQL。
	 */
	public String generateDropTableSql(final String tablename) {
		String sql = "drop table " + tablename;
		return sql;
	}

	/**
	 * テーブル存在チェックSQLを作成します。
	 * @return テーブル存在チェックSQL。
	 */
	public abstract String generateTableExistsSql();


	/**
	 * シーケンスの存在チェックSQLを作成します。
	 * @return テーブル存在チェックSQL。
	 */
	public abstract String generateSequenceExistsSql();


	/**
	 * テーブルのレコード数を取得するSQLを作成します。
	 * @param table テーブル。
	 * @return SQL。
	 */
	public String generateRecordCountSql(final Table table) {
		return this.generateRecordCountSql(table.getTableName());
	}

	/**
	 * テーブルのレコード数を取得するSQLを作成します。
	 * @param tableName テーブル名。
	 * @return SQL。
	 */
	public String generateRecordCountSql(final String tableName) {
		String sql = "select count(*) as record_count from " + tableName;
		return sql;
	}

	/**
	 * テーブルリネーム用SQLを作成します。
	 * @param oldname 旧名称。
	 * @param newname 新名称。
	 * @return SQL。
	 */
	public String generateRenameTableSql(final String oldname, final String newname) {
		String sql = "alter table " + oldname + " rename to " + newname;
		return sql;
	}

	/**
	 * 全レコード削除のSQLを作成します。
	 * @param table テーブル。
	 * @return SQL。
	 */
	public String generateDeleteAllSql(final Table table) {
		return this.generateDeleteAllSql(table.getTableName());
	}

	/**
	 * 全レコード削除のSQLを作成します。
	 * @param tableName テーブル名。
	 * @return SQL。
	 */
	public String generateDeleteAllSql(final String tableName) {
		String sql = "delete from " + tableName;
		return sql;
	}

	/**
	 * 指定したテーブルのInsert文を作成します。
	 * @param table テーブル。
	 * @return insert文。
	 */
	public String generateInsertSql(final Table table) {
		String pkid = "";
		if (!this.isSequenceSupported()) {
			if (table.isAutoIncrementId()) {
				pkid = table.getPkFieldList().get(0).getId();
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(table.getTableName());
		sb.append(" (");
		boolean first = true;
		for (int i = 0; i < table.getFieldList().size(); i++) {
			Field<?> f = table.getFieldList().get(i);
			if (pkid.equals(f.getId())) {
				continue;
			}
			if (!first) {
				sb.append(",");
			}
			first = false;
			sb.append(StringUtil.camelToSnake(f.getId()));
		}
		sb.append(") values (");
		first = true;
		for (int i = 0; i < table.getFieldList().size(); i++) {
			Field<?> f = table.getFieldList().get(i);
			if (pkid.equals(f.getId())) {
				continue;
			}
			if (!first) {
				sb.append(",");
			}
			first = false;
			if (f instanceof CreateTimestampField || f instanceof UpdateTimestampField) {
				sb.append(this.generateSysTimestampSql());
			} else {
				sb.append(":");
				sb.append(StringUtil.camelToSnake(f.getId()));
			}
		}
		sb.append(")");

		return sb.toString();
	}


	/**
	 * 元テーブルのカラム存在チェックを行います。
	 * @param colname カラム名。
	 * @param collist 元テーブルのカラムリスト。
	 * @return 存在した場合true。
	 */
	private boolean checkFromColumn(final String colname, final List<Map<String, Object>> collist) {
		boolean ret = false;
		for (Map<String, Object> m: collist) {
			String n = (String) m.get("columnName");
			if (colname.equals(n)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * テーブルのデータをコピーするSQLを取得します。
	 * @param toTable コピー先テーブル。
	 * @param fromTableName コピー元のテーブル。
	 * @param collist カラムリスト。
	 * @return データコピーSQL。
	 */
	public String generateCopyDataSql(final Table toTable, final String fromTableName, final List<Map<String, Object>> collist) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(toTable.getTableName());
		sb.append(" (");
		boolean flg = false;
		for (Field<?> f: toTable.getFieldList()) {
			String colname = f.getDbColumnName();
			if (this.checkFromColumn(colname, collist)) {
				if (flg) {
					sb.append(",");
				}
				sb.append(f.getDbColumnName());
				flg = true;
			}
		}
		sb.append(" ) select ");
		flg = false;
		for (Field<?> f: toTable.getFieldList()) {
			String colname = f.getDbColumnName();
			if (this.checkFromColumn(colname, collist)) {
				if (flg) {
					sb.append(",");
				}
				sb.append(f.getDbColumnName());
				flg = true;
			}
		}
		sb.append(" from ");
		sb.append(fromTableName);
		return sb.toString();
	}

	/**
	 * システム時刻を取得するSQLを作成します。
	 * @return システム時刻を取得するSQL。
	 */
	public abstract String generateSysTimestampSql();

	
	/**
	 * 対象テーブルが主テーブルまでリンクがたどれるかを確認する。
	 * @param t 全テーブルリスト。
	 * @param list 結合するテーブル。
	 * @return 主テーブルまでリンクが設定されている場合true。
	 */
	private boolean isLinkedToMainTable(final TableList list, final Table t) {
		boolean ret = false;
		if (!StringUtil.isBlank(list.get(0).getJoinCondition(t, t.getAlias()))) {
			return true;
		} else {
			for (int i = 1; i < list.size(); i++) {
				Table tbl = list.get(i);
				if (tbl.getClass().getName().equals(t.getClass().getName()) && tbl.getAlias().equals(t.getAlias())) {
					continue;
				}
				if (StringUtil.isBlank(list.get(i).getJoinCondition(t, t.getAlias()))) {
					if (this.isLinkedToMainTable(list, list.get(i))) {
						return true;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * 主テーブル以外の結合条件を検索します。
	 * @param list 全テーブルリスト。
	 * @param table 結合するテーブル。
	 * @return 結合条件。
	 */
	public String getJoinConditionOtherThamMainTable(final TableList list, final Table table) {
		String ret = null;
		for (int i = 1; i < list.size(); i++) {
			Table t = list.get(i);
			if (table.getClass().getName().equals(t.getClass().getName()) && table.getAlias().equals(t.getAlias())) {
				continue;
			}
			String cond = t.getJoinCondition(table, table.getAlias());
			log.debug("class=" + t.getClass().getName() + "," + table.getClass().getName() + ",cond=" + cond);
			if (!StringUtil.isBlank(cond)) {
				if (this.isLinkedToMainTable(list, t)) {
					ret = cond;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * JOINの構文を作成します。
	 * @param sb 構文を追加する文字列バッファ。
	 * @param query 問い合わせ。
	 * @param mtable 結合元テーブル。
	 * @param type JOINのタイプ。
	 * @param list 結合テーブルリスト。
	 * @param alist 別名リスト。
	 */
	private void generateJoinSql(final StringBuilder sb, final Query query, final Table mtable, final String type, final TableList list, final List<String> alist) {
		int idx = 0;
		for (Table table: list) {
			this.generateSubQuerySql(table);
			sb.append(" ");
			sb.append(type);
			sb.append(" ");
			sb.append(table.getTableName());
			sb.append(this.getAsAliasSql());
			sb.append(alist.get(idx));
			sb.append(" on ");
			String c = mtable.getJoinCondition(table, alist.get(idx));
			if (c != null) {
				sb.append(c);
				if (query.isEffectivenessOfDeleteFlag()) {
					if (table.hasDeleteFlag()) {
						String delColumn = table.getDeleteFlagField().getDbColumnName();
						sb.append(" and " + table.getAlias() + "." + delColumn + "='0' ");
					}
				}
			} else {
				log.debug("getJoinConditionOtherThamMainTable");
				TableList tlist = new TableList();
				tlist.add(query.getMainTable());
				if (query.getJoinTableList() != null) {
					tlist.addAll(query.getJoinTableList());
				}
				if (query.getLeftJoinTableList() != null) {
					tlist.addAll(query.getLeftJoinTableList());
				}
				if (query.getRightJoinTableList() != null) {
					tlist.addAll(query.getRightJoinTableList());
				}
				c = this.getJoinConditionOtherThamMainTable(tlist, table);
				if (c != null) {
					sb.append(c);
					if (query.isEffectivenessOfDeleteFlag()) {
						if (table.hasDeleteFlag()) {
							String delColumn = table.getDeleteFlagField().getDbColumnName();
							sb.append(" and " + table.getAlias() + "." + delColumn + "='0' ");
						}
					}
				} else {
					throw new Error(mtable.getTableName() + "と" + table.getTableName() + "の結合条件が定義されていません。");
				}
			}
			sb.append("\n");
			idx++;
		}
	}

	/**
	 * フィールド、テーブル別名マップを作成します。
	 * @param map フィールド、テーブル別名マップ。
	 * @param table テーブル。
	 * @param alias テーブル別名。
	 */
	private void addFieldAliasMap(final Map<String, String> map, final Table table, final String alias) {
		FieldList list = table.getFieldList();
		for (Field<?> f: list) {
			if (!map.containsKey(f.getId())) {
				String a = alias;
				if (f.getTable() != null) {
					if (f.getTable().getAlias() != null) {
						a = f.getTable().getAlias();
					}
				}
				map.put(f.getId(), a);

			}
		}
	}

	/**
	 * 副問い合わせのSQLを作成します。
	 * @param table 副問い合わせ。
	 */
	private void generateSubQuerySql(final Table table) {
		if (table.getTableName() == null) {
			if (table instanceof SubQuery) {
				SubQuery sq = (SubQuery) table;
				String sql = this.generateQuerySql(sq.getQuery(), false);
				sq.setSql(sql);
			}
		}
	}

	/**
	 * テーブル結合のSQLを取得します。
	 * @param query 結合テーブル。
	 * @return 結合テーブルのSQL。
	 */
	private String generateJoinTableSql(final Query query) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> fieldAliasMap = new HashMap<String, String>();
		Table mtable = query.getMainTable();
		this.generateSubQuerySql(mtable);
		sb.append(mtable.getTableName());
		if (mtable.getAlias() == null) {
			mtable.setAlias("m");
		}
		sb.append(this.getAsAliasSql() + " " + mtable.getAlias() + "\n");
		this.addFieldAliasMap(fieldAliasMap, mtable, mtable.getAlias());
		TableList list = query.getJoinTableList();
		if (list != null) {
			List<String> alist = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				Table t = list.get(i);
				String alias = t.getAlias();
				if (alias == null) {
					alias = "i" + i;
					t.setAlias(alias);
				}
				alist.add(alias);
				this.addFieldAliasMap(fieldAliasMap, list.get(i), alias);
			}
			query.setJoinAliasList(alist);
			generateJoinSql(sb, query, mtable, "inner join", list, alist);
		}
		TableList llist = query.getLeftJoinTableList();
		if (llist != null) {
			List<String> alist = new ArrayList<String>();
			for (int i = 0; i < llist.size(); i++) {
				//String alias = "l" + i;
				Table t = llist.get(i);
				String alias = t.getAlias();
				if (alias == null) {
					alias = "l" + i;
					t.setAlias(alias);
				}
				alist.add(alias);
				this.addFieldAliasMap(fieldAliasMap, llist.get(i), alias);
			}
			query.setLeftJoinAliasList(alist);
			generateJoinSql(sb, query, mtable, "left join", llist, alist);
		}
		TableList rlist = query.getRightJoinTableList();
		if (rlist != null) {
			List<String> alist = new ArrayList<String>();
			for (int i = 0; i < rlist.size(); i++) {
//				String alias = "r" + i;
				Table t = rlist.get(i);
				String alias = t.getAlias();
				if (alias == null) {
					alias = "r" + i;
					t.setAlias(alias);
				}
				alist.add(alias);
				this.addFieldAliasMap(fieldAliasMap, rlist.get(i), alias);
			}
			query.setRightJoinAliasList(alist);
			generateJoinSql(sb, query, mtable, "right join", rlist, alist);
		}
		query.setFieldTableAliasMap(fieldAliasMap);
		return sb.toString();
	}

	/**
	 * Like文のEscape指定文字列を取得します。
	 * @return Like文のEscape指定文字列。
	 */
	protected String getLikeEscape() {
		return " {escape '\\'} ";
	}

	/**
	 * 大文字変換関数を取得します。
	 * @return 大文字変換関数。
	 */
	protected String getUpperMethod() {
		return "upper";
	}

	/**
	 * 小文字変換関数を取得します。
	 * @return 小文字変換関数。
	 */
	protected String getLowerMethod() {
		return "lower";
	}

	/**
	 * Where句の条件式の生成します。
	 * @param query 問い合わせ。
	 * @return where句の条件式。
	 */
	private String generateWhereCondition(final Query query) {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> p = query.getQueryFormData();
		if (query.getCondition() != null) {
			sb.append(query.getCondition());
		}
		if (query.getQueryFormFieldList() != null) {
			for (Field<?> f: query.getQueryFormFieldList()) {
				String id = f.getId();

				Object v = p.get(id);
				if (v == null) {
					continue;
				}
				String sv = v.toString();
				if (StringUtil.isBlank(sv)) {
					continue;
				}
				String ex = null;
				if (f.getMatchType() == MatchType.FULL) {
					String colname = query.getMatchFieldSql(f);
					if (colname != null) {
						if (!f.isCaseInsensitive()) {
							ex = colname + " = :" + StringUtil.camelToSnake(id);
						} else {
							String lower = this.getLowerMethod();
							ex = lower + "(" + colname + ") = " + lower + "(:" + StringUtil.camelToSnake(id) + ")";
						}
					}
				} else if (f.getMatchType() == MatchType.BEGIN
						|| f.getMatchType() == MatchType.END
						|| f.getMatchType() == MatchType.PART) {
					String colname = query.getMatchFieldSql(f);
					if (colname != null) {
						if (!f.isCaseInsensitive()) {
							ex = colname + " like :" + StringUtil.camelToSnake(id) + this.getLikeEscape();
						} else {
							String lower = this.getLowerMethod();
							ex = lower + "(" + colname + ") like " + lower + "(:" +StringUtil.camelToSnake(id) + ")" +  this.getLikeEscape();
						}
					}
				} else if (f.getMatchType() == MatchType.RANGE_FROM) {
					String colname = query.getMatchFieldSql(f);
					if (colname != null) {
						ex = colname + " >= :" + StringUtil.camelToSnake(id);
					}
				} else if (f.getMatchType() == MatchType.RANGE_TO) {
					String colname = query.getMatchFieldSql(f);
					if (colname != null) {
						ex = colname + " <= :" + StringUtil.camelToSnake(id);
					}
				} else if (f.getMatchType() == MatchType.IN) {
					String colname = query.getMatchFieldSql(f);
					if (colname != null) {
						if (v instanceof List) {
							@SuppressWarnings("rawtypes")
							List l = (List) v;
							if (l.size() > 0) {
								StringBuilder ary = new StringBuilder();
								for (int i = 0; i < l.size(); i++) {
									if (ary.length() > 0) {
										ary.append(",");
									}
									ary.append(":" + StringUtil.camelToSnake(id) + "[" + i + "]");
								}
								ex = colname + " in (" + ary.toString() + ")";
							}
						}
					}
				}
				if (ex != null) {
					if (sb.length() > 0) {
						sb.append(" and ");
					}
					sb.append(ex);
				}
			}
		}
		if (query.isEffectivenessOfDeleteFlag()) {
			if (query.getMainTable().hasDeleteFlag()) {
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				String alias = query.getMainTable().getAlias();
				sb.append(alias);
				String delFlagColumn = query.getMainTable().getDeleteFlagField().getDbColumnName();
				sb.append("." + delFlagColumn + "='0' ");
			}
		}
		return sb.toString();
	}



	/**
	 * 標準的なselectの対象のフィールド式を作成します。
	 * @param query 問い合わせ。
	 * @param field フィールド。
	 * @return フィールド式。
	 */
	public String getSelectFieldSql(final Query query, final Field<?> field) {
		String t = null;
		if (field.getTable() != null) {
			 t = field.getTable().getAlias();
		}
		if (field instanceof GroupSummaryField) {
			GroupSummaryField<?> gsf = (GroupSummaryField<?>) field;
			//String t = query.getFieldTableAliasMap().get(gsf.getTargetField().getId());
			if (t != null) {
				String ret = gsf.getFunctionName() + "(" + t + "." + gsf.getTargetField().getDbColumnName() + ")" + this.getAsAliasSql() + field.getDbColumnName() + "\n";
				return ret;
			}
		} else if (field instanceof AliasField) {
			AliasField af = (AliasField) field;
			if (t != null) {
				String ret = t + "." + af.getTargetField().getDbColumnName() + this.getAsAliasSql() + field.getDbColumnName() + "\n";
				return ret;
			}
		} else if (field instanceof SqlField) {
			SqlField sqlf = (SqlField) field;
			String ret = sqlf.getSql() + this.getAsAliasSql() + field.getDbColumnName() + "\n";
			return ret;
		} else {
			//String t = query.getFieldTableAliasMap().get(field.getId());
			if (t != null) {
				String ret = t + "." + field.getDbColumnName() + this.getAsAliasSql()  + field.getDbColumnName() + "\n";
				return ret;
			}
		}
		return null;
	}

	/**
	 * フィールドの並びを作成します。
	 * @param query 問い合わせ。
	 * @param flist フィールドリスト。
	 * @param orderby order by 用のフィールドリスト。
	 * @return フィールドの並び。
	 */
	private String getFieldSequence(final Query query, final FieldList flist, final boolean orderby) {
		StringBuilder sb = new StringBuilder();
		for (Field<?> f: flist) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(f.getTable().getAlias());
			sb.append(".");
			if (f instanceof AliasField) {
				sb.append(StringUtil.camelToSnake(((AliasField) f).getTargetField().getId()));
			} else {
				sb.append(StringUtil.camelToSnake(f.getId()));
			}
			if (orderby) {
				if (f.getSortOrder() == Field.SortOrder.ASC) {
					sb.append(" asc");
				} else {
					sb.append(" desc");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Group by 句を取得します。
	 * @param query 問い合わせ・
	 * @return Group By 句。
	 */
	private String getGroupBySql(final Query query) {
		FieldList flist = query.getGroupByFieldList();
		if (flist.size() > 0) {
			return " group by " + this.getFieldSequence(query, flist, false);
		} else {
			return "";
		}
	}

	/**
	 * Order by 句を取得します。
	 * @param query 問い合わせ。
	 * @return order By 句。
	 */
	private String getOrderBySql(final Query query) {
		FieldList flist = query.getOrderByFieldList();
		if (flist != null) {
			if (flist.size() > 0) {
				return " order by " + this.getFieldSequence(query, flist, true);
			}
		}
		return "";
	}

	/**
	 * 問い合わせのSQLを取得します。
	 * @param query 問い合わせ。
	 * @param orderBy trueの場合、order byを生成する。
	 * @return SQL。
	 */
	public String generateQuerySql(final Query query, final boolean orderBy) {
		String joinTable = this.generateJoinTableSql(query);
		FieldList flist = query.getFieldList();
		StringBuilder collist = new StringBuilder();
		for (Field<?> f: flist) {
			if (collist.length() > 0) {
				collist.append(",");
			}
			collist.append(this.getSelectFieldSql(query, f));
		}
		StringBuilder sb = new StringBuilder();
		if (query.isDistinct()) {
			sb.append("select distinct \n");
		} else {
			sb.append("select \n");
		}
		sb.append(collist);
		sb.append(" from ");
		sb.append(joinTable);
		String wc = this.generateWhereCondition(query);
		if (wc.length() > 0) {
			sb.append(" where ");
			sb.append(wc);
		}
		sb.append(this.getGroupBySql(query));
		if (orderBy) {
			sb.append(this.getOrderBySql(query));
		}
		return sb.toString();
	}

	/**
	 * 問い合わせのSQLを取得します。
	 * @param query 問い合わせ。
	 * @return SQL。
	 */
	public String generateQuerySql(final Query query) {
		return this.generateQuerySql(query, true);
	}

	/**
	 * BLOB等のファイルフィールドの更新用SQLを作成します。
	 * <pre>
	 * 既にファイルが登録されており、ファイルが送信されない場合は、そのままの値を保持するSQLを生成します。
	 * </pre>
	 * @param id フィールドID.
	 * @return SQL.
	 */
	protected String generateUpdateFileFieldSql(final String id) {
		String pid = StringUtil.camelToSnake(id);
		String ret = "case when :" + pid + "_kf = '1' then " + pid + " else :" + pid + " end ";
		return ret;
	}


	/**
	 * テーブル更新SQLを作成します。
	 * @param table テーブル。
	 * @param updateField 更新フィールドリスト。
	 * @param condField 条件フィールドリスト。
	 * @return sql。
	 */
	public String generateUpdateSql(final Table table, final FieldList updateField, final FieldList condField) {
		return this.generateUpdateSql(table, updateField, condField, null);
	}


	/**
	 * 指定されたフィールドリストに完全一致するSQLを作成します。
	 * <pre>
	 * FieldのMatchTypeは無視し常に完全一致の条件を生成します。
	 * </pre>
	 * @param condField 条件フィールドリスト。
	 * @return 指定されたフィールドリストに完全一致するSQL。
	 */
	private String getWhereCondition(final FieldList condField) {
		StringBuilder csb = new StringBuilder();
		for (Field<?> f : condField) {
			if (csb.length() > 0) {
				csb.append(" and ");
			}
			csb.append(StringUtil.camelToSnake(f.getId()));
			csb.append(" = :");
			csb.append(StringUtil.camelToSnake(f.getId()));
			csb.append("\n");
		}
		return csb.toString();
	}

	/**
	 * 指定されたフィールドリストに存在し、かつ条件データマップに値が存在する場合条件式を生成します。
	 * <pre>
	 * Queryの条件式生成と同様にMatchTypeもサポートします。
	 * </pre>
	 * @param table テーブル。
	 * @param condField 条件フィールドリスト。
	 * @param data 条件データマップ。
	 * @return sql。
	 */
	private String getWhereCondition(final Table table, final FieldList condField, final Map<String, Object> data) {
		Query query = new Query();
		table.setAlias(table.getTableName());
		query.setMainTable(table);
		FieldList list = new FieldList();
		for (Field<?> f: condField) {
			list.add(table.getField(f.getId()));
		}
		query.setQueryFormFieldList(list);
		query.setQueryFormData(data);
		String w = this.generateWhereCondition(query);
		return w;
	}


	/**
	 * テーブル更新SQLを作成します。
	 * @param table テーブル。
	 * @param updateField 更新フィールドリスト。
	 * @param condField 条件フィールドリスト。
	 * @param data 条件作成データ。
	 * <pre>
	 * dataがnullの場合condFieldに指定されたフィールドに完全一致する条件式が作成されます。
	 * dataかnot nullの場合、generateQuerySqlと同様にcondFieldに対応するデータが存在するフィールド条件のみ作成されます。
	 * </pre>
	 * @return sql。
	 */
	public String generateUpdateSql(final Table table, final FieldList updateField, final FieldList condField, final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		sb.append("update " + table.getTableName() + " set \n");
		StringBuilder usb = new StringBuilder();
		for (Field<?> f : updateField) {
			if (f instanceof DoNotUpdateField) {
				continue;
			}
			if (usb.length() > 0) {
				usb.append(", ");
			}
			if (f instanceof UpdateTimestampField) {
				usb.append(StringUtil.camelToSnake(f.getId()));
				usb.append(" = ");
				usb.append(this.generateSysTimestampSql());
				usb.append("\n");
			} else if (f instanceof FileField) {
				usb.append(StringUtil.camelToSnake(f.getId()));
				usb.append(" = ");
				usb.append(this.generateUpdateFileFieldSql(f.getId()));
				usb.append("\n");
			} else {
				usb.append(StringUtil.camelToSnake(f.getId()));
				usb.append(" = :");
				usb.append(StringUtil.camelToSnake(f.getId()));
				usb.append("\n");
			}
		}
		String csb = null;
		if (data == null) {
			csb = this.getWhereCondition(condField);
		} else {
			csb = this.getWhereCondition(table, condField, data);
		}
		sb.append(usb.toString());
		sb.append("where\n");
		sb.append(csb);
		return sb.toString();
	}


	/**
	 * 標準的な更新SQLを取得します。
	 * @param table テーブル。
	 * @return sql。
	 */
	public String generateUpdateSql(final Table table) {
		FieldList condField = new FieldList();
		condField.addAll(table.getPkFieldList());
		FieldList updateField = table.getUpdateFieldList();
		return this.generateUpdateSql(table, updateField, condField);
	}



	/**
	 * 更新情報を取得するSQLを作成します。
	 * @param table テーブル。
	 * @return 例外。
	 */
	public String generateIsUpdatableSql(final Table table) {
		StringBuilder sb = new StringBuilder("select update_user_id, update_timestamp from ");
		sb.append(table.getTableName());
		sb.append(" where ");
		sb.append(this.getWhereCondition(table.getPkFieldList()));
		return sb.toString();
	}

	/**
	 * レコード削除用SQLを作成します。
	 * <pre>
	 * keyFieldListに指定されたフィールドに完全一致する条件式が作成されます。
	 * </pre>
	 * @param table テーブル。
	 * @param keyFieldList キーフィールドリスト。
	 * @return SQL。
	 */
	public String generateDeleteSql(final Table table, final FieldList keyFieldList) {
		StringBuilder sb = new StringBuilder("delete from ");
		sb.append(table.getTableName());
		sb.append(" where ");
		sb.append(this.getWhereCondition(keyFieldList));
		return sb.toString();
	}


	/**
	 * レコード削除用SQLを作成します。
	 * <pre>
	 * generateQuerySqlと同様にkeyFieldListに対応するデータが存在するフィールド条件のみ作成されます。
	 * </pre>
	 * @param table テーブル。
	 * @param keyFieldList キーフィールドリスト。
	 * @param data パラメータデータ。
	 * @return SQL。
	 */
	public String generateDeleteSql(final Table table, final FieldList keyFieldList, final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder("delete from ");
		sb.append(table.getTableName());
		sb.append(" where ");
		sb.append(this.getWhereCondition(table, keyFieldList, data));
		return sb.toString();
	}

	/**
	 * 1レコード削除用SQLを取得します。
	 * @param table テーブル。
	 * @return 1レコード削除用SQL。
	 */
	public String generateDeleteSql(final Table table) {
		return this.generateDeleteSql(table, table.getPkFieldList());
	}

	/**
	 * 削除フラグをONにするSQLを作成します。
	 * <pre>
	 * keyFieldListに指定されたフィールドに完全一致する条件式が作成されます。
	 * </pre>
	 * @param table テーブル。
	 * @param keyFieldList キーフィールドリスト。
	 * @return SQL。
	 */
	public String generateRemoveSql(final Table table, final FieldList keyFieldList) {
		StringBuilder sb = new StringBuilder("update ");
		sb.append(table.getTableName());
		String delFlagColumn = table.getDeleteFlagField().getDbColumnName();
		String updateTimestamp = table.getUpdateTimestampField().getDbColumnName();
		String updateUserId = table.getUpdateUserIdField().getDbColumnName();
		sb.append(" set " + delFlagColumn + "='1'");
		sb.append(", " + updateTimestamp + "=" + this.generateSysTimestampSql());
		sb.append(", " + updateUserId + "=:" + updateUserId);
		sb.append(" where ");
		sb.append(this.getWhereCondition(keyFieldList));
		return sb.toString();
	}


	/**
	 * 削除フラグをONにするSQLを作成します。
	 * <pre>
	 * generateQuerySqlと同様にkeyFieldListに対応するデータが存在するフィールド条件のみ作成されます。
	 * </pre>
	 * @param table テーブル。
	 * @param keyFieldList キーフィールドリスト。
	 * @param data パラメータデータ。
	 * @return SQL。
	 */
	public String generateRemoveSql(final Table table, final FieldList keyFieldList, final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder("update ");
		sb.append(table.getTableName());
		String delFlagColumn = table.getDeleteFlagField().getDbColumnName();
		String updateTimestamp = table.getUpdateTimestampField().getDbColumnName();
		String updateUserId = table.getUpdateUserIdField().getDbColumnName();
		sb.append(" set " + delFlagColumn + "='1'");
		sb.append(", " + updateTimestamp + "=" + this.generateSysTimestampSql());
		sb.append(", " + updateUserId + "=:" + updateUserId);
		sb.append(" where ");
		sb.append(this.getWhereCondition(table, keyFieldList, data));
		return sb.toString();
	}

	/**
	 * 削除フラグをONにするSQLを取得します。
	 * @param table テーブル。
	 * @return 1レコード削除用SQL。
	 */
	public String generateRemoveSql(final Table table) {
		return this.generateRemoveSql(table, table.getPkFieldList());
	}


	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param query 問い合わせ。
	 * @return レコード数をカウントするsql。
	 */
	public String generateHitCountSql(final Query query) {
		String orgsql = this.generateQuerySql(query, true);
		String sql = "select count(*) as cnt from (" + orgsql + ") as m";
		return sql;
	}

	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param orgsql SQL。
	 * @return レコード数をカウントするsql。
	 */
	public String generateHitCountSql(final String orgsql) {
		String sql = "select count(*) as cnt from (" + orgsql + ") as m";
		return sql;
	}


	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param qp QueryPager・
	 * @return レコード数をカウントするsql。
	 */
	public String generateHitCountSql(final QueryPager qp) {
		String orgsql = getOrgSql(qp);
		String sql = "select count(*) as cnt from (" + orgsql + ") as m";
		return sql;
	}

	/**
	 * Page表示するSQLを取得します。
	 * @param qp QueryPager。
	 * @return Page表示するSQL。
	 */
	protected String getOrgSql(final QueryPager qp) {
		String orgsql = qp.getSql();
		if (orgsql == null) {
			Query q = qp.getQuery();
			orgsql = this.generateQuerySql(q, true);
		}
		return orgsql;
	}

	/**
	 * 指定ページを取得するSQLを作成します。
	 * @param qp QueryPager。
	 * @return SQL。
	 */
	public abstract String generateGetPageSql(final QueryPager qp);

	/**
	 * 指定項目の指定条件での最大値を作成します。
	 * @param table テーブル。
	 * @param field 枝番フィールド。
	 * @param keyFieldList 枝番以外の主キー。
	 * @param param パラメータ。
	 * @return SQL。
	 */
	public String generateGetMaxValueSql(final Table table, final Field<?> field, final FieldList keyFieldList, final Map<String, Object> param) {
		Query query = new Query();
		query.setFieldList(new FieldList(new MaxField(field.getId(), field)));
		query.setMainTable(table);
		query.setQueryFormFieldList(keyFieldList);
		query.setQueryFormData(param);
		String ret = this.generateQuerySql(query);
		return ret;
	}

	/**
	 * 主キーの最終項目の最大値を取得するSQLを作成します。
	 * @param table テーブル。
	 * @param param パラメータ。
	 * @return sql。
	 */
	public String generateGetLastPrimaryKeySql(final Table table, final Map<String, Object> param) {
		FieldList pklist = table.getPkFieldList();
		int lastidx = pklist.size() - 1;
		FieldList list = new FieldList();
		for (int i = 0; i < pklist.size() - 1; i++) {
			list.add(pklist.get(i));
		}
		return this.generateGetMaxValueSql(table, pklist.get(lastidx), list, param);
	}


	/**
	 * 更新リスト中に存在しないレコード取得するためのwhere句を作成します。
	 * @param pklist PKフィールドリスト。
	 * @param updatedList 更新リスト。 
	 * @return where句。
	 */
	private String generateNotInListConditon(final FieldList pklist, final List<Map<String, Object>> updatedList) {
		StringBuilder sb = new StringBuilder();
		boolean delflg = false;
		if (updatedList != null && updatedList.size() > 0) {
			Field<?> lastPk = pklist.get(pklist.size() - 1);
			sb.append(" and ");
			sb.append(lastPk.getDbColumnName());
			sb.append(" not in (");
			int idx = 0;
			for (int i = 0; i < updatedList.size(); i++) {
				Map<String, Object> m = updatedList.get(i);
				Object v = m.get(lastPk.getId());
				if (v != null) {
					if (idx > 0) {
						sb.append(",");
					}
					if (v instanceof Number) {
						sb.append(m.get(lastPk.getId()).toString());
					} else {
						sb.append("'");
						sb.append(m.get(lastPk.getId()).toString());
						sb.append("'");
					}
					delflg = true;
					idx++;
				}
			}
			sb.append(")");
		}
		if (delflg) {
			return sb.toString();
		} else {
			// 全て新規の場合sqlはnull
			return null;
		}

	}
	
	/**
	 * リスト中に存在しないレコードを選択するSQLを作成します。
	 * @param table テーブル。
	 * @param updatedList 更新されたリスト。
	 * @return SQL。
	 */
	public String generateSelectNotInListSql(final Table table, final List<Map<String, Object>> updatedList) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(table.getTableName());
		sb.append(" where ");
		FieldList pklist = table.getPkFieldList();
		for (int i = 0; i < pklist.size() - 1; i++) {
			if (i > 0) {
				sb.append(" and ");
			}
			Field<?> f = pklist.get(i);
			String cn = f.getDbColumnName();
			sb.append(cn);
			sb.append("=:");
			sb.append(cn);
		}
		String notInCond = this.generateNotInListConditon(pklist, updatedList);
		if (notInCond != null) {
			sb.append(notInCond);
		}
		return sb.toString();
	}
	
	
	/**
	 * Index作成用SQLを作成します。
	 * @param index インデックス。
	 * @return インデックス作成用SQL。
	 */
	public String generateCreateIndexSql(final Index index) {
		StringBuilder sb = new StringBuilder();
		if (index.isUnique()) {
			sb.append("create unique index ");
		} else {
			sb.append("create index ");
		}
		sb.append(index.getIndexName());
		sb.append(" on ");
		sb.append(index.getTable().getTableName());
		sb.append(" (");
		StringBuilder fsb = new StringBuilder();
		for (Field<?> f: index.getFieldList()) {
			if (fsb.length() > 0) {
				fsb.append(",");
			}
			fsb.append(f.getDbColumnName());
		}
		sb.append(fsb.toString());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * インデックス削除用SQLを作成します。
	 * @param index インデックス。
	 * @return インデックス削除用SQL。
	 */
	public String generateDropIndexSql(final Index index) {
		return "drop index " + index.getIndexName();
	}

	
}