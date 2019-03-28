package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.dao.sqlgen.mysql.MysqlSqlGenerator;
import dataforms.dao.sqlgen.pgsql.PgsqlSqlGenerator;

/**
 * BLOB保存音声ファイルフィールドクラス。
 *
 */
public class BlobStoreAudioField extends AudioField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreAudioField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreAudioField(final String id) {
		super(id);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "bytea");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longblob");
	}
}
