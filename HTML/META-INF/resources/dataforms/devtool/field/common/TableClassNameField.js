/**
 * @fileOverview {@link TableClassNameField}クラスを記述したファイルです。
 */

/**
 * @class TableClassNameField
 *
 * @extends SimpleClassNameField
 */
TableClassNameField = createSubclass("TableClassNameField", {}, "SimpleClassNameField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
TableClassNameField.prototype.attach = function() {
	SimpleClassNameField.prototype.attach.call(this);
};

TableClassNameField.prototype.onUpdateRelationField = function() {
	SimpleClassNameField.prototype.onUpdateRelationField.call(this);
	if (this.get().val().length != 0) {
		var form = this.getParentForm();
		if (typeof form.getFieldList == "function") {
			form.getFieldList();
		}
	}
};