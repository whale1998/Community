package test.myfisttest.fristtest.Service;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.DTO.QuestionUserDTO;
import test.myfisttest.fristtest.Entity.Question;
import test.myfisttest.fristtest.Entity.QuestionExample;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Mapper.QuestionExtMapper;
import test.myfisttest.fristtest.Mapper.QuestionMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper usermapper;

    @Autowired
    private QuestionMapper questionmapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;


//    更新发布问题
    public Question createOrUpdate(Question question) {
        if(question.getId()==null){
            //插入
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionmapper.insert(question);
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andGmtCreateEqualTo(question.getGmtCreate());
            List<Question> questions = questionmapper.selectByExample(example);
            Question onesure = questionmapper.selectByPrimaryKey(questions.get(0).getId());
            return onesure;
        }else{
            //更新
            question.setGmtModified(System.currentTimeMillis());
            Question updatequestion = new Question();
            updatequestion.setDescription(question.getDescription());
            updatequestion.setGmtModified(System.currentTimeMillis());
            updatequestion.setTag(question.getTag());
            updatequestion.setTitle(question.getTitle());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int update=questionmapper.updateByExampleSelective(updatequestion, example);
            if(update != 1){
                throw new RuntimeError(ErrorCode.QUESTION_NOT_FOUND);
            }
           return  questionmapper.selectByPrimaryKey(question.getId());
        }
    }

    public PaginationDTO list(Integer page, Integer size){


        //保存数据库分页查出来的数据
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalcount =(int) questionmapper.countByExample(new QuestionExample());
        Integer totalpage;
        //        判断页数有多少
        if(totalcount % size ==0){
            totalpage = totalcount / size;
        }else{
            totalpage=totalcount / size + 1;
        }

        if(page < 1){
            page=1;
        }
        if(page > totalpage ){
            page=totalpage;
        }

        paginationDTO.setPagination(totalpage,page);

        //将page和size处理成 支持分页的sql语句
        Integer offset=size*(page-1);
        //可以接收从question表中查询出来的的集合,并且接受的集合：按分页方式返回的 从offset开始，size 条数据
        QuestionExample example = new QuestionExample();
//        设置倒序排列
        example.setOrderByClause("gmt_create desc");
        List<Question> questions = questionmapper.selectByExampleWithBLOBsWithRowbounds(example, new RowBounds(offset, size));
        //可以接收要返回的 QuestionUserDTO 集合
        List<QuestionUserDTO> questiondto= new ArrayList<>();

        //遍历从表中查询出来的内容,question表和User表一一对应
        for (Question question : questions) {
            //通过表中的Creator与User表中的ID 一一对应
            Long Creator = question.getCreator();
            User user=usermapper.selectByPrimaryKey(Creator);
            //封装一个新的 QuestionUserDTO 对象
            QuestionUserDTO dto = new QuestionUserDTO();
//            将question放入dto
            BeanUtils.copyProperties(question,dto);
            dto.setUser(user);
            //将对象全部存入 QuestionUserDTO 集合
            questiondto.add(dto);
        }

//        将查出来的发布问题加入到 pagination
        paginationDTO.setData(questiondto);

        //返回 PaginationDTO 集合
        return paginationDTO;
    }


//  个人页面
    public PaginationDTO listbyid(long userid, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userid);
        Integer totalcount =(int) questionmapper.countByExample(example);
        Integer totalpage;
        //        判断页数有多少
        if(totalcount % size ==0){
            totalpage = totalcount / size;
        }else{
            totalpage=totalcount / size + 1;
        }

        if(page < 1){
            page=1;
        }
        if(page > totalpage ){
            page=totalpage;
        }

        paginationDTO.setPagination(totalpage,page);

        Integer offset=size*(page-1);

        QuestionExample example1 = new QuestionExample();
        example1.createCriteria()
                .andCreatorEqualTo(userid);
        List<Question> questions = questionmapper.selectByExampleWithBLOBsWithRowbounds(example1, new RowBounds(offset, size));
        List<QuestionUserDTO> questiondto= new ArrayList<>();

        for (Question question : questions) {
            long Creator = question.getCreator();
            User user=usermapper.selectByPrimaryKey(Creator);
            QuestionUserDTO dto = new QuestionUserDTO();
//            将一个类快速封装成另一个类
            BeanUtils.copyProperties(question,dto);
            dto.setUser(user);
            questiondto.add(dto);
        }

        paginationDTO.setData(questiondto);

        return paginationDTO;
    }

//   根据用户的ID查询用户的问题，将问题封装成 用户-问题DTO

//    问题详情页面
    public QuestionUserDTO getbyid(long id) {
        Question question= questionmapper.selectByPrimaryKey(id);
        if(question ==null){
//            定义了枚举类ErrorCode因此不用每个都需要重写，只需要写一次
//            获取到异常，交给异常Controller处理
            throw new RuntimeError(ErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionUserDTO dto = new QuestionUserDTO();
        //将question快速封装到DTO中
        BeanUtils.copyProperties(question,dto);
        User user=usermapper.selectByPrimaryKey(question.getCreator());
        dto.setUser(user);
        return dto;
    }

//    观看数增加功能
    public void addView(long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.updateView(question);
    }

    public List<QuestionUserDTO> relatedselect(QuestionUserDTO questionuserDTO) {
//        先判断标签是否为空
        if(questionuserDTO.getTag()==null || questionuserDTO.getTag()==""){
            return new ArrayList<>();
        }
//        将拿出来的标签 将里面的 ， 代替成 |
        String[] tags = StringUtils.split( questionuserDTO.getTag(),",");
        String relatedtags = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(questionuserDTO.getId());
        question.setTag(relatedtags);

//        根据question去查找 得到 集合 在将集合转换成 questionDTO
        List<Question> questions = questionExtMapper.relatedselect(question);
//        !!!
        List<QuestionUserDTO> questionUserDTOS = questions.stream().map(q -> {
            QuestionUserDTO dto = new QuestionUserDTO();
            BeanUtils.copyProperties(q, dto);
            return dto;
        }).collect(Collectors.toList());
        return questionUserDTOS;
    }
}
