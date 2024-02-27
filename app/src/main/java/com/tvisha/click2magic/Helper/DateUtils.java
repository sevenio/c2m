package com.tvisha.click2magic.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    private static final String REQUEST_YEAR_MONTH = "yyyy-MM";
    public static final String REQUEST_DATE = "yyyy-MM-dd";
    public static final String REQUEST_DATE_ = "yyyy-M-dd";
    public static final String REQUEST_DATE_NEW = "dd MMMM yyyy";
    public static final String REQUEST_DATE_SORT_NEW = "dd MMM yyyy";
    public static final String REQUEST_DATE_SORT_NEW1 = "EEE, dd MMM, yyyy";
    public static final String REQUEST_DATE_SORT = "dd MMMM yyyy";
    public static final String EVENT_DATE = "EEE, dd MMM";
    public static final String SINGLE_EVENT_DATE = "EEE, dd MMM yyyy hh:mm a";
    public static final String BOOK_SUCCESS_DATE = "MMMM, yyyy, dd";
    public static final String NORMAL_REQUEST_DATE = "dd/MM/yyyy";
    public static final String REQUEST_DATE_TIME = "dd-MM-yyyy hh:mm a";
    private static final String REQUEST_TIME = "HH:mm";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_WITH_OUT_SEC = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_WITH_OUT_SEC_12 = "yyyy-MM-dd HH:mm:ss";
    public static final String START_DATE = "dd MMM yyyy K:mm a";
    public static final String START_TIME = "K:mm a";
    public static final String DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private Date date;

    /**
     * Constructs a AppDate with the specified date time format and
     * dateTime value.
     * A format is a String that describes date format.
     * And dateTime is a String that time value.
     *
     * @param format   the date format
     * @param dateTime the date value string
     */
    public DateUtils(String format, String dateTime) {
        try {
            parse(format, dateTime, TimeZone.getDefault());
        } catch (DateFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a AppDate with the specified date time format and
     * dateTime value.
     * A format is a String that describes date format.
     * And dateTime is a String that time value.
     *
     * @param format   the date format
     * @param dateTime the date value string
     */
    public DateUtils(String format, String dateTime, TimeZone timeZone) {
        try {
            parse(format, dateTime, timeZone);
        } catch (DateFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * parse date with format
     *
     * @param format the date format
     * @param date   the date value string
     */
    private void parse(String format, String date, TimeZone timeZone) throws DateFormatException {
        SimpleDateFormat sdfFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            sdfFormat.setTimeZone(timeZone);
            this.date = sdfFormat.parse(date);
        } catch (ParseException e) {
            String message = date + " format is not valid for " + format;
            throw new DateFormatException(message);
        }
    }

    /**
     * Constructs a AppDate with Date
     *
     * @param date Date type
     */
    public DateUtils(Date date) {
        this.date = date;
    }


    /**
     * Change time zone
     */
    public void setTimeZone(TimeZone timeZone) {
        if (timeZone == null)
            return;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        String newTime = sdf.format(date);

        try {
            parse(DATE_TIME, newTime, timeZone);
        }catch (DateFormatException e){
            e.printStackTrace();
        }
    }

    /**
     * Compare instance date is less then to otherDate
     *
     * @param toDate for comparision
     * @return true if less then
     */
    public boolean isLessThan(Date toDate) {
        Date otherDate = toDate;
        if (date == null || otherDate == null)
            return false;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_WITH_OUT_SEC, Locale.ENGLISH);
        try {
            date = sdf.parse(sdf.format(date));
            otherDate = sdf.parse(sdf.format(otherDate));

            return date.before(otherDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * get time
     */
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get time with out am/pm
     */
    public String getTimeWithoutMeridian() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get day
     */
    public String getCalenderDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get month
     */
    public String getCalenderMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get Week day
     */
    public String getCalenderWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get Week day sort
     */
    public String getCalenderWeekSmall() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get year
     */
    public String getCalenderYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get calender date
     */
    public String getCalenderDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public String getRevCalenderDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * get month and year
     */
    public String getMonthAndYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Get Time zone name
     *
     * @return formatted timezone name
     */
    public String getTimezoneName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeZone().getDisplayName(false, TimeZone.LONG);
    }

    /**
     * get file name
     */
    public String getFileNameDateFormat() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH).format(date);
    }

    /**
     * Get Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * get request date
     */
    public String getRequestDate(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(REQUEST_DATE, Locale.ENGLISH);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    /**
     * get request time
     */
    public String getRequestTime(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(REQUEST_TIME, Locale.ENGLISH);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    /**
     * get Request date and time
     */
    public String getRequestDateTime(TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME, Locale.ENGLISH);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }


    /**
     * Get custom data time format with new timezone
     *
     * @param itemFormat Date format
     * @param timeZone for new TimeZone
     * @return converted date
     */
    public String getCustomFormatWithTimeZone(String itemFormat, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(itemFormat, Locale.ENGLISH);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    /**
     * get year and month
     */
    public String getYearMonth() {
        return new SimpleDateFormat(REQUEST_YEAR_MONTH, Locale.ENGLISH).format(date);
    }

    /**
     * @return complete Time in Relative Time span in String format
     */
    public String getTimeSpan() {
        long now = System.currentTimeMillis();
        return android.text.format.DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                now,
                android.text.format.DateUtils.SECOND_IN_MILLIS).toString();
    }

    /**
     * get require data from date
     */
    public String getCustomFormat(String itemFormat) {
        return new SimpleDateFormat(itemFormat, Locale.ENGLISH).format(date);
    }

    /**
    * Compare two date
    */
    public static boolean compareDates(String format, Date date1, Date date2) {
        if (format == null || date1 == null || date2 == null)
            return false;

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            date1 = sdf.parse(sdf.format(date1));
            date2 = sdf.parse(sdf.format(date2));

            return date1.compareTo(date2) == 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }



    public class DateFormatException extends Exception {

        /**
         * Constructs a AppDateFormatException with the specified detail message.
         * A detail message is a String that describes this particular exception.
         *
         * @param message the detail message text
         */

        DateFormatException(String message) {
            super(message);
        }
    }


}