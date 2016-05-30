/**
 * @fileOverview {@link TimeValidator}クラスを記述したファイルです。
 */

/**
 * @class TimeValidator
 * 時刻バリデータ。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
TimeValidator = createSubclass("TimeValidator", {dateFormatKey: "format.timefield"}, "DateTimeValidator");
