/**
 * @fileOverview {@link AudioField}クラスを記述したファイルです。
 */

/**
 * @class AudioField
 * 音声ファイルアップロードフィールドクラス。
 * @extends StreamingField
 */
AudioField = createSubclass("AudioField", {}, "StreamingField");


/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
 * </pre>
 */
AudioField.prototype.attach = function() {
	StreamingField.prototype.attach.call(this);
};


