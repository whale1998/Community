package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.Cache.HotTagCache;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Service.QuestionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserMapper usermapper;

    @Autowired
    private QuestionService qusetionservice;
    @Autowired
    HotTagCache hotTagCache;

    @GetMapping("/login")
    public  String login(){
        return "login";
    }

    @GetMapping("/")
    public String toindex(){
        return "indexpage";
    }

    @GetMapping("/index")//表示用户进入index页面
    public String index(HttpServletRequest request,
                        Model model,
                        //从前端去获取一个pege页数和展示size条数据
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size){
//      将设置好的 page和size传回前端
        PaginationDTO list = qusetionservice.list(page,size);
        List<String> hots = hotTagCache.getHots();
        model.addAttribute("tags",hots);
        model.addAttribute( "pagination",list);
        return "firstPage";
    }
}
