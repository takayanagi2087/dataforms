package dataforms.dao.sqlgen.derby;

import java.sql.Connection;

import dataforms.annotation.SqlGeneratorImpl;
import dataforms.dao.QueryPager;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.Field;

/**
 * Apache Derby用SQL Generator.
 *
 */
@SqlGeneratorImpl(databaseProductName = DerbySqlGenerator.DATABASE_PRODUCT_NAME)
public class DerbySqlGenerator extends SqlGenerator {
	/**
	 * データベースシステムの名称。
	 */
	public static final String DATABASE_PRODUCT_NAME = "Apache Derby";
	
	/**
	 * コンストラクタ.
	 * @param conn JDBC接続情報.
	 */
	public DerbySqlGenerator(final Connection conn) {
		super(conn);
	}
	
	@Override
	public String getDatabaseProductName() {
		return DATABASE_PRODUCT_NAME;
	}

	
	@Override
	public String getDatabaseType(final Field<?> field) {
		String type = field.getDbDependentType(DATABASE_PRODUCT_NAME);
		if (type != null) {
			return type;
		}
		return super.getDatabaseType(field);
	}
	
	/**
	 * {@inheritDoc}
	 * テーブル、カラムのcommentはサポートされていません。
	 *
	 */
	@Override
	protected CommentSyntax getCommentSyntax() {
		// commentはサポートしない.
		return SqlGenerator.CommentSyntax.NONE;
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
	 * SYS.SYSTABLESにテーブルが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateTableExistsSql() {
		String sql = "select count(*) as TABLE_EXISTS from SYS.SYSTABLES where LOWER(TABLENAME)=:table_name";
		return sql;
	}

	/**
	 * {@inheritDoc}
	 * SYS.SYSSEQUENCESにシーケンスが登録されているか確認するSQLを作成します。
	 */
	@Override
	public String generateSequenceExistsSql() {
		String sql = "select count(*) as SEQUENCE_EXISTS from SYS.SYSSEQUENCES where LOWER(sequencename)=:sequence_name";
		return sql;
	}

	@Override
	public String generateRenameTableSql(final String oldname, final String newname) {
		String sql = "RENAME TABLE " + oldname + " TO " + newname;
		return sql;
	}


	@Override
	public String generateCreateSequenceSql(final String sequencename, final Long startValue) throws Exception {
		String ret = null;
		ret = "create sequence " + sequencename + " as bigint start with " + startValue;
		return ret;
	}

	@Override
	public String generateDropSequenceSql(final String tablename) throws Exception {
		String ret = null;
		ret = "drop sequence " + tablename + " restrict";
		return ret;
	}

	@Override
	public String generateGetRecordIdSql(final String tablename) throws Exception {
		return "select seq from (values next value for " + tablename + "_seq) t(seq)";
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
}
