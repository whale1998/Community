package test.myfisttest.fristtest.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String tagname;
    private List<String> tags;
}
