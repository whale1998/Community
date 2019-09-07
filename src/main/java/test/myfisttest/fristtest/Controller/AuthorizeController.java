package test.myfisttest.fristtest.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.DTO.AccessTokenDTO;
import test.myfisttest.fristtest.DTO.GithubUser;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Service.UserService;
import test.myfisttest.fristtest.Utils.GithubUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Controller
/*
@Slf4j
若要在某个地方打出日志，类似 DEBUG 的断点 需要添加注解
*/

public class AuthorizeController {

    @Autowired
    private GithubUtil githubutil;

    //将其配置在配置文件，这样在切换开发环境时候不用一个一个找，使用配置文件集体改
    //用@Value去映射properties的值
    //此时需要加入${}来获取对应的值
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecrect;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper usermapper;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callb1ack(@RequestParam("code") String code,
                            @RequestParam("state") String state,
                            HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecrect);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        //传入一个accessTokenDTO对象去获取 accessToken
        String token = githubutil.getAccessToken(accessTokenDTO);
        //拿到Token去获取用户信息
        GithubUser githubUser = githubutil.getuser(token);
        System.out.println(githubUser.getName());

        //此时要注意  return "redirect:/" 的时候，redirect 此种方式 要补全地址，不会被视图解析器加上前后缀

        if(githubUser!=null){
            //增加用户
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            //使用UUID生成唯一的序列id 自动生成主键的办法
            String mytoken= UUID.randomUUID().toString();
            user.setToken(mytoken);
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdata(user);
            //登陆成功 将生成的token写入 cookie中
            Cookie cook = new Cookie("cookie",mytoken);
//            保存一周
            cook.setMaxAge(604800000);
            response.addCookie(cook);
            return "redirect:/index";
        }else{
            /*指出断点地方需要被打出日志的级别，并且 {}用来存放 githubUser 的信息
            log.error("错误的是：{}",githubUser);*/
            //登陆失败，重新登陆，回到主页
            return "redirect:/index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         HttpServletRequest request){
        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("cookie",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/index";
    }

//    普通用户登陆
    @PostMapping("/userlogin")
    public String userlogin(@RequestParam("email") String email,
                            @RequestParam("password") String pwd,
                            HttpServletResponse response,
                            Model model){
        UserExample user = new UserExample();
        user.createCriteria()
                .andEmailEqualTo(email)
                .andPasswordEqualTo(pwd);
        List<User> users = usermapper.selectByExample(user);
        if(users.get(0).getState()==0){
            model.addAttribute("message","未激活用户T-T~");
            return "login";
        }
        if(users.size()!=0){
            //登陆成功 将生成的token写入 cookie中
            String mytoken= UUID.randomUUID().toString();
            users.get(0).setToken(mytoken);
            UserExample example = new UserExample();
            example.createCriteria()
                    .andEmailEqualTo(email);
            usermapper.updateByExampleSelective(users.get(0), example);
            Cookie cook = new Cookie("cookie",mytoken);
//            保存一周
            cook.setMaxAge(604800000);
            response.addCookie(cook);
            return "redirect:/index";
        }else {
            model.addAttribute("message" , "登陆失败，查看是否都写对了！");
            return "login";
        }
    }


}
