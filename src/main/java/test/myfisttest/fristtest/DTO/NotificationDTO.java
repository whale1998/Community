package test.myfisttest.fristtest.DTO;

import lombok.Data;
import test.myfisttest.fristtest.Entity.User;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerid;
    private String typeName;
    private Integer type;
}
