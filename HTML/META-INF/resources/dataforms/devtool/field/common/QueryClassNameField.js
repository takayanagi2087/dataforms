/**
 * @fileOverview {@link QueryClassNameField}クラスを記述したファイルです。
 */

/**
 * @class QueryClassNameField
 *
 * @extends SimpleClassNameField
 */
QueryClassNameField = createSubclass("QueryClassNameField", {}, "SimpleClassNameField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
QueryClassNameField.prototype.attach = function() {
	SimpleClassNameField.prototype.attach.call(this);
};

QueryClassNameField.prototype.onUpdateRelationField = function() {
	SimpleClassNameField.prototype.onUpdateRelationField.call(this);
	if (this.get().val().length != 0) {
		var form = this.getParentForm();
		if (typeof form.getSql == "function") {
			form.getSql();
		}
	}
};