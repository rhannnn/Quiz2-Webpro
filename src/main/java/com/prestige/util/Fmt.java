package com.prestige.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Fmt {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private Fmt() {
    }

    public static String h(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        StringBuilder out = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    public static String js(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        StringBuilder out = new StringBuilder(text.length() + 8);
        out.append('\'');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' || c == '\'' || c == '"' || c == '<' || c == '>' || c == '&' || c < 0x20
                    || c == '\u2028' || c == '\u2029') {
                out.append(String.format("\\u%04x", (int) c));
            } else {
                out.append(c);
            }
        }
        out.append('\'');
        return out.toString();
    }

    public static String idr(Object value) {
        if (value == null) {
            return "0";
        }
        BigDecimal number;
        if (value instanceof BigDecimal decimal) {
            number = decimal;
        } else if (value instanceof Number n) {
            number = BigDecimal.valueOf(n.doubleValue());
        } else {
            return h(value);
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat("#,##0", symbols).format(number);
    }

    public static String date(Object value) {
        if (value instanceof LocalDate d) {
            return DISPLAY_DATE.format(d);
        }
        return value == null ? "" : String.valueOf(value);
    }

    public static String repeat(String text, int times) {
        if (text == null || times <= 0) {
            return "";
        }
        return text.repeat(times);
    }

    public static String today() {
        return LocalDate.now().toString();
    }

    public static int year() {
        return LocalDate.now().getYear();
    }
}
