package dataforms.app.page.sitemap;

import dataforms.app.page.base.BasePage;

/**
 * サイトマップページクラス。
 *
 */
public class SiteMapPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public SiteMapPage() {
		this.addForm(new SiteMapForm());
	}

	/**
	 * デフォルトのhtml,jsパスを取得します。
	 * <pre>
	 * サイトマップページの場合アプリケーションによってデザインを変更できるように
	 * フレームフォルダを参照するように変更します。
	 * </pre>
	 * @param cls パスを取得するクラス.
	 * @return デフォルトのhtml,jsパス.
	 */
	@Override
	public String getWebResourcePath(final Class<?> cls) {
		if (cls.getName().equals(this.getClass().getName())) {
			return this.getPage().getPageFramePath().substring(1) + "/" + cls.getSimpleName();
		} else {
			return super.getWebResourcePath(cls);
		}
	}
}
