package test.myfisttest.fristtest.Exception;
/*异常类要继承RuntimeException
* 属性、获取属性、构造
* 有一个可以获取枚举类的构造方法*/
public class RuntimeError extends RuntimeException{
    private String message;
    private Integer code;

    public RuntimeError(ErrorCode errorCode) {
        this.code=errorCode.getcode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
