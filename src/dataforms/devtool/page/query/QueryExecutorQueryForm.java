package dataforms.devtool.page.query;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.JsonResponse;
import dataforms.controller.QueryForm;
import dataforms.controller.Response;
import dataforms.dao.Dao;
import dataforms.dao.Query;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.field.sqltype.ClobField;
import dataforms.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class QueryExecutorQueryForm extends QueryForm {
	
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(QueryExecutorQueryForm.class);
	
	/**
	 * コンストラクタ。
	 */
	public QueryExecutorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField());
		this.addField(new QueryClassNameField("queryClassName")).setAutocomplete(true).setRelationDataAcquisition(true);
		this.addField(new ClobField("sql")).addValidator(new RequiredValidator());
	}
	
	/**
	 * 指定された問合せクラスのインスタンスを作成します。
	 * @param param POSTされたパラメータ。
	 * @return 問合せクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private Query newQuery(final Map<String, Object> param) throws Exception {
		String packageName = (String) param.get("packageName");
		String queryClassName = (String) param.get("queryClassName");
		log.debug("queryClass=" + packageName + "." + queryClassName);
		@SuppressWarnings("unchecked")
		Class<? extends Query> q = (Class<? extends Query>) Class.forName(packageName + "." + queryClassName);
		return q.newInstance();
	}
	
	/**
	 * 指定された問合せクラスに対応したsqlを取得します。
	 * @param param パラメータ。
	 * @return SQLの応答。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response getSql(final Map<String, Object> param) throws Exception {
		this.methodStartLog(log, param);
		Query query = this.newQuery(param);
		Dao dao = new Dao(this);
		SqlGenerator gen = dao.getSqlGenerator();
		String sql = gen.generateQuerySql(query);
		Response resp = new JsonResponse(JsonResponse.SUCCESS, sql);
		
		this.methodStartLog(log, param);
		return resp;
	}
}
