package test.myfisttest.fristtest.Mapper;

import test.myfisttest.fristtest.Entity.Question;

import java.util.List;

public interface QuestionExtMapper {

    int updateView(Question record);
    int updateCommentView(Question quesiont);
    List<Question> relatedselect(Question question);

    List<Question> selectAll();
}