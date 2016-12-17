/**
 * @fileOverview {@link StreamingField}クラスを記述したファイルです。
 */

/**
 * @class StreamingField
 * ストリーミングファイルアップロードフィールドクラス。
 * @extends FileField
 */
StreamingField = createSubclass("StreamingField", {}, "FileField");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
 * </pre>
 */
StreamingField.prototype.attach = function() {
	FileField.prototype.attach.call(this);
	var thisField = this;
	var linkid = this.id + "_link";
	var link = this.parent.find("#" + this.selectorEscape(linkid));
	var player = this.getPlayer();
	this.get().change(function(){
		for(var j=0; j<this.files.length; j++){
			var url = URL.createObjectURL(this.files[j])
			logger.log("url=" + url);
			player.attr("src", url);
		}
	});
	player.on("abort", function() {
		logger.log("abort");
		setTimeout(function() {
			thisField.deleteTempFile();
		}, 3000);
	});
	player.on("ended", function() {
		logger.log("ended");
		setTimeout(function() {
			thisField.deleteTempFile();
		}, 3000);
	});
};

/**
 * ファイルフィールドに付随する各種コンポーネントを配置します。
 * @param comp ファイルフィールド。
 */
StreamingField.prototype.addElements = function(comp) {
	var getParts = new SyncServerMethod("getParts");
	var htmlstr = getParts.execute("parts=" + escape(currentPage.framePath + "/" + this.parts));
	if (htmlstr.status == ServerMethod.SUCCESS) {
		var html = htmlstr.result.replace(/\$\{fieldId\}/g, this.id);
		logger.log("htmlstr=" + html);
		var tag = comp.prop("tagName");
		var type = comp.prop("type");
		if ("INPUT" == tag && type == "file") {
			comp.after(html);
		} else if (tag == "DIV") {
			comp.html(html);
			this.parent.find("#" + this.selectorEscape(this.id + "_ck")).hide();
			this.parent.find("label[for='" + this.selectorEscape(this.id + "_ck") + "']").hide();
		}
	}
};



/**
 * ストリーミングデータのプレーヤーを取得します。
 * @returns {jQuery} ストリーミングデータのプレーヤー。
 */
StreamingField.prototype.getPlayer = function() {
	var playerid = this.id + "_player"; // プレーヤー.
	var player = this.parent.find("#" + this.selectorEscape(playerid));
	return player;
};

/**
 * サーバ中のストリーミングデータの一時ファイルを削除します。
 */
StreamingField.prototype.deleteTempFile = function() {
	var playerid = this.id + "_player"; // プレーヤーID.
	var player = this.parent.find("#" + this.selectorEscape(playerid));
	var key = player.attr("data-key");
	logger.log("key=" + key);
	var m = this.getSyncServerMethod("deleteTempFile");
	m.execute(key);
};

/**
 * 値を設定します。
 *
 * @param {Object} value 値。
 */
StreamingField.prototype.setValue = function(value) {
	FileField.prototype.setValue.call(this, value);
	var videoid = this.id + "_player"; // プレーヤーID.
	var video = this.parent.find("#" + this.selectorEscape(videoid));
	if (value != null) {
		this.downloadParameter = value.downloadParameter;
		var url = location.pathname + "?dfMethod=" + this.getUniqId() + ".download"  + "&" + value.downloadParameter;
		video.attr("src", url);
		video.attr("data-key", value.downloadParameter);
	} else {
		video.attr("src", null);
	}

};
