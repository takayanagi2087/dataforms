/**
 * @fileOverview {@link QueryExecutorQueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class QueryExecutorQueryResultForm
 *
 * @extends QueryResultForm
 */
QueryExecutorQueryResultForm = createSubclass("QueryExecutorQueryResultForm", {}, "QueryResultForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
QueryExecutorQueryResultForm.prototype.attach = function() {
	this.headerHtml = null;
	QueryResultForm.prototype.attach.call(this);
};

QueryExecutorQueryResultForm.prototype.setQueryResult = function(queryResult) {
	logger.log("headerHtml=" + queryResult.headerHtml);
	logger.log("dataHtml=" + queryResult.dataHtml);
	logger.log("htmlTable=" + queryResult.htmlTable);
	var table = this.getComponent("queryResult");
	table.fields = [];
	table.initField(queryResult.htmlTable.fieldList);
	table.trLine = queryResult.dataHtml;
	if (this.headerHtml != queryResult.headerHtml) {
		logger.log("updateHeader");
		this.find("#queryResult thead tr").html(queryResult.headerHtml);
		table.setColumnSortEvent();
		this.headerHtml = queryResult.headerHtml;
	}
	QueryResultForm.prototype.setQueryResult.call(this, queryResult);
};