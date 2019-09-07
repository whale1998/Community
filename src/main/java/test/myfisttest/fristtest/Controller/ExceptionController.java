package test.myfisttest.fristtest.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import test.myfisttest.fristtest.Exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
/*自定义异常处理类，通用处理异常类
* 若定义了，则BasiseController不工作*/
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ExceptionController implements ErrorController {

    @Override
    public String getErrorPath() {
//        返回error页面
        return "error";
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
//        获取到status
        HttpStatus status = getStatus(request);

        if (status.is4xxClientError()) {
            model.addAttribute("message", ErrorCode.QUESTION_NOT_FOUND);
        }
        if (status.is5xxServerError()) {
            model.addAttribute("message", ErrorCode.SYS_ERROR);
        }

        return new ModelAndView("error");
    }

//    拿到status的方法
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}