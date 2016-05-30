package dataforms.dao.sqlgen.oracle;

import java.io.InputStream;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dataforms.dao.file.FileObject;
import dataforms.dao.sqlgen.SqlParser;

/**
 * Oracle用SqlParserクラス。
 * <pre>
 * OracleはParatemerMetaDataのサポートに問題があるため
 * ParamaterMetaDataを使用しないパラメータ設定ロジックを設定する。
 * </pre>
 */
public class OracleSqlParser extends SqlParser {
	/**
	 * コンストラクタ。
	 * @param sql SQL。
	 */
	public OracleSqlParser(final String sql) {
		super(sql);
	}

	@Override
	protected ParameterMetaData getParameterMetaData(final PreparedStatement st) throws SQLException {
		return null;
	}

	@Override
	protected int getParameterType(final ParameterMetaData meta, final int idx) throws Exception {
		// oracleの場合BLOB以外は0を指定しておけば問題ないみたい。
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * Oracleの場合ParameterMetaDataがまともにサポートされていないため独自に実装します。
	 * </pre>
	 *
	 */
	@Override
	protected void setBlobData(final PreparedStatement st, final int idx, final FileObject v, final ParameterMetaData meta) throws Exception {
		FileObject f = (FileObject) v;
		InputStream is = f.openInputStream();
		if (is != null) {
			this.getBlobIsList().add(is);
			st.setBlob(idx, is);
		} else {
			st.setNull(idx, java.sql.Types.BLOB);
		}
	}
}
