package test.myfisttest.fristtest.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import test.myfisttest.fristtest.Cache.HotTagCache;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.Entity.*;
import test.myfisttest.fristtest.Mapper.CommentMapper;
import test.myfisttest.fristtest.Mapper.QuestionMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Service.QuestionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class BackController {


    @Autowired
    private QuestionService qusetionservice;
    @Autowired
    HotTagCache hotTagCache;

    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CommentMapper commentMapper;

    @RequestMapping("/back")
//    帖子详情管理
    public String backController(HttpServletRequest request,
                                 Model model,
                                 //从前端去获取一个pege页数和展示size条数据
                                 @RequestParam(name = "page",defaultValue = "1") Integer page,
                                 @RequestParam(name = "size",defaultValue = "5") Integer size){
        //      将设置好的 page和size传回前端
        PaginationDTO list = qusetionservice.list(page,size);
        List<String> hots = hotTagCache.getHots();
        model.addAttribute("tags",hots);
        model.addAttribute( "pagination",list);
        return "backindex";
    }

//    后台——删除帖子
    @RequestMapping("/deleteMsg")
    @ResponseBody
    public String deleteMsg(@RequestParam("id") String id){
        long questionid = Long.parseLong(id);
        questionMapper.deleteByPrimaryKey(questionid);
        return "success";
    }

    //    后台——删除评论
    @RequestMapping("/delCom")
    @ResponseBody
    public String delCom(@RequestParam("id") String id){

        long commentId = Long.parseLong(id);
//        找到问题ID，并且设置评论数-1
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        Long parentId = comment.getParentId();
        Question question = questionMapper.selectByPrimaryKey(parentId);
        int count = question.getCommentCount() - 1;
        question.setCommentCount(count);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andIdEqualTo(parentId);
        questionMapper.updateByExampleSelective(question,example);

//        删除评论
        commentMapper.deleteByPrimaryKey(commentId);
        return "success";
    }

//    用户详情管理
    @RequestMapping("/backuser")
    public String backUser(Model model){
        List<User> userList = userMapper.selectAll();
        model.addAttribute("userList",userList);
        model.addAttribute("section","model");
        return "backUser";
    }

    //    后台——删除用户
    @RequestMapping("/deleteUser")
    @ResponseBody
    public String deleteUser(@RequestParam("id") String id){
        long userid = Long.parseLong(id);
        userMapper.deleteByPrimaryKey(userid);
        return "success";
    }


    //    后台——禁用用户
    @RequestMapping("/disableuser")
    @ResponseBody
    public String disableuser(@RequestParam("id") String id){
        long Uid = Long.parseLong(id);
        changeUserState(Uid,0);
        return "success";
    }

    //    后台——解禁用户
    @RequestMapping("/ableuser")
    @ResponseBody
    public String ableuser(@RequestParam("id") String id){
        long Uid = Long.parseLong(id);
       changeUserState(Uid,1);
        return "success";
    }

    public void changeUserState(long Uid,int state){
        User user = userMapper.selectByPrimaryKey(Uid);
        user.setState(state);
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(Uid);
        userMapper.updateByExampleSelective(user,example);
    }





}
