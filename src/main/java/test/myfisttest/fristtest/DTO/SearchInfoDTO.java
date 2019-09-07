package test.myfisttest.fristtest.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchInfoDTO implements Serializable {
    private long id;
    private long questionId;
    private String questionTitle;
    private String questionContent;
    private String questionTag;
    private String commentContent;
    private String userAvatarUrl;
    private Integer questionViewCount;
    private Integer questionCommentCount;
    private long questionGmtCreate;
}
