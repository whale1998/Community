package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Service.UserService;
import test.myfisttest.fristtest.Utils.CodeUtil;
import test.myfisttest.fristtest.Utils.MailUtil;

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class RegisterController {


    @Autowired
    private UserMapper usermapper;

    @Autowired
    private UserService userService;

    //普通用户注册
    @PostMapping("/signin")
    public String signin(@RequestParam("email") String email,
                         @RequestParam("name") String name,
                         @RequestParam("password") String password,
                         Model model){

        String pattern = "[1-9]\\d{7,10}@qq\\.com";
        boolean isMatch = Pattern.matches(pattern, email);

        if(isMatch==false){
            model.addAttribute("alert","邮箱格式错误！");
            return "login";
        }

        if(email==null  && name==null){
            model.addAttribute("message","输入信息有误~请好好检查！");
            return "login";
        }

        UserExample example = new UserExample();
        example.createCriteria()
                .andEmailEqualTo(email);
        List<User> users = usermapper.selectByExample(example);
        if(users.size()>0){
            model.addAttribute("message","该邮箱已经被注册过啦！");
            return "login";
        }

        UserExample example2 = new UserExample();
        example.createCriteria()
                .andNameEqualTo(name);
        List<User> users2 = usermapper.selectByExample(example2);
        if(users.size()>0){
            model.addAttribute("message","该用户名已存在啦！");
            return "login";
        }

        if(password.length()<6 || password.length()>18 || password==null){
            model.addAttribute("message","密码不能小于6位且不能超过18位！");
            return "login";
        }

        User user = new User();
        user.setState(0);
        user.setName(name);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        user.setEmail(email);
        user.setPassword(password);
        user.setAvatarUrl("/TestAll/默认头像(2).png");
        String code = CodeUtil.generateUniqueCode();
        user.setCode(code);
        usermapper.insert(user);
//        发送邮件
        new Thread(new MailUtil(email, code)).start();
        model.addAttribute("message","快去邮箱激活吧~！");
        return "login";
    }

    @GetMapping("/pet/signin")
    public String active(@RequestParam("code") String code,
                         @RequestParam("email") String email,
                         Model model){
        UserExample example = new UserExample();
        example.createCriteria()
                .andCodeEqualTo(code);
        List<User> users = usermapper.selectByExample(example);
        if(users.size()!=0){
            String dbcode = users.get(0).getCode();
            if(dbcode.equals(code)){
                User user = new User();
                user.setState(1);
                user.setCode(null);
                UserExample example1 = new UserExample();
                example1.createCriteria()
                        .andCodeEqualTo(dbcode);
                usermapper.updateByExampleSelective(user, example1);
                model.addAttribute("message","注册成功！");
                return "redirect:/index";
            }
        }else {
            UserExample deleuser = new UserExample();
            deleuser.createCriteria()
                    .andEmailEqualTo(email);
            usermapper.deleteByExample(deleuser);
            model.addAttribute("message","注册失败了~T-T");
            return "login";
        }
        return null;
    }
}
