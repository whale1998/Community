package test.myfisttest.fristtest.DTO;

import lombok.Data;

@Data
public class PrivateLetterDTO {
    private String personSendAratorUrl;
    private String personReceiveAratorUrl;
    private long sendId;
    private long receiveId;
    private String sendName;
    private String receiveName;
    private String content;
}
