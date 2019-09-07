package test.myfisttest.fristtest.DTO;

import lombok.Data;

@Data
public class CommentCreateDTO {
//    评论问题实体
    private Long parentid;
    private String content;
    private int type;
}
