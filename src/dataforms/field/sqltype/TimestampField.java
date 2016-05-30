package dataforms.field.sqltype;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationError;
import dataforms.dao.sqldatatype.SqlTimestamp;
import dataforms.field.base.DateTimeField;
import dataforms.util.MessagesUtil;
import dataforms.util.StringUtil;
import dataforms.validator.TimestampValidator;

/**
 * 日時フィールドクラス。
 *
 */
public class TimestampField extends DateTimeField<Timestamp> implements SqlTimestamp {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(TimestampField.class.getName());


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TimestampField(final String id) {
		super(id);
		this.addValidator(new TimestampValidator());
		this.setDateFormat("format.timestampfield");
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をjava.sql.Timestampに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (StringUtil.isBlank(v)) {
			this.setValue(null);
			return;
		}
		SimpleDateFormat fmt = new SimpleDateFormat(MessagesUtil.getMessage(this.getPage(), this.getDateFormat()));
		try {
			Timestamp t = new Timestamp(fmt.parse((String) v).getTime());
			this.setValue(t);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * java.sql.Timestampを日付時刻フォーマット形式の文字列に変換します。
	 * </pre>
	 */
	@Override
	public String getClientValue() {
		if (this.getValue() == null) {
			return null;
		}
		SimpleDateFormat fmt = new SimpleDateFormat(MessagesUtil.getMessage(this.getPage(), this.getDateFormat()));
		return fmt.format(this.getValue());
	}
}
