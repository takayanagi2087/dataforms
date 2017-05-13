package dataforms.devtool.page.db;

import java.util.Map;

import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.page.base.DeveloperPage;

// TODO:初期化時作成するユーザをdeveloperとadminを切り替えられる設定を追加する。

/**
 * データベース初期化ページクラス。
 * <pre>
 * 初期状態のデータベースに接続した状態でアクセスすると、このページが表示されます。
 * 動作するため必要な最小限のテーブルを作成し開発者ユーザを登録するためのフォームです。
 * </pre>
 */
public class InitializeDatabasePage extends DeveloperPage {
	/**
	 * コンストラクタ.
	 */
	public InitializeDatabasePage() {
		this.addForm(new DatabaseInfoForm());
		this.addForm(new DeveloperEditForm());
		this.setMenuItem(false);
	}

	/**
	 * {@inheritDoc}
	 * DBが初期化されている場合は、developerの場合のみ表示されます。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		if (this.getConnection() != null) {
			TableManagerDao dao = new TableManagerDao(this);
			if (dao.isDatabaseInitialized()) {
				//return super.isAuthenticated(params);
				return false;
			} else {
				return true;
			}
		} else {
			// DB未接続時は判定できないのでtrueを返しておかないと動作しない。
			return true;
		}
	}
}
