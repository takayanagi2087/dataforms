package dataforms.dao.sqlgen.oracle;

import java.sql.Connection;

import dataforms.annotation.SqlGeneratorImpl;
import dataforms.dao.Query;
import dataforms.dao.QueryPager;
import dataforms.dao.sqldatatype.SqlBigint;
import dataforms.dao.sqldatatype.SqlChar;
import dataforms.dao.sqldatatype.SqlInteger;
import dataforms.dao.sqldatatype.SqlNumeric;
import dataforms.dao.sqldatatype.SqlSmallint;
import dataforms.dao.sqldatatype.SqlTime;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.dao.sqlgen.SqlParser;
import dataforms.field.base.Field;
import dataforms.field.sqltype.NumericField;

/**
 * Oracle用SQL Generator。
 *
 */
@SqlGeneratorImpl(databaseProductName = OracleSqlGenerator.DATABASE_PRODUCT_NAME)
public class OracleSqlGenerator extends SqlGenerator {
	
	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "Oracle";
			

	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public OracleSqlGenerator(final Connection conn) {
		super(conn);
	}

	
	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	@Override
	public SqlParser newSqlParser(final String sql) {
		return new OracleSqlParser(sql);
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
	 * {@inheritDoc}
	 * テーブル情報を取得するときには、DatabaseMetadataに対し、大文字のテーブル名を渡す必要があるので
	 * テーブル名を大文字に変換します。
	 */
	@Override
	public String convertTableNameForDatabaseMetaData(final String tblname) {
		return tblname.toUpperCase();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String converTypeNameForDatabaseMetaData(final String type) {
		String ret = super.converTypeNameForDatabaseMetaData(type);
		if ("varchar2".equals(ret)) {
			return "varchar";
		} else if ("char".equals(ret)) {
				return "char";
		} else if ("float".equals(ret)) {
			return "real";
		} else if (ret.indexOf("timestamp") == 0) {
			return "timestamp";
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
	@Override
	public String getDatabaseType(final Field<?> field) {
		String type = field.getDbDependentType(DATABASE_PRODUCT_NAME);
		if (type != null) {
			return type;
		}
		String ret = "";
		if (field instanceof SqlVarchar) {
			ret = "nvarchar2(" + field.getLength() + ")";
		} else if (field instanceof SqlChar) {
			ret = "nchar(" + field.getLength() + ")";
		} else if (field instanceof SqlSmallint) {
			ret = "number(38,0)";
		} else if (field instanceof SqlBigint) {
			ret = "number(38,0)";
		} else if (field instanceof SqlInteger) {
			ret = "number(38,0)";
		} else if (field instanceof SqlNumeric) {
			NumericField nf = (NumericField) field;
			ret = "number(" + nf.getPrecision() + "," + nf.getScale() + ")";
		} else if (field instanceof SqlTime) {
			ret = "timestamp";
		} else {
			return super.getDatabaseType(field);
		}
		if (field.isNotNull()) {
			ret += " not null";
		} else {
			//ret += " null";
		}
		return ret;
	}

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
	 * {@inheritDoc}
	 * pg_stat_user_tablesにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = "select count(*) as table_exists from cat where LOWER(table_name) = :table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * information_schema.sequencesにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from user_sequences where LOWER(sequence_name)=:sequence_name";
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String seqname, final Long startValue) throws Exception {
		String ret = "create sequence " + seqname + " start with " + startValue + " minvalue 0";
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select " + tablename + "_seq.nextval as seq from dual";
	}

	@Override
	public String generateSysTimestampSql() {
		return "current_timestamp";
	}

	@Override
	public String generateGetPageSql(final QueryPager qp) {
		String orgsql = this.getOrgSql(qp);
	//	String sql = "select * from (select row_number() over() as row_no, m.* from (" + orgsql + ") as m) as m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		String sql = "select * from (select rownum as row_no, m.* from (" + orgsql + ") m) m where (:row_from + 1) <= m.row_no and m.row_no <= (:row_to + 1)";
		return sql;
	}

	@Override
	protected String getAsAliasSql() {
		return " ";
	}

	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param query 問い合わせ。
	 * @return レコード数をカウントするsql。
	 */
	@Override
	public String generateHitCountSql(final Query query) {
		String orgsql = this.generateQuerySql(query, true);
		String sql = "select count(*) as cnt from (" + orgsql + ") m";
		return sql;
	}


	/**
	 * レコード数をカウントするsqlを作成します。
	 * @param qp QueryPager・
	 * @return レコード数をカウントするsql。
	 */
	@Override
	public String generateHitCountSql(final QueryPager qp) {
		String orgsql = getOrgSql(qp);
		String sql = "select count(*) as cnt from (" + orgsql + ")";
		return sql;
	}


}
