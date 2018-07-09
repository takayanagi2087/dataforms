package dataforms.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.validator.ValidationError;

/**
 * 複数レコード編集フォームクラス。
 * <pre>
 * テーブルの複数レコードを編集するためのFormクラスです。
 * </pre>
 */
public abstract class MultiRecordEditForm extends TableUpdateForm {
	
	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger(MultiRecordEditForm.class);
	
	/**
	 * 配置するEditableHtmlTableのID。
	 */
	public static final String ID_LIST = "list";
	
	
	/**
	 * コンストラクタ。
	 */
	public MultiRecordEditForm() {
		super(DataForms.ID_EDIT_FORM);
	}
	
	/**
	 * 問い合わせフォームに入力された条件から編集対象のデータを取得します。
	 * @param data 検索条件。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 *
	 */
	protected Map<String, Object> queryDataByQueryFormCondition(final Map<String, Object> data) throws Exception {
		throw new ApplicationException(this.getPage(), "error.notimplemetmethod");
	}

	/**
	 * 問い合わせフォームの条件から編集データを取得します。
	 *
	 * @param param パラメータ。
	 * @return 取得したデータ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getDataByQueryFormCondition(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		QueryForm qf = (QueryForm) this.getParent().getComponent(Page.ID_QUERY_FORM);
		Map<String, Object> data = qf.convertToServerData(param);
		JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, this.convertToClientData(this.queryDataByQueryFormCondition(data)));
		this.methodFinishLog(logger, result);
		return result;
	}

	/**
	 * テーブル保存処理。
	 * @param data 保存データ。
	 * @throws Exception 例外。
	 */
	protected abstract void saveTable(final Map<String, Object> data) throws Exception;
	
	/**
	 * 登録ボタンの処理を行います。
	 *
	 * <pre>
	 * データ挿入または更新を行います。
	 * </pre>
	 *
	 * @param param パラメータ。
	 * @return 登録結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	@Override
	public JsonResponse save(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		List<ValidationError> err = this.validate(param);
		JsonResponse result = null;
		if (err.size() > 0) {
			result = new JsonResponse(JsonResponse.INVALID, err);
		} else {
			Map<String, Object> data = this.convertToServerData(param);
			this.saveTable(data);
			result = new JsonResponse(JsonResponse.SUCCESS, this.getSavedMessage(data));
		}
		this.methodFinishLog(logger, result);
		return result;
	}

}
