package dataforms.devtool.page.db;

import java.sql.Connection;

import dataforms.controller.Form;
import dataforms.field.sqltype.VarcharField;

/**
 * データベース情報フォームクラス。
 * <pre>
 * 接続しているデータベースの製品名、バージョン情報を表示するフォームです。
 * </pre>
 */
public class DatabaseInfoForm extends Form {
	/**
	 * コンストラクタ。
	 */
	public DatabaseInfoForm() {
		super(null);
		this.addField(new VarcharField("dbServerName", 256));
		this.addField(new VarcharField("dbServerVersion", 256));
		this.addField(new VarcharField("dbServerURL", 256));
//		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * データベースの製品名とバージョンを取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		Connection conn = this.getConnection();
		String dbServerName =  conn.getMetaData().getDatabaseProductName();
		String dbServerVersion = conn.getMetaData().getDatabaseProductVersion();
		String dbServerURL = conn.getMetaData().getURL();
		this.setFormData("dbServerName", dbServerName);
		this.setFormData("dbServerVersion", dbServerVersion);
		this.setFormData("dbServerURL", dbServerURL);
	}
}
