package test.myfisttest.fristtest.Enums;

public enum CommentType {
    QUESTION(1),
    COMMENT(2);

    private Integer code;

//    判断是否存在评论类型
    public static boolean isexit(Integer type) {
        for (CommentType value : CommentType.values()) {
            if (value.getCode()==type){
                return true;
            }
        }
        return false;
    }

    public Integer getCode() {
        return code;
    }

    CommentType(Integer code) {
        this.code = code;
    }
}
