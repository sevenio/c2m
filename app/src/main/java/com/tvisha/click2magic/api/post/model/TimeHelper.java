package com.tvisha.click2magic.api.post.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by koti on 26/5/17.
 */

public class TimeHelper {
    public static final String MONTHS[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    public static final String DAYS[] = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private static final TimeHelper ourInstance = new TimeHelper();

    public static TimeHelper getInstance() {
        return ourInstance;
    }

    public String currentTime() {
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public String getTimeStamp() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public String getTimeInterval(String time) {
        Date date = null;
        Date current_date = null;
        String retun_string = null;
        try {
            if (time != null) {
                SimpleDateFormat format = dateFormat;
                current_date = format.parse(getInstance().currentTime());
                //date = format.parse("2016-06-27 10:10:38");
                date = format.parse(time);
                retun_string = timeDifference(date, current_date);
            } else {
                return "...";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "...";
        }
        return retun_string;
    }

    public String getMsgTimeInterval(String time) {
        Date date = null;
        Date current_date = null;
        String retun_string = null;
        try {
            if (time != null) {
                SimpleDateFormat format = dateFormat;
                current_date = format.parse(getInstance().currentTime());
                //date = format.parse("2016-06-27 10:10:38");
                date = format.parse(time);
                retun_string = msgTimeIntervell(date, current_date);
            } else {
                return "...";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "...";
        }
        return retun_string;
    }

    public static String msgTimeIntervell(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;
        long monthsInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String d_time = "0S";

        if (elapsedMonths > 0) {
            d_time = " " + elapsedMonths + "M";
        } else if (elapsedWeeks > 0) {
            d_time = " " + elapsedWeeks + "W";
        } else if (elapsedDays > 0) {
            d_time = " " + elapsedDays + "D";
        } else if (elapsedHours > 0) {
            d_time = " " + elapsedHours + "h";
        } else if (elapsedMinutes > 0) {
            d_time = " " + elapsedMinutes + "m";
        } else if (elapsedSeconds > 0) {
            d_time = " " + elapsedSeconds + "s";
        }

        return d_time != null ? d_time : "...";

    }

    public static String timeDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;
        long monthsInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String d_time = "0S";

        if (elapsedMonths > 0) {

            d_time = " " + elapsedMonths + "M";

            if (elapsedWeeks > 0) {

                d_time = d_time + " " + elapsedWeeks + "W";

            } else if (elapsedDays > 0) {

                d_time = d_time + " " + elapsedDays + "D";

            } else if (elapsedHours > 0) {

                d_time = d_time + " " + elapsedHours + "h";

            } else if (elapsedMinutes > 0) {
                d_time = d_time + " " + elapsedMinutes + "m";

            } else if (elapsedSeconds > 0) {
                d_time = d_time + " " + elapsedSeconds + "s";

            }

        } else if (elapsedWeeks > 0) {

            d_time = " " + elapsedWeeks + "W";

            if (elapsedDays > 0) {

                d_time = d_time + " " + elapsedDays + "D";

            } else if (elapsedHours > 0) {

                d_time = d_time + " " + elapsedHours + "h";

            } else if (elapsedMinutes > 0) {
                d_time = d_time + " " + elapsedMinutes + "m";

            } else if (elapsedSeconds > 0) {
                d_time = d_time + " " + elapsedSeconds + "s";

            }

        } else if (elapsedDays > 0) {

            d_time = " " + elapsedDays + "D";

            if (elapsedHours > 0) {

                d_time = d_time + " " + elapsedHours + "h";

            } else if (elapsedMinutes > 0) {
                d_time = d_time + " " + elapsedMinutes + "m";

            } else if (elapsedSeconds > 0) {
                d_time = d_time + " " + elapsedSeconds + "s";

            }

        } else if (elapsedHours > 0) {
            d_time = " " + elapsedHours + "h";
            if (elapsedMinutes > 0) {
                d_time = d_time + " " + elapsedMinutes + "m";

            } else if (elapsedSeconds > 0) {
                d_time = d_time + " " + elapsedSeconds + "s";

            }
        } else if (elapsedMinutes > 0) {
            d_time = " " + elapsedMinutes + "m";
            if (elapsedSeconds > 0) {
                d_time = d_time + " " + elapsedSeconds + "s";
            }
        } else if (elapsedSeconds > 0) {
            d_time = " " + elapsedSeconds + "s";

        }

        return d_time != null ? d_time : "...";

    }

    //time
    public String getFeedsTime(String timestamp) {
        try {
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            tdy1.setTime(d1);

            String time = tdy1.get(Calendar.HOUR) + ":" + tdy1.get(Calendar.MINUTE) + " " + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM") + " - " + tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getMessagesTime(String timestamp) {
        try {
            Date d1=null;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat form =null;
            if (timestamp!=null && !timestamp.equals("null") && !timestamp.isEmpty() && timestamp.contains(".")) {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
//                form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSSSSS");
            }
            else if(timestamp!=null && !timestamp.equals("null") && !timestamp.isEmpty() && timestamp.contains("Z")){
                form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            }
            else
            {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            if (timestamp!=null) {
                d1 = form.parse(timestamp);
            }else {
               String date = form.format(calendar.getTime());
                d1 = form.parse(date);
            }
            Calendar postDate = Calendar.getInstance();
            postDate.setTime(d1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String date = dateFormat.format(d1);
            //return date.toUpperCase();

            return (postDate.get(Calendar.HOUR)>0?postDate.get(Calendar.HOUR):12)+  ":" +
                    (postDate.get(Calendar.MINUTE) > 9 ? postDate.get(Calendar.MINUTE) : ("0" + postDate.get(Calendar.MINUTE))) + " "
                    + ((postDate.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessagesDate(String timestamp) {
        try {
            SimpleDateFormat form = null;
            if (timestamp.contains(".")){
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            Calendar sysDate = Calendar.getInstance();
            tdy1.setTime(d1);

            //Date date = form.parse(timestamp);
            String time;
            if (isSameDay(tdy1, sysDate)) {
                time = "Today";
            } else if (isPreviousDay(tdy1,sysDate)) {
                time = "Yesterday";
            }else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR);
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getMessagesDateAdapter(String timestamp) {
        try {
            SimpleDateFormat form =null;
            if (timestamp.contains(".")) {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            Calendar sysDate = Calendar.getInstance();
            tdy1.setTime(d1);

            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            String date_time = _12HourSDF.format(d1);

            //Date date = form.parse(timestamp);
            String time;
            if (isSameDay(tdy1, sysDate)) {
                time = "Today ,"+date_time;
            } else if (isPreviousDay(tdy1,sysDate)) {
                time = "Yesterday ,"+date_time;
            }else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR)+" ,"+date_time;
            }

            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isPreviousDay(Calendar cal1, Calendar cal2)
    {
        cal2.add(Calendar.DATE,-1);
        if (cal1 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR-1) == cal2.get(Calendar.DAY_OF_YEAR-1));
    }

    public Calendar getCalenderFormat(String timestamp){
        try{
            SimpleDateFormat form = null;
            if (timestamp.contains(".")){
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            tdy1.setTime(d1);
            return tdy1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getMessagesDateTime(String timestamp) {
        try {
            SimpleDateFormat form = null;
            if (timestamp.contains(".")){
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }

            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            tdy1.setTime(d1);
            Calendar sysDate = Calendar.getInstance();
            String time;
            if (isSameDay(tdy1, sysDate)) {
                time= (tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                        (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                        + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            } else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR);
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getMessagesInfoDateTime(String timestamp) {
        try {
            SimpleDateFormat form = null;
            if (timestamp.contains(".")){
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }

            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            tdy1.setTime(d1);
            Calendar sysDate = Calendar.getInstance();
            String time;
            if (isSameDay(tdy1, sysDate)) {
                time= (tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                        (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                        + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            } else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR)+" "+(tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                        (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                        + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getMessagesInfoDate(String timestamp) {
        try {
            SimpleDateFormat form = null;
            if (timestamp.contains(".")){
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            }else {
                form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }

            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            tdy1.setTime(d1);
            Calendar sysDate = Calendar.getInstance();
            String time;
            if (isSameDay(tdy1, sysDate)) {
                time= (tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                        (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                        + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            } else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR);
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String formatCopiedMsgTimeStamp(String timestamp) {
        try {
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = form.parse(timestamp);
            Calendar tdy1 = Calendar.getInstance();
            Calendar sysDate = Calendar.getInstance();
            tdy1.setTime(d1);
            String time;
            /*if (isSameDay(tdy1, sysDate)) {
                time= (tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                        (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                        + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            } else {
                time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR);
            }*/
            time = tdy1.get(Calendar.DATE) + " " + MONTHS[tdy1.get(Calendar.MONTH)] + " " + tdy1.get(Calendar.YEAR)+" "+(tdy1.get(Calendar.HOUR)>0?tdy1.get(Calendar.HOUR):12)+":" +
                    (tdy1.get(Calendar.MINUTE) > 9 ? tdy1.get(Calendar.MINUTE) : ("0" + tdy1.get(Calendar.MINUTE))) + " "
                    + ((tdy1.get(Calendar.AM_PM)) == 0 ? "AM" : "PM");
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


