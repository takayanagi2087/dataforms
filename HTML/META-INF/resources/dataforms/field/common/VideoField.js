/**
 * @fileOverview {@link VideoField}クラスを記述したファイルです。
 */

/**
 * @class VideoField
 * 動画ファイルアップロードフィールドクラス。
 * @extends FileField
 */
VideoField = createSubclass("VideoField", {parts: "VideoField.html"}, "StreamingField");


/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
 * </pre>
 */
VideoField.prototype.attach = function() {
	StreamingField.prototype.attach.call(this);
	var player = this.getPlayer();
	player.attr("width", this.playerWidth);
	player.attr("height", this.playerlHeight);
};


