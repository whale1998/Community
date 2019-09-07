package test.myfisttest.fristtest.Advice;

import com.alibaba.fastjson.JSON;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import test.myfisttest.fristtest.DTO.ResultDTO;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static test.myfisttest.fristtest.DTO.ResultDTO.errorof;

/*异常类只能处理Error抛出的异常，不能处理其他的异常
因此需要一个通用的异常处理类ExceptionController*/
@ControllerAdvice
public class ErrorHandler {
    //处理异常类型
    @ExceptionHandler(Exception.class)
    //返回页面的类型 这里用的modelandview
    ModelAndView  handle(HttpServletRequest request,
                  Throwable e,
                  Model model,
                  HttpServletResponse response) {
        String contentType = request.getContentType();
//        区分错误信息放回的是 application/json 类型的错误，还是页面级的错误
//        返回json格式的错误
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
//            成功返回JSON
            if (e instanceof RuntimeError) {
                resultDTO = errorof((RuntimeError) e);
            } else {
                resultDTO = errorof(ErrorCode.SYS_ERROR);
            }
            try {

 /*下面的几种情况会出现 unreachable statement：
（1）在reutrn语句后写语句。
（2）在throw语句后写语句。
（3）break、continue语句之后定义语句。*/

                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ex) {
            }
            return null;
        } else {
//            返回页面级的错误
//        若包含Error异常抛出枚举类
            if (e instanceof RuntimeError) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", ErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
