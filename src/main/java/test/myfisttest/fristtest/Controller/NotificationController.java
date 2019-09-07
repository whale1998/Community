package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.DTO.NotificationDTO;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Enums.NotificationStatus;
import test.myfisttest.fristtest.Enums.NotificationType;
import test.myfisttest.fristtest.Mapper.NotificationMapper;
import test.myfisttest.fristtest.Service.NotificationService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/notification/{id}")
    public String notification(@PathVariable("id") Long id,
                               HttpServletRequest request) {

        //检验是否登陆
        User user = null;
        user = (User) request.getSession().getAttribute("user");
//        若不为空则放回错误信息
        if (user == null) {
            return "redirect:/index";
        }
        NotificationDTO notificationDTO=notificationService.read(id,user);
        if(NotificationType.REPLY_COMMENT.getType()==notificationDTO.getType()
                    ||NotificationType.REPLY_QUESTION.getType()==notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else{
            return "redirect:/index";
        }
    }
}