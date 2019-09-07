package test.myfisttest.fristtest.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import test.myfisttest.fristtest.DTO.CommentDTO;
import test.myfisttest.fristtest.DTO.QuestionUserDTO;
import test.myfisttest.fristtest.Entity.Comment;
import test.myfisttest.fristtest.Entity.CommentExample;
import test.myfisttest.fristtest.Enums.CommentType;
import test.myfisttest.fristtest.Mapper.CommentMapper;
import test.myfisttest.fristtest.Service.CommentService;
import test.myfisttest.fristtest.Service.QuestionService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionservice;
    @Autowired
    private CommentService commentService;
    @Autowired
    CommentMapper commentMapper;

    @GetMapping("/question/{id}")
    public  String question(@PathVariable("id") long id,
                            Model model){
        QuestionUserDTO questionuserDTO =questionservice.getbyid(id);
        List<QuestionUserDTO> selectrelated=questionservice.relatedselect(questionuserDTO);
        List<CommentDTO> coments = commentService.listbytargetid(id, CommentType.QUESTION);
//        观看数的功能
        questionservice.addView(id);
//        取十条相关联的问题
        int i=0;
        List<QuestionUserDTO> showtags = new ArrayList<>();
        for (QuestionUserDTO questionUserDTO : selectrelated) {
            showtags.add(questionUserDTO);
            i++;
            if(i>10){
                break;
            }
        }

        model.addAttribute("question",questionuserDTO);
        model.addAttribute("comments",coments);
        model.addAttribute("tags",showtags);
        return "question";
    }

    @RequestMapping("likecount")
    @ResponseBody
    public String likeCount(String yeses,String id){
        long commentId = Long.parseLong(id);
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        long likecount = Long.parseLong(yeses);
        comment.setLikeCount(likecount+1);
        CommentExample example = new CommentExample();
        example.createCriteria().andIdEqualTo(commentId);
        commentMapper.updateByExampleSelective(comment, example);
        return comment.getLikeCount().toString();
    }
}
