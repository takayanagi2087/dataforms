package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlClob;
import dataforms.dao.sqlgen.mysql.MysqlSqlGenerator;
import dataforms.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import dataforms.field.base.TextField;


/**
 * CLOBフィールドクラス。
 *
 */
public class ClobField extends TextField implements SqlClob {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public ClobField(final String fieldId) {
		super(fieldId);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "text");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longtext");
	}

	@Override
	public void setClientValue(final Object v) {
		this.setValue((String) v);
	}

}
