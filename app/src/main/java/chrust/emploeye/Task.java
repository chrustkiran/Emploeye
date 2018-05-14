package chrust.emploeye;

/**
 * Created by Chrustkiran on 23/02/2018.
 */

public class Task {
    String date;
    String name;
    String lat;
    String lng;
    String start_time;
    String end_time;
    String state;

    public Task(){}

    public Task(String date, String name, String lat, String lng, String start_time, String end_time,String state) {
        this.date = date;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.start_time = start_time;
        this.end_time = end_time;
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getState() {
        return state;
    }
}
