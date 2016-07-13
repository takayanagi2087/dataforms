/**
 * @fileOverview {@link QueryExecutorQueryForm}クラスを記述したファイルです。
 */

/**
 * @class QueryExecutorQueryForm
 *
 * @extends QueryForm
 */
QueryExecutorQueryForm = createSubclass("QueryExecutorQueryForm", {}, "QueryForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
QueryExecutorQueryForm.prototype.attach = function() {
	QueryForm.prototype.attach.call(this);
};



/**
 * 計算しベント処理。
 * @param {jQuery} f 計算イベントの発生元。 
 */
QueryExecutorQueryForm.prototype.onCalc = function(f) {
	if (f != null) {
		if (f.attr("id") == "queryClassName") {
			this.getSql();
		}
	}
};

/**
 * Queryクラスに対応するSQLを取得します。
 */
QueryExecutorQueryForm.prototype.getSql = function() {
	var thisForm = this;
	this.submit("getSql", function(r) {
		if (r.status == ServerMethod.SUCCESS) {
			thisForm.find("#sql").val(r.result);
		}
	});
};