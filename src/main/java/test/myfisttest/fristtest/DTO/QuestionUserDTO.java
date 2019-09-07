package test.myfisttest.fristtest.DTO;

import lombok.Data;
import test.myfisttest.fristtest.Entity.User;

@Data
public class QuestionUserDTO {
    private long id;
    private String title;
    private String description;
    private long gmtCreate;
    private long gmtModified;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private  String tag;
    private long creator;
    private User user;
}
