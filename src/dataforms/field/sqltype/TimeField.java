package dataforms.field.sqltype;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationError;
import dataforms.dao.sqldatatype.SqlTime;
import dataforms.field.base.DateTimeField;
import dataforms.util.MessagesUtil;
import dataforms.util.StringUtil;
import dataforms.validator.TimeValidator;

/**
 * 時間フィールドクラス。
 *
 */
public class TimeField extends DateTimeField<Time> implements SqlTime {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(TimeField.class.getName());


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TimeField(final String id) {
		super(id);
		this.addValidator(new TimeValidator());
		this.setDateFormat("format.timefield");
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 時刻フォーマットで入力された文字列を、java.sql.Timeに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (StringUtil.isBlank(v)) {
			this.setValue(null);
			return;
		}
		String dfmt = MessagesUtil.getMessage(this.getPage(), this.getDateFormat());
		SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
		try {
			Time t = new Time(fmt.parse((String) v).getTime());
			this.setValue(t);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * java.sql.Timeを時刻フォーマットの文字列に変換します。
	 * </pre>
	 */
	@Override
	public Object getClientValue() {
		if (this.getValue() != null) {
			String dfmt = MessagesUtil.getMessage(this.getPage(), this.getDateFormat());
			SimpleDateFormat fmt = new SimpleDateFormat(dfmt);
			return fmt.format(this.getValue());
		} else {
			return this.getValue();
		}
	}

}
