package test.myfisttest.fristtest.DTO;

import lombok.Data;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Exception.ErrorCode;

@Data
public class  ResultDTO <T> {
//    用于判断评论时候返回结果
//    一个返回信息和一个代码  如200 返回成功
    private Integer code;
    private String message;
    private T date;

//    封装进方法
    public static ResultDTO
    errorof(Integer code,String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorof(ErrorCode errorCode){
        return errorof(errorCode.getcode(),errorCode.getMessage());
    }
    public static ResultDTO errorof(RuntimeError e){
        return errorof(e.getCode(),e.getMessage());
    }

    public static ResultDTO okof(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功！");
        return resultDTO;
    }

    public static <T>ResultDTO  okof(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setDate(t);
        resultDTO.setMessage("请求成功！");
        return resultDTO;
    }
}
