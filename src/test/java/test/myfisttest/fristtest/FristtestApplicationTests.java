package test.myfisttest.fristtest;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import test.myfisttest.fristtest.DTO.SearchInfoDTO;
import test.myfisttest.fristtest.Entity.Comment;
import test.myfisttest.fristtest.Entity.CommentExample;
import test.myfisttest.fristtest.Entity.Question;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Mapper.CommentMapper;
import test.myfisttest.fristtest.Mapper.QuestionExtMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FristtestApplicationTests {

    @Autowired
    JestClient jestClient;
    @Autowired
    QuestionExtMapper questionExtMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;

    @Test
    public void contextLoads() throws IOException {

    }


    @Test
    public void saveData() throws IOException {
//        查询数据
        //        封装SearchInfo
        List<SearchInfoDTO> searchInfoDTOList = new ArrayList<>();

        List<Question> questionList = questionExtMapper.selectAll();
        for (Question question : questionList) {
            SearchInfoDTO searchInfoDTO = new SearchInfoDTO();
            searchInfoDTO.setQuestionContent(question.getDescription());
            searchInfoDTO.setQuestionTag(question.getTag());
            searchInfoDTO.setQuestionTitle(question.getTitle());
            searchInfoDTO.setQuestionCommentCount(question.getCommentCount());
            searchInfoDTO.setQuestionGmtCreate(question.getGmtCreate());
            searchInfoDTO.setQuestionId(question.getId());

            User user = userMapper.selectByPrimaryKey(question.getCreator());
            searchInfoDTO.setUserAvatarUrl(user.getAvatarUrl());


            CommentExample example = new CommentExample();
            example.createCriteria().andParentIdEqualTo(question.getId());
            List<Comment> commentList = commentMapper.selectByExample(example);
//            有一些问题没有评论
            if (commentList != null) {
                for (Comment comment : commentList) {
                    searchInfoDTO.setCommentContent(comment.getContent());
                }
            }
            searchInfoDTOList.add(searchInfoDTO);
        }

//        放入es
        for (SearchInfoDTO searchInfoDTO : searchInfoDTOList) {
            Index put = new Index.Builder(searchInfoDTO).index("pet").type("searchinfo").id(String.valueOf(searchInfoDTO.getQuestionId())).build();
            jestClient.execute(put);
        }
        System.out.println("数据导入成功");
    }
}
