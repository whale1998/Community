package test.myfisttest.fristtest.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Service.NotificationService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//该类本身不属于IOC管理，添加到IOC中才能去使用Usermapper
@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper usermapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies !=null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("cookie")){
                    String mytoken=cookie.getValue();
//                    System.out.println(mytoken);
                    //根据token和数据库的token对比，一致则返回user对象
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(mytoken)
                            .andStateEqualTo(1);
                    List<User> users = usermapper.selectByExample(userExample);
                    if(users.size()!=0){
                        //将User对象信息放入session域
                        request.getSession().setAttribute("user",users.get(0));
                        Long unreadCount=notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("unreadcount",unreadCount);
                    }
                    break;
                }
            }
        }
        //true放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
