package chrust.emploeye;

/**
 * Created by Chrustkiran on 24/02/2018.
 */

public class Permission {
    String subject;
    String body;
    String photo;
    String date;

    public Permission(String subject, String body, String photo,String date) {
        this.subject = subject;
        this.body = body;
        this.photo = photo;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
