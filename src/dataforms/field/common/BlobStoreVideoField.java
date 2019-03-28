package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.dao.sqlgen.mysql.MysqlSqlGenerator;
import dataforms.dao.sqlgen.pgsql.PgsqlSqlGenerator;

/**
 * BLOB保存動画ファイルフィールドクラス。
 *
 */
public class BlobStoreVideoField extends VideoField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreVideoField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreVideoField(final String id) {
		super(id);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "bytea");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longblob");
	}
}
