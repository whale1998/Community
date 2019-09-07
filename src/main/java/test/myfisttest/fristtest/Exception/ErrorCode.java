package test.myfisttest.fristtest.Exception;
/*枚举类异常
* 属性、获取属性、构造方法
* 枚举异常*/
public enum ErrorCode implements IErrorCode{
    QUESTION_NOT_FOUND(2001,"问题不存在辣，换一个试试~！"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中问题！"),
    NO_LOGIN(2003,"用户未登录！"),
    SYS_ERROR(2004,"服务器冒烟辣！T-T"),
    COMMENT_NOT_FOUND(2005,"评论不存在！T-T"),
    COMMENT_PARAM_NOT_FOUND(2006,"该评论找不到了~T-T"),
    COMMENT_IS_EMPTY(2007,"评论不能为空T-T~"),
    READ_NOTIFICATION_FAIL(2008,"非法操作！"),
    NOTIFICATION_NOT_FOUND(2009,"新消息不存在T-T~"),
    FILE_UPLOAD_FAIL(2010,"文件上传失败T-T~")
    ;

    public Integer code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getcode() {
        return code;
    }


    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
