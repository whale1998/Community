package test.myfisttest.fristtest.DTO;

import lombok.Data;
import test.myfisttest.fristtest.Entity.User;
@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String content;
    private Integer commentCount;
    private User user;

}
