/**
 * @fileOverview {@link EnumManagementEditForm}クラスを記述したファイルです。
 */

/**
 * @class EnumManagementEditForm
 *
 * @extends EditForm
 */
EnumManagementEditForm = createSubclass("EnumManagementEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumManagementEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
};

