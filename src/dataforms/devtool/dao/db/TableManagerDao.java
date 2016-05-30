package dataforms.devtool.dao.db;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.func.FuncInfoTable;
import dataforms.controller.Page;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.util.ClassFinder;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * TableManagerPage用のDAOクラス。
 * TODO:export/import時のデータ変換の見直し。
 *
 */
public class TableManagerDao extends Dao {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TableManagerDao.class.getName());

	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public TableManagerDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}


	/**
	 * データベースが初期化されているかどうかを判定します。
	 *
	 * @return データベースが初期化されている場合true。
	 * @throws Exception 例外。
	 */
	public boolean isDatabaseInitialized() throws Exception {
		FuncInfoTable table = new FuncInfoTable();
		return this.tableExists(table.getTableName());
	}

	/**
	 * テーブルの存在チェックします。
	 * @param sequencename テーブル名。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	private boolean sequenceExists(final String sequencename) throws Exception {
		final SqlGenerator gen = this.getSqlGenerator();
		Dao dao = new Dao(this);
		String sql = gen.generateSequenceExistsSql();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sequenceName", sequencename);
		int seqcnt = NumberUtil.intValue(dao.executeScalarQuery(sql, param));
		return (seqcnt > 0);
	}

	/**
	 * テーブルクラスの一覧を取得します。
	 * @param data パラメータ。
	 * @return クエリ結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryTableClass(final Map<String, Object> data) throws Exception {
		// String funcpath = (String) data.get("functionSelect");
		String packageName = (String) data.get("packageName");
		String classname = (String) data.get("className");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> tableList = finder.findClasses(packageName, Table.class);
		int no = 1;
		for (Class<?> tblcls : tableList) {
			if (SubQuery.class.isAssignableFrom(tblcls)) {
				continue;
			}
			if (Table.class.getName().equals(tblcls.getName())) {
				continue;
			}
			if (!StringUtil.isBlank(classname)) {
				if (tblcls.getName().indexOf(classname) < 0) {
					continue;
				}
			}
			Map<String, Object> m = this.getTableInfo(tblcls.getName());
			if (m != null) {
				m.put("rowNo", Integer.valueOf(no));
				result.add(m);
			}
			no++;
		}
		return result;
	}

	/**
	 * テーブルの状態を取得します。
	 * @param m マップ。
	 * @param gen sqlジェネレータ。
	 * @param tbl テーブル。
	 * @throws Exception 例外。
	 */
	private void getTableStatus(final Map<String, Object> m, final SqlGenerator gen, final Table tbl) throws Exception {
		if (this.tableExists(tbl.getTableName())) {
			m.put("tableExists", Boolean.valueOf(true));
			m.put("status", "1");
			m.put("statusVal", "1");
			String sql = gen.generateRecordCountSql(tbl.getTableName());
			Dao dao = new Dao(this);
			Object c = dao.executeScalarQuery(sql, null);
			Integer reccnt = NumberUtil.intValue(c);
			m.put("recordCount", reccnt);
		} else {
			m.put("tableExists", Boolean.valueOf(false));
			m.put("status", "0");
			m.put("statusVal", "0");
			m.put("recordCount", Integer.valueOf(0));
		}
	}




	/**
	 * テーブルに関する情報を取得します。
	 * @param classname テーブルクラス名。
	 * @return テーブル情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getTableInfo(final String classname) throws Exception {
		Map<String, Object> tableInfo = new HashMap<String, Object>();
		tableInfo.put("checkedClass", classname);
		tableInfo.put("className", classname);
		Table tbl = Table.newInstance(classname);
		if (tbl == null) {
			return null;
		}
		tableInfo.put("tableName", tbl.getTableName());
		SqlGenerator gen = this.getSqlGenerator();
		List<String> sqllist = gen.generateCreateTableSqlList(tbl);
		StringBuilder sb = new StringBuilder();
		for (String sql : sqllist) {
			sb.append(sql); sb.append(";\n");
		}
		tableInfo.put("createTableSql", sb.toString());
		this.getTableStatus(tableInfo, gen, tbl);
		boolean st = tbl.structureAccords(this);
		tableInfo.put("difference", (st ? "0" : "1"));
		tableInfo.put("differenceVal", (st ? "0" : "1"));
		boolean seq = tbl.isAutoIncrementId();
		tableInfo.put("sequenceGeneration", (seq ? "1" : "0"));
		return tableInfo;
	}

	/**
	 * テーブル情報のリストを取得します。
	 * @param classlist テーブルクラス一覧。
	 * @return テーブル情報リスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTableInfoList(final List<String> classlist) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String cls : classlist) {
			Map<String, Object> m = this.getTableInfo(cls);
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}


	/**
	 * テーブルを削除します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void dropTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateDropTableSql(tbl.getTableName());
		this.executeUpdate(sql, (Map<String, Object>) null);
	}

	/**
	 * テーブルをバックアップテーブルに移動します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 * @return バックアップテーブル名。
	 */
	public String moveToBackupTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		SqlGenerator gen = this.getSqlGenerator();
		String oldname = tbl.getTableName();
		String newname = tbl.getBackupTableName();
		if (this.tableExists(newname)) {
			String sql = this.getSqlGenerator().generateDropTableSql(newname);
			this.executeUpdate(sql, (Map<String, Object>) null);
		}
		String sql = gen.generateRenameTableSql(oldname, newname);
		this.executeUpdate(sql, (Map<String, Object>) null);
		return newname;
	}


	/**
	 * テーブルのバックアップを取得します。
	 * @param classname テーブルクラス名。
	 * @param outdir 出力ディレクトリ。
	 * @return バックアップファイルのパス。
	 * @throws Exception 例外。
	 */
	public String exportData(final String classname, final String outdir) throws Exception {
		final Table tbl = Table.newInstance(classname);
		String datapath = outdir + "/" + classname.replaceAll("\\.", "/") + ".data.json";
		if (this.tableExists(tbl.getTableName())) {
			File f = new File(datapath);
			File dir = f.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String sql = "select * from " + tbl.getTableName();
			List<Map<String, Object>> result = this.executeQuery(sql, null);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> m: result) {
				Map<String, Object> rec = new HashMap<String, Object>();
				FieldList flist = tbl.getFieldList();
				for (Field<?> fld : flist) {
					String id = fld.getId();
					Object value = m.get(id);
					fld.setValueObject(value);
					if (fld.getClientValue() != null) {
						rec.put(id, fld.getClientValue().toString());
					} else {
						rec.put(id, null);
					}
				}
				list.add(rec);
			}

			String json = JSON.encode(list, true);

			final PrintWriter out = new PrintWriter(f, "utf-8");
			try {
				out.write(json);
			} finally {
				out.close();
			}

		}
		return datapath;
	}

	/**
	 * 初期データをインポートします。
	 * @param classname クラス名。
	 * @throws Exception 例外。
	 */
	public void importIntialData(final String classname) throws Exception {
		final Table tbl = Table.newInstance(classname);
		this.deleteAllRecord(tbl);
		List<Map<String, Object>> list = tbl.getInitialData();
		if (list != null) {
			String sql = this.getSqlGenerator().generateInsertSql(tbl);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> rec = new HashMap<String, Object>();
				Map<String, Object> m = list.get(i);
				for (Field<?> f : tbl.getFieldList()) {
					f.setClientValue(m.get(f.getId()));
					rec.put(f.getId(), f.getValue());
				}
				this.executeUpdate(sql, rec);
			}
		}
		String initialDataPath = Page.getServlet().getServletContext().getRealPath("/WEB-INF/initialdata");
		this.importData(classname, initialDataPath);
	}

	/**
	 * 指定フォルダのデータをインポートします。
	 * @param classname クラス名。
	 * @param path データのパス。
	 * @throws Exception データ。
	 */
	public void importData(final String classname, final String path) throws Exception {
		final Table tbl = Table.newInstance(classname);
		List<Map<String, Object>> list = tbl.getImportData(path);
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> data = tbl.getFieldList().convertClientToServer(list.get(i));
				if (this.existRecord(tbl, tbl.getPkFieldList(), data)) {
					this.executeUpdate(tbl, data);
				} else {
					this.executeInsert(tbl, data);
				}
			}
		}
	}

	/**
	 * テーブル作成します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	protected void createTable(final String className) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Table tbl = Table.newInstance(className);
		List<String> sqllist = gen.generateCreateTableSqlList(tbl);
		for (String sql: sqllist) {
			this.executeUpdate(sql, (Map<String, Object>) null);
		}
		if (gen.isSequenceSupported()) {
			String cseq = gen.generateCreateSequenceSql(tbl);
			if (cseq != null) {
				String seqname = tbl.getSequenceName();
				if (!this.sequenceExists(seqname)) {
					this.executeUpdate(cseq, (Map<String, Object>) null);
				}
			}
		}

	}

	/**
	 * テーブルの初期化を行います。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void initTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		// テーブルが存在したらバックアップを行う.
		if (this.tableExists(tbl.getTableName())) {
			this.moveToBackupTable(className);
		}
		this.createTable(className);
		this.importIntialData(className);
	}


	/**
	 * テーブル構造の更新を行います。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void updateTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		if (this.tableExists(tbl.getTableName())) {
			this.moveToBackupTable(className);
			this.createTable(className);
			String bakfile = tbl.getBackupTableName();
			List<Map<String, Object>> collist = this.getTableColumnList(bakfile);
			SqlGenerator gen = this.getSqlGenerator();
			String sql = gen.generateCopyDataSql(tbl,  bakfile, collist);
			this.executeUpdate(sql, (Map<String, Object>) null);
		} else {
			this.initTable(className);
		}
	}

}
