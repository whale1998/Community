package test.myfisttest.fristtest.Service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.myfisttest.fristtest.DTO.CommentDTO;
import test.myfisttest.fristtest.Entity.*;
import test.myfisttest.fristtest.Enums.CommentType;
import test.myfisttest.fristtest.Enums.NotificationStatus;
import test.myfisttest.fristtest.Enums.NotificationType;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Mapper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CommentService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /* 增加评论功能
     * 每次报错都要返回上一级跳转 （太麻烦 耗费性能）
     * 封装一个枚举异常类 作为跳转
     *
     * @Transactional开启事务AOP支持，若其中有一条抛出RuntimeExecption或者ERROR时候，则执行语句全部回滚（失败）*/
    @Transactional
    public void insert(Comment comment, User commentator) {
//        判断是否有该问题
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new RuntimeError(ErrorCode.TARGET_PARAM_NOT_FOUND);
        }
//        判断是否有评论类型
        if (comment.getType() == null || !CommentType.isexit(comment.getType())) {
            throw new RuntimeError(ErrorCode.COMMENT_NOT_FOUND);
        }
//        评论操作
        if (comment.getType() == CommentType.COMMENT.getCode()) {
//            回复评论

//            根据comment的parentid（问题ID）去取出 整条comment数据
            Comment dbcomment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbcomment == null) {
                throw new RuntimeError(ErrorCode.COMMENT_PARAM_NOT_FOUND);
            }
            //            回复问题    根据Comment的父类ID去找问题
            Question question = questionMapper.selectByPrimaryKey(dbcomment.getParentId());
            if (question == null) {
                throw new RuntimeError(ErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            //        增加评论数（二级评论数）
            Comment parentcomment = new Comment();
            parentcomment.setId(comment.getParentId());
            parentcomment.setCommentCount(1);
            commentExtMapper.updateCommentView(parentcomment);

//            新增通知
            NotificationInfo(comment, dbcomment.getCommentator(), commentator.getName(), question.getTitle(), question.getId());
        } else {
//            回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new RuntimeError(ErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
//            问题评论成功 评论数+1
//            要设置评论数加X（1/评论一次增加一次），在执行评论数增加操作（在数据库根据原来的加上X）
            question.setCommentCount(1);
            questionExtMapper.updateCommentView(question);
            //            新增通知
            NotificationInfo(comment, question.getCreator(), commentator.getName(), question.getTitle(), question.getId());
        }
    }

    private void NotificationInfo(Comment comment, Long commentator, String notifiername, String outertitle, Long outerid) {
//       假如是自己回复自己的话，则不需用有未通知。
        if(commentator == comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(NotificationType.REPLY_COMMENT.getType());
        notification.setStatus(NotificationStatus.UNREAD.getType());
        notification.setNotifier(comment.getCommentator());
        notification.setOuterid(outerid);
        notification.setNotifierName(notifiername);
        notification.setOuterTitle(outertitle);
        notification.setReceiver(commentator);
        notificationMapper.insert(notification);
    }

    //    展示评论功能
    public List<CommentDTO> listbytargetid(Long id, CommentType type) {
//       根据问题ID和评论类型去找评论
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getCode());
//        根据 （条件）创建时间 进行排序
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
//        判断评论是否为空
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
//      在comments中去根据 评论 去获取 评论人的ID 并且去重（去掉重复的值）
        Set<Long> commentators = comments.stream()
                .map(comment -> comment.getCommentator())
                .collect(Collectors.toSet());
        List<Long> userids = new ArrayList<>();
        userids.addAll(commentators);

//        根据评论人的ID 去获取用户并转为map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userids);
        List<User> users = userMapper.selectByExample(userExample);
//        ?????
        Map<Long, User> usermap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

//         comment转换为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
//            将comments和user封装进DTO
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(usermap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
