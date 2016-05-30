package dataforms.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.validator.ValidationError;

/**
 * 問い合わせフォームクラスです。
 * <pre>
 * 問い合わせの条件を入力するフォームです。
 *
 * </pre>
 *
 */
public abstract class QueryForm extends Form {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(QueryForm.class.getName());


	/**
	 * コンストラクタ。
	 */
	public QueryForm() {
		super(Page.ID_QUERY_FORM);
	}

	/**
	 * 問い合わせの開始処理を行います。
	 * <pre>
	 * 問い合わせの条件をチェックし、その結果を返すのみの処理になっています。
	 * 条件が正常な場合、Javascript側の処理でQueryResultFormに検索条件を渡します。
	 * </pre>
	 * @param param パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
    @WebMethod
	public JsonResponse query(final Map<String, Object> param) throws Exception {
    	this.methodStartLog(log, param);
    	List<ValidationError> err = this.validate(param);
    	JsonResponse result = null;
    	if (err.size() > 0) {
    		result = new JsonResponse(JsonResponse.INVALID, err);
    	} else {
        	result = new JsonResponse(JsonResponse.SUCCESS, "");
    	}
    	this.methodFinishLog(log, result);
    	return result;
    }
}
