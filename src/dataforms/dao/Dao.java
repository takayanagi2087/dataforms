package dataforms.dao;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;

import dataforms.controller.ApplicationException;
import dataforms.controller.Page;
import dataforms.controller.WebComponent;
import dataforms.dao.file.BlobFileStore;
import dataforms.dao.file.FileObject;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.dao.sqlgen.SqlParser;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.BlobStoreFileField;
import dataforms.field.common.FileObjectField;
import dataforms.field.sqltype.BigintField;
import dataforms.field.sqltype.CharField;
import dataforms.field.sqltype.ClobField;
import dataforms.field.sqltype.DateField;
import dataforms.field.sqltype.DoubleField;
import dataforms.field.sqltype.IntegerField;
import dataforms.field.sqltype.NumericField;
import dataforms.field.sqltype.SmallintField;
import dataforms.field.sqltype.TimeField;
import dataforms.field.sqltype.TimestampField;
import dataforms.field.sqltype.VarcharField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * データアクセスクラス。
 *
 */
public class Dao implements JDBCConnectableObject {

    /**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(Dao.class.getName());


	/**
	 * JDBC接続可能オブジェクト。
	 */
	private WeakReference<JDBCConnectableObject> jdbcConnectableObject = null;

	/**
	 * SQLジェネレータ。
	 */
	private SqlGenerator sqlGenerator = null;


	/**
	 * BLOBの読み込みモード。
	 *
	 *
	 */
	public enum BlobReadMode {
		/**
		 * Webページに表示するための読み込みモードです(デフォルト)
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録します。
		 * ファイルの内容は読み込みません。
		 * </pre>
		 */
		FOR_DISPLAY_FILE_INFO,
		/**
		 * WebページからBLOB中のファイルをダウンロードする際の読み込みモードです。
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録し、
		 * ヘッダ部を除いたファイルの本体を一時ファイルに出力します。
		 * </pre>
		 */
		FOR_DOWNLOAD,
		/**
		 * BLOBを含むテーブルのレコードを他のレコードにコピーする際の読み込みモードです。
		 * <pre>
		 * BLOBに記録されたファイル情報ヘッダのみを読み込み、FileObjectのインスタンスに記録し、
		 * ヘッダ部を含んだBLOBの内容全体を一時ファイルに出力します。。
		 * </pre>
		 */
		FOR_DB_WRITING
	};

	/**
	 * BLOB読み込みモード。
	 */
	private BlobReadMode blobReadMode = BlobReadMode.FOR_DISPLAY_FILE_INFO;





	/**
	 * JDBC接続可能オブジェクトを設定設定します。
	 * @param cobj JDBC接続可能オブジェクト。
	 */
	public final void setJDBCConnectableObject(final JDBCConnectableObject cobj) {
		this.jdbcConnectableObject = new WeakReference<JDBCConnectableObject>(cobj);
	}


	@Override
	public final Connection getConnection() {
		return this.jdbcConnectableObject.get().getConnection();
	}

	/**
	 * コンストラクタ。
	 * @param cobj JDBC接続可能Object。
	 * @throws Exception 例外。
	 */
	public Dao(final JDBCConnectableObject cobj) throws Exception {
		this.setJDBCConnectableObject(cobj);
		this.sqlGenerator = SqlGenerator.getInstance(this.getConnection());
	}

	/**
	 * 現在のページを取得する。
	 * @return ページ。
	 */
	protected Page getPage() {
//		WebComponent comp = (WebComponent) this.jdbcConnectableObject.get();
//		return comp.getPage();
		JDBCConnectableObject cobj = this.jdbcConnectableObject.get();
		while (cobj instanceof Dao) {
			Dao dao = (Dao) cobj;
			cobj = dao.jdbcConnectableObject.get();
		}
		WebComponent comp = (WebComponent) cobj;
		return comp.getPage();
	}

	/**
	 * JDBC接続可能オブジェクトを取得します。
	 * @return JDBC接続可能オブジェクト。
	 */
	protected JDBCConnectableObject getJDBCConnectableObject() {
		return this.jdbcConnectableObject.get();
	}


	/**
	 * Blobフィールドの読み込みモードを取得します。
	 * @return Blobフィールドの読み込みモード。
	 */
	public BlobReadMode getBlobReadMode() {
		return blobReadMode;
	}

	/**
	 * Blobフィールドの読み込みモードを設定します。
	 * @param blobReadMode Blobフィールドの読み込みモード。
	 */
	public void setBlobReadMode(final BlobReadMode blobReadMode) {
		this.blobReadMode = blobReadMode;
	}


	/**
	 * BLOBダウンロードフラグを取得します。
	 *
	 * @return BLOBダウンロードフラグ。
	 * @deprecated getBlobReadMode()を使用してください。
	 */
	@Deprecated
	public boolean isBlobDownload() {
		return this.blobReadMode == BlobReadMode.FOR_DOWNLOAD;
	}

	/**
	 * BLOBダウンロードフラグを設定します。
	 * <pre>
	 * 通常はfalseに設定されており、BLOB項目はファイル名とサイズのみ取得します。
	 * BLOBの本体をダウンロードする場合、trueを設定すると、BLOBの内容を一時ファイルに
	 * 展開し、ダウンロード可能な状態にします。
	 * </pre>
	 * @param blobDownload BLOBダウンロードフラグ.
	 * @deprecated setBlobDownload(final boolean blobDownload)を使用してください。
	 */
	@Deprecated
	public void setBlobDownload(final boolean blobDownload) {
//		this.blobDownload = blobDownload;
		if (blobDownload) {
			this.blobReadMode = BlobReadMode.FOR_DOWNLOAD;
		} else {
			this.blobReadMode = BlobReadMode.FOR_DISPLAY_FILE_INFO;
		}
	}


	/**
	 * SQLジェネレータを取得するします。
	 * @return SQLジェネレータ。
	 */
	public SqlGenerator getSqlGenerator() {
		return sqlGenerator;
	}


	/**
	 * SQLジェネレータを設定します。
	 * @param sqlGenerator SQLジェネレータ。
	 */
	protected void setSqlGenerator(final SqlGenerator sqlGenerator) {
		this.sqlGenerator = sqlGenerator;
	}






	/**
	 * レコード処理クラス。
	 * <pre>
	 * 問い合わせ結果のレコードを一件ずつ処理するためのクラスです。
	 * </pre>
	 */
	public abstract class RecordProcessor {
		/**
		 * 1レコード処理します。
		 * @param rec レコード。
		 * @return 処理継続フラグ。
		 * @throws Exception 例外。
		 */
		public abstract boolean process(final Map<String, Object> rec) throws Exception;
	};

	/**
	 * カラム情報。
	 *
	 */
	private static class ColumnInfo {
		/**
		 * フィールドID。
		 */
		private String id;
		/**
		 * データ型。
		 */
		private int type;

		/**
		 * フィールド長。
		 */
		private int precision = 0;

		/**
		 * 小数点以下の桁数。
		 */
		private int scale = 0;

		/**
		 * コンストラクタ。
		 * @param id フィールドID。
		 * @param type データタイプ。
		 * @param precision データタイプ。
		 * @param scale データタイプ。
		 *
		 */
		public ColumnInfo(final String id, final int type, final int precision, final int scale) {
			this.id = StringUtil.snakeToCamel(id);
			this.type = type;
			this.precision = precision;
			this.scale = scale;
		}

		/**
		 * フィールドIDを取得します。
		 * @return フィールドID。
		 */
		public String getId() {
			return id;
		}

		/**
		 * データタイプを取得します。
		 * @return データタイプ。
		 */
		public int getType() {
			return type;
		}


		/**
		 * フィールド長を取得します。
		 * @return フィールド長。
		 */
		public int getPrecision() {
			return precision;
		}

		/**
		 * 小数点以下の桁数を取得します。
		 * @return 小数点以下の桁数。
		 */
		public int getScale() {
			return scale;
		}

		/**
		 * データ型に応じたフィールドクラスのインスタンスを取得します。
		 * @return デフォルトフィールド。
		 */
		public Field<?> getDefaultFieldInstance() {
			Field<?> ret = null;
			if (this.getType() == Types.BIGINT) {
				ret = new BigintField(this.getId());
			} else if (this.getType() == Types.BLOB
					 || this.getType() == Types.BINARY
					 || this.getType() == Types.LONGVARBINARY
					 || this.getType() == Types.VARBINARY) {
				ret = new BlobStoreFileField(this.getId());
			} else if (this.getType() == Types.CHAR) {
				ret = new CharField(this.getId(), this.getPrecision());
			} else if (this.getType() == Types.CLOB) {
				ret = new ClobField(this.getId());
			} else if (this.getType() == Types.DATE) {
				ret = new DateField(this.getId());
			} else if (this.getType() == Types.DOUBLE) {
				ret = new DoubleField(this.getId());
			} else if (this.getType() == Types.INTEGER) {
				ret = new IntegerField(this.getId());
			} else if (this.getType() == Types.NUMERIC) {
				ret = new NumericField(this.getId(), this.getPrecision(), this.getScale());
			} else if (this.getType() == Types.SMALLINT) {
				ret = new SmallintField(this.getId());
			} else if (this.getType() == Types.TIME) {
				ret = new TimeField(this.getId());
			} else if (this.getType() == Types.TIMESTAMP) {
				ret = new TimestampField(this.getId());
			} else if (this.getType() == Types.VARCHAR) {
				ret = new VarcharField(this.getId(), this.getPrecision());
			}
			return ret;
		}
	}

	/**
	 * 問合せ結果のカラムリスト。
	 */
	private List<ColumnInfo> resultSetColumnList = null;

	/**
	 * 問合せ結果のメタデータを取得します。
	 * @param meta メタデータ。
	 * @throws Exception 例外。
	 */
	private void setResultSetMetaData(final ResultSetMetaData meta) throws Exception {
		this.resultSetColumnList = new ArrayList<ColumnInfo>();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String name = meta.getColumnName(i);
			int type = meta.getColumnType(i);
			ColumnInfo ci = new ColumnInfo(name, type, meta.getPrecision(i), meta.getScale(i));
			this.resultSetColumnList.add(ci);
		}
	}

	/**
	 * 直前に実行した問い合わせのフィールドリストを取得します。
	 * <pre>
	 * ResultSetMetaDataの各カラムの情報から想定されるフィールドを作成し、そのリストを返します。
	 * </pre>
	 *
	 * @return フィールドリスト。
	 */
	public FieldList getResultSetFieldList() {
		FieldList ret = new FieldList();
		for (ColumnInfo ci: this.resultSetColumnList) {
			Field<?> f = ci.getDefaultFieldInstance();
			if (f != null) {
				ret.add(ci.getDefaultFieldInstance());
			}
		}
		return ret;
	}

	/**
	 * Queryを実行し、その結果の各レコードをRecordProcessorに渡します。
	 * @param sql SQL。
	 * @param data パラメータ。
	 * @param processor レコード処理クラス。
	 * @throws Exception 例外。
	 */
	public void executeQuery(final String sql, final Map<String, Object> data, final RecordProcessor processor) throws Exception {
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			p.setParameter(st, data);
			ResultSet rset = st.executeQuery();
			ResultSetMetaData meta = rset.getMetaData();
			this.setResultSetMetaData(meta);
			try {
				while (rset.next()) {
					Map<String, Object> m = new HashMap<String, Object>();
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						String name = meta.getColumnName(i);
						if (meta.getColumnType(i) == Types.BLOB) {
							Blob blob = rset.getBlob(i);
							FileObject obj = null;
							if (blob != null) {
								InputStream is = blob.getBinaryStream();
								BlobFileStore fs = new BlobFileStore(this.jdbcConnectableObject.get());
								obj = readBlob(is, fs);
							}
							m.put(StringUtil.snakeToCamel(name), obj);
						} else if (meta.getColumnType(i) == Types.BINARY
								 || meta.getColumnType(i) == Types.LONGVARBINARY
								 || meta.getColumnType(i) == Types.VARBINARY) {
							InputStream is = rset.getBinaryStream(i);
							BlobFileStore fs = new BlobFileStore(this.jdbcConnectableObject.get());
							FileObject obj = null;
							if (is != null) {
								obj = readBlob(is, fs);
							}
							m.put(StringUtil.snakeToCamel(name), obj);
						} else if (meta.getColumnType(i) == Types.CLOB) {
							Clob clob = rset.getClob(i);
							if (clob != null) {
								String val = clob.getSubString(1, (int) clob.length());
								m.put(StringUtil.snakeToCamel(name), val);
							}
						} else if (meta.getColumnType(i) == Types.TIMESTAMP) {
							Timestamp ts = rset.getTimestamp(i);
							if (ts != null) {
								m.put(StringUtil.snakeToCamel(name), ts);
							}
						} else {
							Object obj = rset.getObject(i);
							m.put(StringUtil.snakeToCamel(name), obj);
						}
					}
					if (!processor.process(m)) {
						break;
					}
				}
			} finally {
				rset.close();
			}
		} finally {
			st.close();
		}
	}


	/**
	 * BLOBフィールドの読み込みを行います。
	 * @param is BLOBの入力ストリーム。
	 * @param fs ファイルストア。
	 * @return 読み込み結果。
	 * @throws Exception 例外。
	 */
	public FileObject readBlob(final InputStream is, final BlobFileStore fs) throws Exception {
		FileObject obj;
		if (this.getBlobReadMode() == BlobReadMode.FOR_DOWNLOAD) {
			obj = fs.readForDownload(is);
		} else if (this.getBlobReadMode() == BlobReadMode.FOR_DB_WRITING) {
			obj = fs.readForDbWriting(is);
		} else {
			obj = fs.readFileInfo(is);
		}
		return obj;
	}


	/**
	 * SQLを実行し、その結果リストを返します。
	 * <pre>
	 * 各カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param data パラメータ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> executeQuery(final String sql, final Map<String, Object> data) throws Exception {
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.executeQuery(sql, data, new RecordProcessor() {
			@Override
			public boolean process(final Map<String, Object> rec) {
				list.add(rec);
				return true;
			}
		});
		return list;
	}

	/**
	 * 問い合わせを実行し、その結果リストを返します。
	 * <pre>
	 * 問い合わせ結果の各カラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 問い合わせ結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> executeQuery(final Query query) throws Exception {
		String sql = this.getSqlGenerator().generateQuerySql(query);
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.executeQuery(sql, this.convertToDBValue(query.getQueryFormFieldList(), query.getQueryFormData()), new RecordProcessor() {
			@Override
			public boolean process(final Map<String, Object> rec) {
				list.add(rec);
				return true;
			}
		});
		return this.convertFromDBValue(query.getFieldList(), list);
	}

	/**
	 * SQLの実行結果の先頭レコードを取得します。
	 * <pre>
	 * 各カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 先頭レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executeRecordQuery(final String sql, final Map<String, Object> param) throws Exception {
		List<Map<String, Object>> list = this.executeQuery(sql, param);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}


	/**
	 * 問い合わせ結果の先頭レコードを取得します。
	 * <pre>
	 * 問い合わせ結果の各カラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 先頭レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executeRecordQuery(final Query query) throws Exception {
		List<Map<String, Object>> list = this.executeQuery(query);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * クエリ結果の先頭レコードの先頭の値を取得します。
	 * <pre>
	 * カラムのデータ変換は行わず、DBの保存形式のまま取得します。
	 * </pre>
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 先頭レコードの先頭項目。
	 * @throws Exception 例外。
	 */
	public Object executeScalarQuery(final String sql, final Map<String, Object> param) throws Exception {
		Map<String, Object> rec = this.executeRecordQuery(sql, param);
		if (rec != null) {
			Collection<?> col = rec.values();
			return col.toArray()[0];
		} else {
			return null;
		}
	}


	/**
	 * クエリ結果の先頭レコードの先頭の値を取得します。
	 * <pre>
	 * 問い合わせ結果のカラムはフィールドの機能で変換(DBValue→Value)を行います。
	 * </pre>
	 * @param query 問い合わせ。
	 * @return 先頭レコードの先頭項目。
	 * @throws Exception 例外。
	 */
	public Object executeScalarQuery(final Query query) throws Exception {
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			Collection<?> col = rec.values();
			return col.toArray()[0];
		} else {
			return null;
		}
	}


	/**
	 * 更新系SQLを実行します。
	 * <pre>
	 * insert, update, delete等の更新系SQLを実行します。
	 * dataの変換は行わず、そのまま保存します。
	 * </pre>
	 * @param sql 実行するSQL。
	 * @param data パラメータ。
	 * @return 更新件数。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final String sql, final Map<String, Object> data) throws Exception {
		int ret = 0;
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			p.setParameter(st, data);
			try {
				ret = st.executeUpdate();
			} finally {
				p.removeBlobTempFile(data);
			}
		} catch (SQLIntegrityConstraintViolationException th) {
			logger.debug(th.getLocalizedMessage(), th);
			throw new ApplicationException(getPage(), "error.integrityconstraintviolation");
		} catch (SQLException e) {
			this.checkPsqlException(e);
		} finally {
			st.close();
		}
		return ret;
	}

	/**
	 * Postgresql用例外処理。
	 * @param e 例外。
	 * @throws ApplicationException アプリケーション例外。
	 * @throws Exception 例外。
	 */
	protected void checkPsqlException(final SQLException e) throws ApplicationException, Exception {
		if ("org.postgresql.util.PSQLException".equals(e.getClass().getName())) {
			SQLException sqlex = (SQLException) e;
			logger.debug("code=" + sqlex.getErrorCode() + ",msg=" +e.getLocalizedMessage(), e);
			throw new ApplicationException(getPage(), "error.integrityconstraintviolation");
		} else {
			throw e;
		}
	}

	/**
	 * 更新系SQLを実行します。
	 * <pre>
	 * insert, update, delete等の更新系SQLを実行します。
	 * dataの変換は行わず、そのまま保存します。
	 * dataList中の全Mapを指定されたSQLに渡して繰り返し実行します。
	 * </pre>
	 * @param sql 実行するSQL。
	 * @param dataList データリスト。
	 * @return 更新件数のリスト。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeUpdate(final String sql, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		SqlParser p = this.getSqlGenerator().newSqlParser(sql);
		Connection conn = this.getConnection();
		PreparedStatement st = conn.prepareStatement(p.getParsedSql());
		try {
			for (Map<String, Object> m: dataList) {
				p.setParameter(st, m);
				try {
					ret.add(st.executeUpdate());
				} finally {
					p.removeBlobTempFile(m);
				}
			}
		} catch (SQLIntegrityConstraintViolationException th) {
			logger.debug(th.getLocalizedMessage(), th);
			throw new ApplicationException(getPage(), "error.integrityconstraintviolation");
		} catch (SQLException e) {
			this.checkPsqlException(e);
		} finally {
			st.close();
		}
		return ret;
	}


	/**
	 * 新規登録用のレコードIDを取得します。
	 * <pre>
	 * シーケンスを使用して、レコードIDを生成します。
	 * </pre>
	 * @param tbl レコードIDを取得するテーブル。
	 * @param data 登録するデータマップ。
	 * @throws Exception 例外。
	 */
	private void setNewSequenceValue(final Table tbl, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		if (tbl.recordIdExists() && tbl.isAutoIncrementId() && gen.isSequenceSupported()) {
			String id = tbl.getIdField().getId();
			if (StringUtil.isBlank(data.get(id))) {
				String sql = gen.generateGetRecordIdSql(tbl);
				Long value = Long.valueOf(NumberUtil.longValue(this.executeScalarQuery(sql, null)));
				data.put(id, value);
			}
		}
	}


	/**
	 * 自動加算されたIDを取得する。
	 *
	 * @param table テーブル。
	 * @param data データマップ。
	 * @throws Exception 例外.
	 */
	private void getAutoIncrementValue(final Table table, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String keysql = gen.generateGetAutoIncrementValueSql(table);
		if (keysql != null) {
			if (table.isAutoIncrementId()) {
				String id = table.getIdField().getId();
				Object val = this.executeScalarQuery(keysql, null);
				data.put(id, Long.valueOf(NumberUtil.longValue(val)));
			}
		}
	}


	/**
	 * 指定されたテーブルに対して、データを挿入します。
	 * <pre>
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * ID自動生成フラグがtrueの場合、シーケンスまたはauto_Incrementカラム属性で
	 * IDを自動生成し、data中にidフィールドの値が設定されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data データ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	public int executeInsert(final Table table, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateInsertSql(table);
		this.setNewSequenceValue(table, data);
		int ret = this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data));
		this.getAutoIncrementValue(table, data);
		return ret;
	}



	/**
	 * 指定されたテーブルに対して、dataList中の全データをデータを挿入します。
	 * <pre>
	 * dataList中のデータはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList データリスト。
	 * @return 結果リスト。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeInsert(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateInsertSql(table);
		List<Integer> ret = new ArrayList<Integer>();
		for (Map<String, Object> data: dataList) {
			this.setNewSequenceValue(table, data);
			ret.add(this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data)));
			this.getAutoIncrementValue(table, data);
		}
		return ret;
	}

	/**
	 * 削除ファイルリスト。
	 * @param table テーブル。
	 * @param data データ。
	 * @param forDelete 削除用フラグ。
	 * @return 削除ファイルリスト。
	 * @throws Exception 例外。
	 */
	private List<String> getOldFileList(final Table table, final Map<String, Object> data, final boolean forDelete) throws Exception {
		List<String> ret = new ArrayList<String>();
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileObjectField && f instanceof SqlVarchar) {
				String kf = (String) data.get(f.getId() + "Kf");
				if ("0".equals(kf) || forDelete) {
					FileObjectQuery query = new FileObjectQuery(table, f.getId(), data);
					Map<String, Object> map = this.executeRecordQuery(query);
					FileObject oldfile = (FileObject) map.get(f.getId());
					if (oldfile != null) {
						File tf = oldfile.getTempFile();
						if (tf != null) {
							ret.add(tf.getAbsolutePath());
							logger.info("deleteFile=" + tf.getAbsolutePath() + "," + kf);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 古いファイルの削除を行います。
	 * @param list 古いファイルリスト。
	 * @throws Exception 例外。
	 */
	private void deleteOldFile(final List<String> list) throws Exception {
		for (String f: list) {
			File file = new File(f);
			file.delete();
		}
	}

	/**
	 * 指定テーブル更新を行います。
	 * <pre>
	 * テーブルに対応した、適切なupdate文を作成し実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新された場合、
	 * 既に記録されていたファイルは削除されます。
	 * </pre>
	 * @param table テーブル。
	 * @param data 更新データ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final Table table, final Map<String, Object> data) throws Exception {
		String sql = this.getSqlGenerator().generateUpdateSql(table);
		Map<String, Object> dbdata = this.convertToDBValue(table.getFieldList(), data);
		List<String> dellist = this.getOldFileList(table, dbdata, false);
		int ret = this.executeUpdate(sql, dbdata);
		this.deleteOldFile(dellist);
		return ret;
	}

	/**
	 * 指定テーブルの更新を行ないます。
	 * <pre>
	 * テーブルに対応した、適切なupdate文を作成し、dataListの全要素に対して実行すします。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新された場合、
	 * 既に記録されていたファイルは削除されます。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public List<Integer> executeUpdate(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateUpdateSql(table);
		for (Map<String, Object> data: dataList) {
			Map<String, Object> dbdata = this.convertToDBValue(table.getFieldList(), data);
			List<String> dellist = this.getOldFileList(table, dbdata, false);
			ret.add(this.executeUpdate(sql, dbdata));
			this.deleteOldFile(dellist);
		}
		return ret;
	}


	/**
	 * テーブル中のレコードを更新します。
	 * <pre>
	 * updateFieldList,condFieldListの内容から指定テーブルの更新SQLを作成し実行します。
	 * dataはフィールドの機能で変換(Value→DBValue)した後保存します。
	 * </pre>
	 * @param table テーブル。
	 * @param updateFieldList 更新対象のフィールドリスト。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * 	trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 *  falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 *  テーブル中にFolderStoreFileFieldが存在し、そのフィールドが更新されても、
	 *  既に記録されていたファイルは削除されません。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeUpdate(final Table table, final FieldList updateFieldList, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		int ret = 0;
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateUpdateSql(table, updateFieldList, condFieldList);
			ret = this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateUpdateSql(table, updateFieldList, condFieldList, m);
			ret = this.executeUpdate(sql, m);
		}
		return ret;
	}

	/**
	 * テーブル中の指定レコードを削除します。
	 * <pre>
	 * テーブルに対応した、適切なdelete文を作成し実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルを削除します。
	 * </pre>
	 * @param table テーブル.
	 * @param data 削除対象のレコードを示すデータ.
	 * @return 更新結果.
	 * @throws Exception 例外.
	 */
	public int executeDelete(final Table table, final Map<String, Object> data) throws Exception {
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		List<String> dellist = this.getOldFileList(table, m, true);
		String sql = this.getSqlGenerator().generateDeleteSql(table);
		int ret = this.executeUpdate(sql, m);
		this.deleteOldFile(dellist);
		return ret;
	}

	/**
	 * 指定されたテーブルに対して、dataList中の全データに対応するレコードを削除します。
	 * <pre>
	 * テーブルに対応した適切なdelete文を作成し、dataListの全要素に対して実行します。
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルを削除します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */

	public List<Integer> executeDelete(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateDeleteSql(table);
		for (Map<String, Object> data: dataList) {
			Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
			List<String> dellist = this.getOldFileList(table, m, true);
			ret.add(this.executeUpdate(sql, m));
			this.deleteOldFile(dellist);
		}
		return ret;
	}


	/**
	 * テーブル中のレコードを削除します。
	 * <pre>
	 * condFieldListの内容とdataから指定テーブルの削除SQLを作成し実行します。
	 * </pre>
	 * @param table テーブル。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 * falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 * テーブル中にFolderStoreFileFieldが存在した場合、そのフィールドに対応したファイルは削除されません。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeDelete(final Table table, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		int ret = 0;
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateDeleteSql(table, condFieldList);
			ret =this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateDeleteSql(table, condFieldList, data);
			ret = this.executeUpdate(sql, m);
		}
		return ret;
	}

	/**
	 * フォルダ保存ファイルフィールドが存在するテーブルを判定します。
	 * @param table テーブル。
	 * @return フォルダ保存ファイルフィールドが存在するテーブルの場合true。
	 */
	private boolean folderStoreFileExists(final Table table) {
		boolean ret = false;
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileObjectField && f instanceof SqlVarchar) {
				ret = true;
				break;
			}
		}
		return ret;
	}


	/**
	 * 指定されたファイルストアを削除します。
	 * @param f ファイルストアを指すフォルダ。
	 */
	private void deleteFolderFileStore(final File f) {
		if (!f.exists()) {
			return;
		}
		if (f.isFile()) {
			f.delete();
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFolderFileStore(files[i]);
			}
			f.delete();
		}
	}

	/**
	 * 全レコードの削除を行います。
	 * @param table テーブル。
	 * @return 削除件数。
	 * @throws Exception 例外。
	 *
	 */
	public int deleteAllRecord(final Table table) throws Exception {
		if (this.folderStoreFileExists(table)) {
			String store = DataFormsServlet.getUploadDataFolder() + "/" + table.getClass().getSimpleName();
			this.deleteFolderFileStore(new File(store));
		}
		String sql = this.getSqlGenerator().generateDeleteAllSql(table.getTableName());
		return this.executeUpdate(sql, (Map<String, Object>) null);
	}


	/**
	 * 指定レコードの削除フラグを設定します。
	 * <pre>
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * </pre>
	 * @param table テーブル.
	 * @param data 削除対象のレコードを示すデータ.
	 * @return 更新結果.
	 * @throws Exception 例外.
	 */
	public int executeRemove(final Table table, final Map<String, Object> data) throws Exception {
		String sql = this.getSqlGenerator().generateRemoveSql(table);
		return this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data));
	}

	/**
	 * 指定レコードの削除フラグを設定します。
	 * <pre>
	 * Where句はテーブルの全PKがマッチするように作成します。
	 * </pre>
	 * @param table テーブル。
	 * @param dataList 更新データリスト。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */

	public List<Integer> executeRemove(final Table table, final List<Map<String, Object>> dataList) throws Exception {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = this.getSqlGenerator().generateRemoveSql(table);
		for (Map<String, Object> data: dataList) {
			ret.add(this.executeUpdate(sql, this.convertToDBValue(table.getFieldList(), data)));
		}
		return ret;
	}


	/**
	 * 指定レコードの削除フラグを設定します。
	 *
	 * @param table テーブル。
	 * @param condFieldList 更新条件フィールドリスト。
	 * @param data データ。
	 * @param fullmatch 完全マッチフラグ。
	 * <pre>
	 * 	trueの場合condFieldList中の全項目に対する完全一致のwhere句が作成されます。
	 *  falseの場合condFieldList中の項目の内、data中に存在する項目のwhere句が作成されます。
	 * </pre>
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	public int executeRemove(final Table table, final FieldList condFieldList, final Map<String, Object> data, final boolean fullmatch) throws Exception {
		Map<String, Object> m = this.convertToDBValue(table.getFieldList(), data);
		if (fullmatch) {
			String sql = this.getSqlGenerator().generateRemoveSql(table, condFieldList);
			return this.executeUpdate(sql, m);
		} else {
			String sql = this.getSqlGenerator().generateRemoveSql(table, condFieldList, m);
			return this.executeUpdate(sql, m);
		}
	}


	/**
	 * カラム情報を取得します。
	 * @param rs 結果セット。
	 * @return カラム情報。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getColiumnInfo(final ResultSet rs) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Map<String, Object> colinfo = new HashMap<String, Object>();
		String cat = rs.getString("TABLE_CAT");
		String schem = rs.getString("TABLE_SCHEM");
		String name = rs.getString("COLUMN_NAME");
		String type = rs.getString("TYPE_NAME");
		int size = rs.getInt("COLUMN_SIZE");
		int scale = rs.getInt("DECIMAL_DIGITS");
		int nullable = rs.getInt("NULLABLE");
		String dataType = gen.converTypeNameForDatabaseMetaData(type);
		if ("char".equals(dataType) || "nchar".equals(dataType) || "varchar".equals(dataType) || "nvarchar".equals(dataType) || "nvarchar2".equals(dataType)) {
			dataType += "(" + size + ")";
		} else if ("numeric".equals(dataType) || "number".equals(dataType)) {
			dataType += "(" + size + "," + scale + ")";
		}
		if (nullable == 0) {
			dataType += " not null";
		}
		logger.debug(cat + " " + schem + " " + name + " " + dataType);
		colinfo.put("columnName", name.toLowerCase());
		colinfo.put("dataType", dataType);
		return colinfo;
	}

	/**
	 * Schemaを取得します。
	 * @param conn JDBC接続情報。
	 * @return 接続しているSchema。
	 */
	private String getSchema(final Connection conn) {
		String schema = null;
		try {
			schema = conn.getSchema();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return schema;
	}

	/**
	 * テーブル構造取得します。
	 * @param tblname テーブル名。
	 * @return テーブル構造。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTableColumnList(final String tblname) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		logger.debug("currentCatalog=" + conn.getCatalog());
		String schema = getSchema(conn);
		logger.debug("currentSchema=" + schema);
		List<Map<String, Object>> collist = new ArrayList<Map<String, Object>>();
		ResultSet rs = md.getColumns(conn.getCatalog(), schema, gen.convertTableNameForDatabaseMetaData(tblname), "%");
		logger.debug("----\n");
		try {
			while (rs.next()) {
				Map<String, Object> m = this.getColiumnInfo(rs);
				collist.add(m);
			}
		} finally {
			rs.close();
		}
		logger.debug("----\n");
		return collist;
	}


	/**
	 * 非一意フラグを取得します。
	 * <pre>
	 * OracleのみnonUniqueが数値型(Booleanではない)ので、
	 * このメソッドでその違いを吸収しています。
	 * </pre>
	 * @param nonUnique nonUniqueカラムのオブジェクト。
	 * @return Boolean型のフラグ。
	 */
	private Boolean getNonUnique(final Object nonUnique) {
		if (nonUnique instanceof BigDecimal) {
			BigDecimal v = (BigDecimal) nonUnique;
			return v.compareTo(BigDecimal.valueOf(0.0)) != 0;
		} else {
			return (Boolean) nonUnique;
		}
	}

	/**
	 * 指定されたテーブルのインデックスを取得します。
	 * @param md データベースメタデータ。
	 * @param catalog カタログ。
	 * @param schema スキーマ。
	 * @param table テーブル。
	 * @param unique ユニークフラグ。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> getCurrentDBIndexInfo(final DatabaseMetaData md, final String catalog, final String schema, final String table, final boolean unique) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		logger.debug("catalog=" + catalog + ", schema=" + schema + ", table=" + table + ", unique=" + unique);
		ResultSet rset = md.getIndexInfo(catalog, schema, table, unique, false);
		try {
			ResultSetMetaData rmd = rset.getMetaData();
			while (rset.next()) {
				Map<String, Object> m = new HashMap<String, Object>();
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					String name = StringUtil.snakeToCamel(rmd.getColumnName(i + 1).toLowerCase());
					Object value = rset.getObject(i + 1);
					m.put(name, value);
				}
				Object nu = m.get("nonUnique");
				Boolean nonUnique = this.getNonUnique(nu);
				logger.debug("nu=" + nu + ", nonUnique=" + nonUnique);
				if (nonUnique != unique) {
					ret.add(m);
				}
			}
		} finally {
			rset.close();
		}
		return ret;

	}


	/**
	 * 指定されたテーブルのデータベース中のインデックス情報を取得します。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getCurrentDBIndexInfo(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String catalog = conn.getCatalog();
		String schema = getSchema(conn);
		logger.debug("currentSchema=" + schema);
		String tablename = gen.convertTableNameForDatabaseMetaData(table.getTableName());
		List<Map<String, Object>> ret = this.getCurrentDBIndexInfo(md, catalog, schema, tablename, true);
		ret.addAll(this.getCurrentDBIndexInfo(md, catalog, schema, tablename, false));
		logger.debug("indexInfo=" + JSON.encode(ret, true));
		return ret;
	}


	/**
	 * 指定されたテーブルの外部キー情報を取得します。
	 * @param md データベースメタデータ。
	 * @param catalog カタログ。
	 * @param schema スキーマ。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> getCurrentDBForeignKeyInfo(final DatabaseMetaData md, final String catalog, final String schema, final String table) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		logger.debug("catalog=" + catalog + ", schema=" + schema + ", table=" + table);
		ResultSet rset = md.getImportedKeys(catalog, schema, table);
		try {
			ResultSetMetaData rmd = rset.getMetaData();
			while (rset.next()) {
				Map<String, Object> m = new HashMap<String, Object>();
				for (int i = 0; i < rmd.getColumnCount(); i++) {
					String name = StringUtil.snakeToCamel(rmd.getColumnName(i + 1).toLowerCase());
					Object value = rset.getObject(i + 1);
					m.put(name, value);
				}
				ret.add(m);
			}
		} finally {
			rset.close();
		}
		return ret;

	}



	/**
	 * 指定されたテーブルのデータベース中の外部キー情報を取得します。
	 * @param table テーブル。
	 * @return インデックス情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getCurrentDBForeignKeyInfo(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String catalog = conn.getCatalog();
		String schema = getSchema(conn);
		logger.debug("currentSchema=" + schema);
		String tablename = gen.convertTableNameForDatabaseMetaData(table.getTableName());
		List<Map<String, Object>> ret = this.getCurrentDBForeignKeyInfo(md, catalog, schema, tablename);
		logger.debug("ForeignKey Info=" + JSON.encode(ret, true));
		return ret;
	}

	/**
	 * 外部キーの名前の集合を取得します。
	 * @param table テーブル。
	 * @return 外部キーの名前の集合。
	 * @throws Exception 例外。
	 */
	protected Set<String> getForeignKeyNameSet(final Table table) throws Exception {
		List<Map<String, Object>> fklist = this.getCurrentDBForeignKeyInfo(table);
		Set<String> fkset = new HashSet<String>();
		for (Map<String, Object> m: fklist) {
			String fkName = (String) m.get("fkName");
			fkset.add(fkName);
		}
		return fkset;
	}


	/**
	 * インデックスが存在するかどうかを確認します。
	 * @param table テーブル。
	 * @param fkname インデックス名。
	 * @return インデックスが存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean foreignKeyExists(final Table table, final String fkname) throws Exception {
		boolean ret = false;
		List<Map<String, Object>> list = this.getCurrentDBForeignKeyInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("fkName");
			if (fkname.equalsIgnoreCase(indexName)) {
				ret = true;
			}
		}
		return ret;
	}


	/**
	 * インデックスが存在するかどうかを確認します。
	 * @param table テーブル。
	 * @param idxname インデックス名。
	 * @return インデックスが存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean indexExists(final Table table, final String idxname) throws Exception {
		boolean ret = false;
		List<Map<String, Object>> list = this.getCurrentDBIndexInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("indexName");
			if (idxname.equalsIgnoreCase(indexName)) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 指定されたインデックスのフィールドリストを取得します。
	 * @param table テーブル。
	 * @param idxname インデックス名称。
	 * @return インデックスのフィールド情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getIndexFieldList(final Table table, final String idxname) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = this.getCurrentDBIndexInfo(table);
		for (Map<String, Object> m: list) {
			String indexName = (String) m.get("indexName");
			if (idxname.equalsIgnoreCase(indexName)) {
				ret.add(m);
			}
		}
		return ret;
	}


	/**
	 * PKのリストを取得します。
	 * @param tbl テーブル。
	 * @return PKリスト。
	 * @throws Exception 例外。
	 */
	public List<String> getTablePkList(final Table tbl) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		List<String> collist = new ArrayList<String>();
		List<Short> seqlist = new ArrayList<Short>();
		String schema = getSchema(conn);
		logger.debug("currentSchema=" + schema);

		ResultSet rs = md.getPrimaryKeys(conn.getCatalog(), schema, gen.convertTableNameForDatabaseMetaData(tbl.getTableName()));
//		ResultSet rs = md.getPrimaryKeys("", "", tbl.getTableName().toLowerCase());
		try {
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME");
				collist.add(name.toLowerCase());
				short seq = rs.getShort("KEY_SEQ");
				seqlist.add(Short.valueOf((short) (seq - 1)));
			}
		} finally {
			rs.close();
		}
		List<String> pklist = new ArrayList<String>();
		for (int i = 0; i < collist.size(); i++) {
			pklist.add("");
		}
		for (int i = 0; i < collist.size(); i++) {
			pklist.set(seqlist.get(i), collist.get(i));
		}
		return pklist;
	}

	/**
	 * PKの最大値+1を取得します。
	 * <pre>
	 * 最下位PKの最大値+1を取得します。
	 * 最下位PKのタイプはBIGINTを想定します。
	 * </pre>
	 * @param tbl テーブル。
	 * @param param 上位のPKを指定するパラメータ。
	 * @return PKの最大値+1。
	 * @throws Exception 例外。
	 */
	public Long getGetNextPrimaryKey(final Table tbl, final Map<String, Object> param) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateGetLastPrimaryKeySql(tbl, param);
		Long ret = (Long) this.executeScalarQuery(sql, param);
		return ret.longValue() + 1;
	}


	/**
	 * 更新可能なレコードかどうかをチェックします。
	 * <pre>
	 * 楽観ロック用の更新可能チェック処理です。
	 * paramに含まれるupdateTimestampが一致することをチェックします。
	 * </pre>
	 * @param table テーブル。
	 * @param param パラメータ。
	 * @return 更新可能な場合true。
	 * @throws Exception 例外。
	 */
	public boolean isUpdatable(final Table table, final Map<String, Object> param) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateIsUpdatableSql(table);
		Map<String, Object> uinfo = this.executeRecordQuery(sql, param);
		if (uinfo != null) {
			Entity p = new Entity(param);
			java.sql.Timestamp ut0 = p.getUpdateTimestamp(); //(java.sql.Timestamp) param.get("updateTimestamp");
			if (/*uid0 != null &&*/ ut0 != null) {
				Entity u = new Entity(uinfo);
				java.sql.Timestamp ut1 = u.getUpdateTimestamp(); //(java.sql.Timestamp) uinfo.get("updateTimestamp");
//				return /*uid0.equals(uid1) &&*/ ut0.equals(ut1);
				logger.debug("isUpdatable:ut0=" + ut0.toString() + ",ut1=" + ut1.toString());
				return ut0.getTime() == ut1.getTime();
			} else {
				logger.warn("There is no updateUserId, updateTimestamp on this page.Therefore the exclusive control does not work.");
				return true;
			}
		} else {
			// TODO:この判定で良いか検討をする必要がある。
			return true;
		}
	}

	/**
	 * Query結果の指定ページを取得します。
	 * @param query 問い合わせ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final Query query) throws Exception {
		return this.executePageQuery(query, 10);
	}

	/**
	 * Query結果の指定ページを取得します。
	 * @param query 問い合わせ。
	 * @param defaultLines 1ページの行数のデフォルト値。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final Query query, final int defaultLines) throws Exception {
		Map<String, Object> param = query.getQueryFormData();
		Map<String, Object> ret = new HashMap<String, Object>();
		int linesPerPage = defaultLines;
		if (param.get("linesPerPage") != null) {
			linesPerPage = ((Integer) param.get("linesPerPage")).intValue();
		}
		int pageNo = 0;
		if (param.get("pageNo") != null) {
			pageNo = ((Integer) param.get("pageNo")).intValue();
		}
		QueryPager qp = new QueryPager(query, linesPerPage);
		param.putAll(qp.getPageParameter(pageNo));
		Long hitCount = this.countQueryResoult(query);
		ret.put("hitCount", hitCount);
		ret.put("linesPerPage", linesPerPage);
		ret.put("pageNo", pageNo);
		//
		String psql = this.getSqlGenerator().generateGetPageSql(qp);
		FieldList flist = query.getFieldList();
		Map<String, Object> data = this.convertToDBValue(query.getQueryFormFieldList(), query.getQueryFormData());
		List<Map<String, Object>> list = this.convertFromDBValue(flist, this.executeQuery(psql, data));
		ret.put("queryResult", list);
		return ret;
	}


	/**
	 * 問い合わせ結果の件数を求める。
	 * @param query 問い合わせ。
	 * @return 結果件数。
	 * @throws Exception 例外。
	 */
	private Long countQueryResoult(final Query query) throws Exception {
		String csql = this.getSqlGenerator().generateHitCountSql(query);
		// FieldList qflist = query.getQueryFormFieldList();
		// Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, qflist.convertServerToDb(query.getQueryFormData())));
		Map<String, Object> data = this.convertToDBValue(query.getQueryFormFieldList(), query.getQueryFormData());
		Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, data));
		return hitCount;
	}

	/**
	 * 問合せ結果の指定ページを取得します。
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 問合せ結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final String sql, final Map<String, Object> param) throws Exception {
		return this.executePageQuery(sql, param, 10);
	}

	/**
	 * 問合せ結果の指定ページを取得します。
	 *
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @param defaultLines 1ページの行数のデフォルト値。
	 *
	 * @return 問合せ結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> executePageQuery(final String sql, final Map<String, Object> param, final int defaultLines) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		int linesPerPage = defaultLines;
		if (param.get("linesPerPage") != null) {
			linesPerPage = ((Integer) param.get("linesPerPage")).intValue();
		}
		int pageNo = 0;
		if (param.get("pageNo") != null) {
			pageNo = ((Integer) param.get("pageNo")).intValue();
		}
		QueryPager qp = new QueryPager(sql, linesPerPage);
		param.putAll(qp.getPageParameter(pageNo));
		Long hitCount = this.countQueryResoult(sql, param);
		ret.put("hitCount", hitCount);
		ret.put("linesPerPage", linesPerPage);
		ret.put("pageNo", pageNo);
		//
		String psql = this.getSqlGenerator().generateGetPageSql(qp);
		List<Map<String, Object>> list = this.executeQuery(psql, param);
		ret.put("queryResult", list);
		return ret;
	}


	/**
	 * 問い合わせ結果の件数を求める。
	 * @param sql SQL。
	 * @param param パラメータ。
	 * @return 結果件数。
	 * @throws Exception 例外。
	 */
	private Long countQueryResoult(final String sql, final Map<String, Object> param) throws Exception {
		String csql = this.getSqlGenerator().generateHitCountSql(sql);
		Long hitCount = NumberUtil.longValue(this.executeScalarQuery(csql, param));
		return hitCount;
	}

	/**
	 * レコードの存在チェックを行います。
	 * @param table テーブル。
	 * @param flist フィールドリスト。
	 * @param data フォームデータ。
	 * @param forUpdate 更新用の存在チェックの場合true。
	 * <pre>
	 * 更新用の場合、更新対象以外のレコードに同一値があるかどうかをチェック。
	 * </pre>
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean existRecord(final Table table, final FieldList flist, final Map<String, Object> data, final boolean forUpdate) throws Exception {
		Query q = new Query();
		q.setFieldList(table.getPkFieldList());
		q.setMainTable(table);
		if (forUpdate) {
			StringBuilder sb = new StringBuilder();
			for (Field<?> f: table.getPkFieldList()) {
				if (sb.length() > 0) {
					sb.append(" or ");
				}
				sb.append("m.");
				sb.append(StringUtil.camelToSnake(f.getId()));
				sb.append("<>:");
				sb.append(StringUtil.camelToSnake(f.getId()));
				sb.append(" ");

			}
			q.setCondition("(" + sb.toString() + ")");
		}
		q.setQueryFormFieldList(flist);
		q.setQueryFormData(data);
//		String sql = this.getSqlGenerator().generateQuerySql(q);
//		List<Map<String, Object>> list = this.executeQuery(sql, data);
		List<Map<String, Object>> list = this.executeQuery(q);

		return (list.size() > 0);
	}


	/**
	 * レコードの存在チェックを行います。
	 * @param table テーブル。
	 * @param flist フィールドリスト。
	 * @param data フォームデータ。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	protected boolean existRecord(final Table table, final FieldList flist, final Map<String, Object> data) throws Exception {
		return this.existRecord(table, flist, data, false);
	}

	/**
	 * テーブルの存在チェックを行います。
	 * @param tablename テーブル名。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	public boolean tableExists(final String tablename) throws Exception {
		final SqlGenerator gen = this.getSqlGenerator();
		Dao dao = new Dao(this);
		String sql = gen.generateTableExistsSql();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tableName", tablename);
		int tblcnt = NumberUtil.intValue(dao.executeScalarQuery(sql, param));
		return (tblcnt > 0);
	}




	/**
	 * DBから読み込んだマップをアプリケーションで処理しやすい形式に変換します(DBValue→Value変換)。
	 * @param flist フィールドリスト。
	 * @param m DBから読み込んだマップ。
	 * @return 返還後のマップ。
	 */
	protected Map<String, Object> convertFromDBValue(final FieldList flist, final Map<String, Object> m) {
		if (m == null) {
			return m;
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		// 一旦全要素をコピーする。
		ret.putAll(m);
		if (flist != null) {
			// 変換が必要なものは変換してコピーする。
			ret.putAll(flist.convertDbToServer(m));
		}
		return ret;
	}

	/**
	 * DBから読み込んだリストをアプリケーションで処理しやすい形式に変換します(DBValue→Value変換)。
	 * @param flist フィールドリスト。
	 * @param list リスト。
	 * @return 変換後のリスト。
	 */
	protected List<Map<String, Object>> convertFromDBValue(final FieldList flist, final List<Map<String, Object>> list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			ret.add(this.convertFromDBValue(flist, m));
		}
		return ret;
	}
	/**
	 * マップの内容をDBの保存形式に変換します(Value→DBValue変換)。
	 * @param flist フィールドリスト。
	 * @param m マップ。
	 * @return 返還後のマップ。
	 */
	protected Map<String, Object> convertToDBValue(final FieldList flist, final Map<String, Object> m) {
		if (m == null) {
			return m;
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		// 一旦全要素をコピーする。
		ret.putAll(m);
		if (flist != null) {
			// 変換が必要なものは変換してコピーする。
			ret.putAll(flist.convertServerToDb(m));
		}
		// TODO:条件がテーブルになる場合の変換を検討。
		return ret;
	}

	/**
	 * リストの内容をDBの保存形式に変換します(Value→DBValue変換)。
	 * @param flist フィールドリスト。
	 * @param list リスト。
	 * @return 返還後のリスト。
	 */
	protected List<Map<String, Object>> convertToDBValue(final FieldList flist, final List<Map<String, Object>> list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			ret.add(this.convertToDBValue(flist, m));
		}
		return ret;
	}

	/**
	 * BLOBデータを取得する問い合わせを行います。
	 *
	 */
	private class FileObjectQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param table テーブル。
		 * @param fieldId BLOBフィールドID。
		 * @param data パラメータ。
		 */
		public FileObjectQuery(final Table table, final String fieldId, final Map<String, Object> data) {
			FieldList flist = new FieldList();
			flist.addAll(table.getPkFieldList());
			flist.add(table.getField(fieldId));
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setQueryFormFieldList(table.getPkFieldList());
			this.setQueryFormData(data);
		}
	}

	/**
	 * BLOBデータを取得します。
	 * @param table テーブル。
	 * @param fieldId BLOBフィールドID。
	 * @param data パラメータ。
	 * @return BLOBデータ。
	 * @throws Exception 例外。
	 */
	public FileObject queryBlobFileObject(final Table table, final String fieldId, final Map<String, Object> data) throws Exception {
		this.setBlobReadMode(BlobReadMode.FOR_DOWNLOAD);
		FileObjectQuery query = new FileObjectQuery(table, fieldId, data);
		Map<String, Object> ret = (Map<String, Object>) this.executeRecordQuery(query);
		this.setBlobReadMode(BlobReadMode.FOR_DISPLAY_FILE_INFO);
		return (FileObject) ret.get(fieldId);
	}


	/**
	 * BLOBデータの情報のみを取得します。
	 * @param table テーブル。
	 * @param fieldId BLOBフィールドID。
	 * @param data パラメータ。
	 * @return BLOBデータ。
	 * @throws Exception 例外。
	 */
	public FileObject queryBlobFileInfo(final Table table, final String fieldId, final Map<String, Object> data) throws Exception {
		FileObjectQuery query = new FileObjectQuery(table, fieldId, data);
		Map<String, Object> ret = (Map<String, Object>) this.executeRecordQuery(query);
		return (FileObject) ret.get(fieldId);
	}



	/**
	 * 明細テーブルの保存を行います。
	 * <pre>
	 * PKの配置がヘッダID,明細IDとなっていることを前提条件として、明細を保存します。
	 * ヘッダテーブルに対応した明細テーブルの保存は、通常対応レコードの
	 * 全削除、挿入で実装するのが簡単です。
	 * しかしBLOB項目等を含む場合の更新は毎回ファイルをやり取りするわけでは
	 * ないので、複雑な更新処理が必要になります。
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存明細レコードを削除します。
	 * 2.明細のIDがnullの場合、明細のIDを作成し挿入します。
	 * 3.明細のIDがnullでない場合、対応レコードを更新します。
	 *
	 * </pre>
	 * @param table 明細テーブル。
	 * @param list 保存する明細リスト。
	 * @param data ヘッダ情報。
	 * @throws Exception 例外。
	 */
	protected void saveTable(final Table table, final List<Map<String, Object>> list, final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		String delsql = gen.generateSelectNotInListSql(table, list);
		if (delsql != null) {
//			this.executeUpdate(delsql, data);
			List<Map<String, Object>> dellist = this.executeQuery(delsql, data);
			for (Map<String, Object> m: dellist) {
				this.executeDelete(table, m);
			}
		}
		String maxsql = gen.generateGetLastPrimaryKeySql(table, data);
		FieldList pklist = table.getPkFieldList();
		Field<?> lastpk = pklist.get(pklist.size() - 1);
		for (Map<String, Object> m: list) {
			if (m.get(lastpk.getId()) == null) {
				if (lastpk instanceof SmallintField) {
					short key = (short) (NumberUtil.shortValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Short.valueOf(key));
				} else if (lastpk instanceof IntegerField) {
					int key = (int) (NumberUtil.intValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Integer.valueOf(key));
				} else if (lastpk instanceof BigintField) {
					long key = (long) (NumberUtil.longValue(this.executeScalarQuery(maxsql, data)) + 1);
					m.put(lastpk.getId(), Long.valueOf(key));
				} else {
					throw new Exception("Unsupported primary key type.");
				}
				this.executeInsert(table, m);
			} else {
				if (this.findRecord(table, m) == null) {
					this.executeInsert(table, m);
				} else {
					this.executeUpdate(table, m);
				}
			}
		}
	}

	/**
	 * 指定されたレコードと同じPKを持つレコードがリスト中に存在するかどうかをチェックします。
	 *
	 * @param table テーブル。
	 * @param rec レコード。
	 * @param list レコードリスト。
	 * @return レコードがリスト中に存在する場合true。
	 */
	protected boolean existRecord(final Table table, final Map<String, Object> rec, final List<Map<String, Object>> list) {
		boolean ret = false;
		FieldList pklist = table.getPkFieldList();
		for (Map<String, Object> m: list) {
			boolean eq = true;
			for (Field<?> f: pklist) {
				Object k0 = rec.get(f.getId());
				Object k1 = m.get(f.getId());
				if (k1 == null) {
					eq = false;
					continue;
				}
				if (!k1.equals(k0)) {
					eq = false;
					break;
				}
			}
			if (eq) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * oldlistに存在し、listに存在しないレコードを削除します。
	 * @param table テーブル。
	 * @param list 新リスト。
	 * @param oldlist 旧リスト。
	 * @throws Exception 例外。
	 */
	protected void deleteNotExistRecord(final Table table, final List<Map<String, Object>> list, final List<Map<String, Object>> oldlist) throws Exception {
		for (Map<String, Object> om: oldlist) {
			if (!this.existRecord(table, om, list)) {
				this.executeDelete(table, om);
			}
		}
	}

	/**
	 * 明細テーブルの保存を行います。
	 * <pre>
	 * PKが明細IDのみでヘッダのIDを別途持つことを前提条件として明細を保存します。
	 *
	 * ヘッダテーブルに対応した明細テーブルの保存は、通常対応レコードの
	 * 全削除、挿入で実装するのが簡単です。
	 * しかしBLOB項目等を含む場合の更新は毎回ファイルをやり取りするわけでは
	 * ないので、複雑な更新処理が必要になります。
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存レコードを削除します。
	 * 2.レコードのIDがnullの場合、レコードのIDを作成し挿入します。
	 * 3.レコードのIDがnullでない場合、対応レコードを更新します。
	 *
	 * flistにはヘッダのIDフィールドを指定し、condはヘッダのIDを含むマップである必要があります。
	 *
	 * </pre>
	 * @param table テーブル。
	 * @param list 保存するレコードの全リスト。
	 * @param cond 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @throws Exception 例外。
	 */
	public void saveTable(final Table table, final List<Map<String, Object>> list, final Map<String, Object> cond, final FieldList flist) throws Exception {
		Query query = new Query();
		query.setFieldList(table.getFieldList());
		query.setMainTable(table);
		if (flist != null) {
			query.setQueryFormFieldList(flist);
		}
		if (cond != null) {
			query.setQueryFormData(cond);
		}
		List<Map<String, Object>> oldlist = this.executeQuery(query);
		this.deleteNotExistRecord(table, list, oldlist);
		for (Map<String, Object> m: list) {
			if (this.existRecord(table, m, list)) {
				boolean ret = this.isUpdatable(table, m);
				if (!ret) {
					throw new ApplicationException(this.getPage(), "error.notupdatable");
				}
				this.executeUpdate(table, m);
			} else {
				this.executeInsert(table, m);
			}
		}
	}

	/**
	 * テーブルの全レコードを保存します。
	 * <pre>
	 * このメソッドは以下のロジックでBLOBを含む明細テーブルの更新に対応します。
	 *
	 * 1.listに含まなれない既存レコードを削除します。
	 * 2.レコードのIDがnullの場合、レコードのIDを作成し挿入します。
	 * 3.レコードのIDがnullでない場合、対応レコードを更新します。
	 * </pre>
	 *
	 * @param table テーブル。
	 * @param list 保存するレコードの全リスト。
	 * @throws Exception 例外。
	 */
	public void saveTable(final Table table, final List<Map<String, Object>> list) throws Exception {
		this.saveTable(table, list, null, null);
	}

	/**
	 * レコードを取得します。
	 *
	 * @param table テーブル。
	 * @param rec レコード(PK項目値を含むMap)
	 * @return レコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> findRecord(final Table table, final Map<String, Object> rec) throws Exception {
		Query query = new Query();
		query.setFieldList(table.getFieldList());
		query.setMainTable(table);
		query.setQueryFormFieldList(table.getPkFieldList());
		query.setQueryFormData(rec);
		return this.executeRecordQuery(query);
	}
}
