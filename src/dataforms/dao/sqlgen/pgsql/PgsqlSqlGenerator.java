package dataforms.dao.sqlgen.pgsql;

import java.sql.Connection;

import dataforms.annotation.SqlGeneratorImpl;
import dataforms.dao.QueryPager;
import dataforms.dao.Table;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.Field;
import dataforms.util.StringUtil;

/**
 * PostgreSQL用SQL Generator。
 *
 */
@SqlGeneratorImpl(databaseProductName = PgsqlSqlGenerator.DATABASE_PRODUCT_NAME)
public class PgsqlSqlGenerator extends SqlGenerator {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(PgsqlSqlGenerator.class.getName());

	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "PostgreSQL";


	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public PgsqlSqlGenerator(final Connection conn) {
		super(conn);
	}

	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	/**
	 * {@inheritDoc}
	 * シーケンスをサポートしているのでtrueを返します。
	 */
	@Override
	public boolean isSequenceSupported() {
		return true;
	}

	/**
	 * インポートされたデータに応じてシーケンスを調整します。。
	 * @param table テーブル。
	 * @return シーケンス調整SQL。
	 * @throws Exception 例外。
	 */
	@Override
	public String generateAdjustSequenceSql(final Table table) throws Exception {
		String seqName = table.getSequenceName();
		Field<?> idField = table.getIdField();
		String tableName = table.getTableName();
		String ret = "select setval('" + seqName +
				"', (select max(" + StringUtil.camelToSnake(idField.getId()) +
				") + 1 from " + tableName + "))";
		return ret;
	}


	/**
	 * {@inheritDoc}
	 * テーブル情報を取得するときには、DatabaseMetadataに対し、小文字のテーブル名を渡す必要があるので
	 * テーブル名を小文字に変換します。
	 */
	@Override
	public String convertTableNameForDatabaseMetaData(final String tblname) {
		return tblname.toLowerCase();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 以下のタイプ名を変換します。
	 * int2 -&gt; smallint
	 * int4 -&gt; integer
	 * int8 -&gt; bigint
	 * bpchar -&gt; char
	 * float4 -&gt; real
	 * </pre>
	 */
	@Override
	public String converTypeNameForDatabaseMetaData(final String type) {
		String ret = super.converTypeNameForDatabaseMetaData(type);
		if ("int2".equals(ret)) {
			return "smallint";
		} else if ("int4".equals(ret)) {
			return "integer";
		} else if ("int8".equals(ret)) {
			return "bigint";
		} else if ("bpchar".equals(ret)) {
			return "char";
		} else if ("float4".equals(ret)) {
			return "real";
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * SqlClobの実装クラスはtext型のカラムを作成します。
	 * SqlBlobの実装クラスはbytea型のカラムを作成します。
	 * </pre>
	 */
/*	@Override
	public String getDatabaseType(final Field<?> field) {
		String type = field.getDbDependentType(DATABASE_PRODUCT_NAME);
		if (type != null) {
			return type;
		}
		// TODO:DB依存型で実装しなおす。
		if (field instanceof SqlBlob) {
			return "bytea";
		} else if (field instanceof SqlClob) {
			return "text";
		} else {
			return super.getDatabaseType(field);
		}
	}*/

	/**
	 * {@inheritDoc}
	 * postgresqlは標準的なcomment文をサポートするので、COMMENTを返します。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		return SqlGenerator.CommentSyntax.COMMENT;
	}

	/**
	 * スキーマ名を取得します。
	 * @return スキーマ名。
	 */
/*	private String getSchema() {
		String ret = null;
		try {
			Connection conn = this.getConnection();
			log.debug("catalog=" + conn.getCatalog());
			ret = conn.getCatalog();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ret;
	}
*/
	/**
	 * {@inheritDoc}
	 * pg_stat_user_tablesにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		//this.getSchema();
		String sql = "SELECT count(*) as table_exists FROM information_schema.tables WHERE LOWER(table_name) = :table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * information_schema.sequencesにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from information_schema.sequences where LOWER(sequence_name)=:sequence_name";
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String seqname, final Long startValue) throws Exception {
		String ret = "create sequence " + seqname + " start with " + startValue + " minvalue " + startValue;
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select nextval('" + tablename + "_seq') as seq";
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	@Override
	public String generateGetPageSql(final QueryPager qp) {
		String orgsql = this.getOrgSql(qp);
		String sql = "select * from (select row_number() over() as row_no, m.* from (" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}

	/**
	 * Like文のEscape指定文字列を取得します。
	 * <pre>
	 * PostgreSQLは\がEscape文字となるのでその対策。
	 * </pre>
	 * @return Like文のEscape指定文字列。
	 */
	// var9.0以前ではpostgresql.confのstandard_conforming_strings をonに設定してください。
/*	protected String getLikeEscape() {
		return " {escape '\\\\'} ";
	}*/

	@Override
	public String getRebildSqlFolder() {
		return "/WEB-INF/dbRebuild/pgsql";
	}

}
