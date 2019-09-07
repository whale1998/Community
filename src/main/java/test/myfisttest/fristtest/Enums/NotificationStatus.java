package test.myfisttest.fristtest.Enums;

public enum  NotificationStatus {
    UNREAD(0),READ(1)
    ;
    private Integer type;


    public Integer getType() {
        return type;
    }



    NotificationStatus(Integer type) {
        this.type = type;
    }
}
