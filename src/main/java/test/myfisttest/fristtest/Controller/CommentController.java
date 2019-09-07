package test.myfisttest.fristtest.Controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import test.myfisttest.fristtest.DTO.CommentCreateDTO;
import test.myfisttest.fristtest.DTO.CommentDTO;
import test.myfisttest.fristtest.DTO.ResultDTO;
import test.myfisttest.fristtest.Entity.Comment;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Enums.CommentType;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Service.CommentService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static test.myfisttest.fristtest.DTO.ResultDTO.errorof;
import static test.myfisttest.fristtest.DTO.ResultDTO.okof;

/*
*@ResponseBody 可以将JAVA对象转化为JSON格式对象发送给客户端
*@RequestBody 可以接受客户端传来的JSON格式对象转为JAVA对象  */

@ResponseBody
@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;


    @PostMapping("/comment")
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
//      拿到user，判断是否为空
        User user=(User) request.getSession().getAttribute("user");
        if(user == null){
//            返回枚举未登录
            return errorof(ErrorCode.NO_LOGIN);
        }
//        评论不能为空 使用comment lang帮助校验
        if(commentCreateDTO==null|| StringUtils.isBlank(commentCreateDTO.getContent())){
            return errorof(ErrorCode.COMMENT_IS_EMPTY);
        }
//      封装comment
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentid());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment,user);
//        成功则返回 成功
        return okof();
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id") Long id){
        List<CommentDTO> commentDTOS = commentService.listbytargetid(id, CommentType.COMMENT);
        return okof(commentDTOS);
    }
}
