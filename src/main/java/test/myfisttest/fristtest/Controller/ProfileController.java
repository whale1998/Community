package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Service.NotificationService;
import test.myfisttest.fristtest.Service.QuestionService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService qusetionservice;
    @Autowired
    private NotificationService notificationService;



    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name="action") String action,
                          HttpServletRequest request,
                          Model model,
                          @RequestParam(name = "page",defaultValue = "1") Integer page,
                          @RequestParam(name = "size",defaultValue = "5") Integer size){

        //检验是否登陆
        User user=null;
        user = (User) request.getSession().getAttribute("user");
//        若不为空则放回错误信息
        if(user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

//        判断用户是否为空
        if(user == null){
            return "redirect:/index";
        }

//      点击列表映射到标题
        if(action.equals("questions")){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            //        展示用户自己的问题
            PaginationDTO list = qusetionservice.listbyid(user.getId(),page,size);
            model.addAttribute("pagination",list);
        }else if(action.equals("replies")){
            PaginationDTO lists= notificationService.list(user.getId(),page,size);
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
            model.addAttribute("pagination",lists);
        }
        return "profile";
    }


}
