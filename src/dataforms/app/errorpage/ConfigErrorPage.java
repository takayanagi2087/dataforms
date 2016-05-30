package dataforms.app.errorpage;

import java.util.Map;

import dataforms.annotation.WebMethod;
import dataforms.controller.JsonResponse;
import dataforms.controller.Page;
import dataforms.controller.Response;

/**
 * 設定エラーページクラス。
 * <pre>
 * web.xmlやアプリケーションサーバ設定の不備に関する表示を行います。
 * </pre>
 */
public class ConfigErrorPage extends Page {

	/**
	 * デフォルトコンストラクタ。
	 * <pre>
	 * メニューの表示確認処理のため必要です。
	 * </pre>
	 */
	public ConfigErrorPage() {
		this.setMenuItem(false);
		this.setNoFrame(true);
	}


	/**
	 * コンストラクタ。
	 * @param msgkey エラーメッセージのキー。
	 */
	public ConfigErrorPage(final String msgkey) {
		// メニューには表示しないページ
		//this.addPreloadJs(this.getClassScriptPath(ConfigErrorPage.class));
		this.setMenuItem(false);
		this.setNoFrame(true);
		this.addForm(new ConfigErrorForm(msgkey));
	}

	/**
	 * {@inheritDoc}
	 * DB接続を行わないように変更します。
	 */
	@WebMethod(useDB = false)
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		return super.getHtml(params);
	}

	/**
	 * {@inheritDoc}
	 * DB接続を行わないように変更します。
	 */
	@WebMethod(useDB = false)
	@Override
	public JsonResponse getPageInfo(final Map<String, Object> params) throws Exception {
		return super.getPageInfo(params);
	}

	/**
	 * {@inheritDoc}
	 * 常にtrueを返します。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> param) throws Exception {
		return true; // 常にOK
	}
}
