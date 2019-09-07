package test.myfisttest.fristtest.Enums;

public enum NotificationType {
    REPLY_COMMENT(2, "回复了评论"),
    REPLY_QUESTION(1, "回复了问题");
    private String name;
    private Integer type;

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    NotificationType(Integer type, String name) {
        this.name = name;
        this.type = type;
    }

    public static String nameof(int type) {
        for (NotificationType value : NotificationType.values()) {
            if (type == value.getType()) {
                return value.getName();
            }
        }
        return "";
    }
}
