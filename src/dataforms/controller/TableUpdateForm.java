package dataforms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.util.MessagesUtil;
import dataforms.validator.ValidationError;

/**
 * データベーステーブル更新フォーム。
 * 
 * <pre>
 * データベーステーブルを更新するためのフォームのベースクラスです。
 * </pre>
 */
public abstract class TableUpdateForm extends Form {
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(TableUpdateForm.class);

	/**
	 * PKフィールドリスト。
	 */
	private List<String> pkFieldIdList = null;
	
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableUpdateForm(final String id) {
		super(id);
	}
	
	/**
	 * 主キーフィールドリストを取得します。
	 * @return 主キーフィールドリスト。
	 */
	public List<String> getPkFieldIdList() {
		return pkFieldIdList;
	}


	/**
	 * 主キーフィールドリストを設定します。
	 * @param pkFieldList 主キーフィールドリスト。
	 */
	public void setPkFieldIdList(final FieldList pkFieldList) {
		this.pkFieldIdList = new ArrayList<String>();
		for (Field<?> f: pkFieldList) {
			this.pkFieldIdList.add(f.getId());
		}
	}


	/**
	 * 主キーフィールドリストを設定します。
	 * @param list 主キーIDリスト。
	 */
	public void setPkFieldIdList(final List<String> list) {
		this.pkFieldIdList = list;
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		if (this.pkFieldIdList != null) {
			ret.put("pkFieldIdList", this.pkFieldIdList);
		}
		return ret;
	}
	
	/**
	 * 保存時のメッセージキーを取得します。
	 * <pre>
	 * "message.saved"を返します。
	 * 保存時のメッセージを指定する場合、オーバーライドします。
	 * </pre>
	 * @param data 保存したデータ。
	 * @return 保存時のメッセージ。
	 */
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.saved");
	}

	/**
	 * 編集対象のデータを取得します。
	 * @param data 編集対象を特定するためのデータ。
	 * @return 問い合わせ結果。
	 * @throws Exception 例外。
	 */
	protected abstract Map<String, Object> queryData(final Map<String, Object> data) throws Exception;

	/**
	 * 選択されたデータを取得します。
	 *
	 * @param param パラメータ。
	 * @return 取得したデータ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getData(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		Map<String, Object> data = this.convertToServerData(param);
		JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, this.convertToClientData(this.queryData(data)));
		this.methodFinishLog(logger, result);
		return result;
	}
	
	/**
	 * 編集フォームの確認処理を行います。
	 * <pre>
	 * バリデーションを行いその結果を返します。
	 * </pre>
	 * @param param パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse confirm(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		List<ValidationError> err = this.validate(param);
		JsonResponse result = null;
		if (err.size() > 0) {
			result = new JsonResponse(JsonResponse.INVALID, err);
		} else {
			result = new JsonResponse(JsonResponse.SUCCESS, "");
		}
		this.methodFinishLog(logger, result);
		return result;
	}

	/**
	 * 登録ボタンの処理を行います。
	 *
	 * <pre>
	 * データ挿入、更新、削除を行います。
	 * </pre>
	 *
	 * @param param パラメータ。
	 * @return 登録結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public abstract JsonResponse save(final Map<String, Object> param) throws Exception;

}
