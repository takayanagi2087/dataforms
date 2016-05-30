package dataforms.dao.sqlgen;

import java.io.InputStream;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dataforms.dao.file.FileObject;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * SQLパーサークラス。
 * <pre>
 * ":parameter"のSQLパラメータを処理するクラスです。
 * </pre>
 */
public class SqlParser {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(SqlParser.class.getName());

	/**
	 * オリジナルSQL。
	 */
	private String orgSql = null;
	/**
	 * 変換されたSQL。
	 */
	private String parsedSql = null;
	/**
	 * パラメータ配列。
	 */
	private List<String> paramnames = null;

	/**
	 * BLOB用のストリームリスト。
	 */
	private List<InputStream> blobIsList = new ArrayList<InputStream>();

	/**
	 * BLOB用のストリームリストを取得します。
	 * @return BLOB用のストリームリスト。
	 */
	public List<InputStream> getBlobIsList() {
		return blobIsList;
	}

	/**
	 * コンストラクタ。
	 * @param sql SQL。
	 */
	public SqlParser(final String sql) {
		this.orgSql = sql;
		this.parsedSql = this.parseSql(this.orgSql);
		log.debug("parsedSql = " + parsedSql);
	}

	/**
	 * :parameter形式のパラメータを解析します。
	 * @param sql SQL。
	 * @return パラメータを?に変換したSQL。
	 */
	private String parseSql(final String sql) {
		if (log.isDebugEnabled()) {
			log.debug("sql=" + sql);
		}
		this.paramnames = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile("\\:\\w+(\\[[0-9]+\\](\\.\\w+){0,1})?");
		Matcher m = p.matcher(sql);
		int idx = 0;
		while (m.find()) {
			int pos = m.start();
			sb.append(sql.substring(idx, pos));
			sb.append("?");
			String g = m.group();
			this.paramnames.add(g.substring(1));
			idx = pos + g.length();
		}
		if (idx < sql.length()) {
			sb.append(sql.substring(idx));
		}
		return sb.toString();
	}

	/**
	 * 解析済みSQLを取得します。
	 * @return 解析済みSQL。
	 */
	public final String getParsedSql() {
		return parsedSql;
	}

	/**
	 * パラメータに対応した値を取得します。
	 * @param p パラメータ名。
	 * @param param パラメータマップ。
	 * @return 値。
	 */
	private Object getParam(final String p, final Map<String, Object> param) {
//		return MapUtil.getValue(p, param);
		String [] sp = p.split("(\\[)|(\\]\\.)");
//		log.debug("sp.length=" + sp.length);
		if (sp.length == 2) {
			sp = p.split("(\\[)|(\\])");
			String json = (String) param.get(StringUtil.snakeToCamel(sp[0]));
//			log.debug("json=" + json);
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) JSON.decode(json);
			int idx = Integer.parseInt(sp[1]);
			return list.get(idx);
		} else if (sp.length == 3) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) param.get(StringUtil.snakeToCamel(sp[0]));
			int idx = Integer.parseInt(sp[1]);
			return list.get(idx).get(StringUtil.snakeToCamel(sp[2]));
		} else {
			return param.get(StringUtil.snakeToCamel(p));
		}
	}

	/**
	 * パラメータタイプを取得します。
	 * <pre>
	 * パラメータメタデータがサポートされていないOracle JDBCのドライバの場合組み直して使用します。
	 * </pre>
	 *
	 * @param meta ParameterMetaData。
	 * @param idx インデックス。
	 * @return パラメータタイプ。java.sql.Types.xxxの値。
	 * @throws Exception 例外。
	 */
	protected int getParameterType(final ParameterMetaData meta, final int idx) throws Exception {
		return meta.getParameterType(idx);
	}


	/**
	 * BLOB用パラメータ設定を行います。
	 * <pre>
	 * パラメータメタデータがサポートされていないOracle JDBCのドライバの場合組み直して使用します。
	 * </pre>
	 * @param st SQLステートメント。
	 * @param idx パラメータインデックス。
	 * @param v 値。
	 * @param meta パラメータメタデータ。
	 * @throws Exception 例外。
	 */
	protected void setBlobData(final PreparedStatement st, final int idx, final FileObject v, final ParameterMetaData meta) throws Exception {
		FileObject f =  v;
		log.debug("blobFileName=" + f.getFileName());
		InputStream is = f.openInputStream();
		if (is != null) {
			this.blobIsList.add(is);
			int type = meta.getParameterType(idx);
			if (type == java.sql.Types.BLOB) {
				st.setBlob(idx, is);
			} else {
				st.setBinaryStream(idx, is, is.available());
			}
		} else {
			int type = meta.getParameterType(idx);
			st.setNull(idx, type);
		}
	}


	/**
	 * statementにパラメータを設定します。
	 * @param st ステートメント。
	 * @param param パラメータ。
	 * @throws Exception 例外。
	 */
	public void setParameter(final PreparedStatement st, final  Map<String, Object> param) throws Exception {
		if (this.paramnames != null && param != null) {
			int idx = 1;
			ParameterMetaData meta = getParameterMetaData(st);
			for (String p : this.paramnames) {
				Object v = this.getParam(p, param);
				if (log.isDebugEnabled()) {
					log.debug(idx + " :" + p + "=" + v);
				}
				if (v == null) {
					st.setNull(idx, this.getParameterType(meta, idx));
				} else {
					if (v instanceof FileObject) {
						log.debug("valueClass=" + v.getClass().getName());
						this.setBlobData(st, idx, (FileObject) v, meta);
					} else {
						st.setObject(idx, v);
					}
				}
				idx++;
			}
		}
	}

	/**
	 * ParameterMetaDataを取得します。
	 * <pre>
	 * Oracle等のParameterMetaDataをサポートしていないJDBCドライバでは、
	 * 派生クラスでNULLを返すようにする。
	 * </pre>
	 * @param st PreparedStatement。
	 * @return ParameterMetaData.
	 * @throws SQLException 例外。
	 */
	protected ParameterMetaData getParameterMetaData(final PreparedStatement st) throws SQLException {
		ParameterMetaData meta = st.getParameterMetaData();
		return meta;
	}


	/**
	 * BLOB関連の後始末を行います。
	 * <pre>
	 * BLOBのinputStreamをcloseし、一時ファイルを削除します。
	 * </pre>
	 * @param param パラメータ。
	 * @throws Exception 例外。
	 */
	public final void removeBlobTempFile(final  Map<String, Object> param) throws Exception {
		for (InputStream is : this.blobIsList) {
			is.close();
		}
		if (this.paramnames != null && param != null) {
			for (String p : this.paramnames) {
				Object v = this.getParam(p, param);
				if (v != null) {
					if (v instanceof FileObject) {
						FileObject f = (FileObject) v;
						if (f.getTempFile() != null) {
							f.getTempFile().delete();
						}
					}
				}
			}
		}
	}
}
