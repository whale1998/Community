package test.myfisttest.fristtest.Cache;

import lombok.Data;
import org.springframework.stereotype.Component;
import test.myfisttest.fristtest.DTO.HotTagDTO;

import java.util.*;

@Component
@Data
public class HotTagCache {
    private List<String> hots = new ArrayList<>();


    public void updateTags(Map<String, Integer> tags) {
        int max = 5;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);
        tags.forEach((name, priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size() < max) {
                priorityQueue.add(hotTagDTO);
            } else {
                HotTagDTO minHot = priorityQueue.peek();
                if (hotTagDTO.compareTo(minHot) > 0) {
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });

//        取出队列里面的热门标签
        List<String> sortTag = new ArrayList<>();
//        取出第一个标签
        HotTagDTO poll = priorityQueue.poll();
        while (poll != null) {
            /*
             将这个标签加入list中
            * java.util.ArrayList.add(int index, E elemen) 方法将指定的元素E在此列表中的指定位置
            * 它改变了目前元素在该位置(如果有的话)和所有后续元素向右移动(将添加一个到其索引)。
             * */
            sortTag.add(0, poll.getName());
//            取出下个队列元素
            poll = priorityQueue.poll();
        }
        hots = sortTag;
    }
}
