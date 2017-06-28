package org.hsweb.ezorm.rdb.meta.converter;

import org.hsweb.commons.DateTimeUtils;
import org.hsweb.commons.time.DateFormatter;
import org.hsweb.ezorm.core.ValueConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 日期转换器
 */
public class DateTimeConverter implements ValueConverter {

    private String format;

    private Class toType;

    public DateTimeConverter(String format, Class toType) {
        this.format = format;
        this.toType = toType;
    }

    @Override
    public Object getData(Object value) {
        if (value instanceof Date) return value;
        if (value instanceof Number) {
            // 是20160112格式,而不是时间戳
            if (((Number) value).longValue() < 29999999) {
                value = value.toString();
            } else {
                return new Date(((Number) value).longValue());
            }
        }
        if (value instanceof String) {
            if (((String) value).contains(",")) {
                return Arrays.stream(((String) value).split(","))
                        .map(DateFormatter::fromString)
                        .collect(Collectors.toList());
            }
            return DateFormatter.fromString(((String) value));
        }
        return value;
    }

    @Override
    public Object getValue(Object data) {
        if (data instanceof Number) {
            data = new Date(((Number) data).longValue());
        }
        if (data instanceof Date) {
            if (toType == Date.class) return data;
            if (toType == String.class) {
                return DateTimeUtils.format(((Date) data), format);
            }
        }
        if (data instanceof String) {
            if (toType == Date.class) {
                if (((String) data).contains(",")) {
                    return Arrays.stream(((String) data).split(","))
                            .map(DateFormatter::fromString)
                            .collect(Collectors.toList());
                }
                data = DateFormatter.fromString(((String) data));
                if (data == null) data = DateTimeUtils.formatDateString(((String) data), format);
            }
        }
        return data;
    }
}
