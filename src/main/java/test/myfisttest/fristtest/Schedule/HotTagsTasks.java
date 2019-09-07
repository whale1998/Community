package test.myfisttest.fristtest.Schedule;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import test.myfisttest.fristtest.Cache.HotTagCache;
import test.myfisttest.fristtest.Entity.Question;
import test.myfisttest.fristtest.Entity.QuestionExample;
import test.myfisttest.fristtest.Mapper.QuestionMapper;

import java.util.*;

@Component
@Slf4j
public class HotTagsTasks {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    HotTagCache hotTagCache;

    /*
    * 热门话题，即对每个标签进行计数从而得到每个标签的优先级
    * 计数器，在某个时刻 取出所有问题进行热度的计算
    * 取出问题的每个标签，对每个的计数是 5+回复数+之前的热度
    * */
    @Scheduled(fixedRate = 1000*60*60)
//    @Scheduled(cron = "0 0 1 * * *")
    public void reportCurrentTime() {
        int offset = 0;
        int limit = 5;
        List<Question> list = new ArrayList<>();
        Map<String, Integer> properties = new HashMap<>();
        while (offset == 0 || list.size() == limit) {
//            取出分页里的问题
            List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question : questionList) {
//                取出每个问题里的每个标签
                String[] tags = StringUtils.split(question.getTag(), ",");
                for (String tag : tags) {
                    Integer propertie = properties.get(tag);
                    if (propertie != null) {
                        properties.put(tag, propertie + 5 + question.getCommentCount());
                    } else {
                        properties.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            offset = +limit;
            hotTagCache.updateTags(properties);
            log.info("The time is now {}", new Date());
        }
    }
}
