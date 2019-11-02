package test.myfisttest.fristtest.Cache;

import org.apache.commons.lang3.StringUtils;
import test.myfisttest.fristtest.DTO.TagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> gettag(){
        List<TagDTO> tagDTOS =new ArrayList<>();

        TagDTO location = new TagDTO();
        location.setTagname("地理位置");
        location.setTags(Arrays.asList("广东","北京","增城","揭阳","榕城","汕头","潮汕","湖南","深圳"));

        TagDTO pet = new TagDTO();
        pet.setTagname("动物种类");
        pet.setTags(Arrays.asList("猫","狗","仓鼠","龙猫","宠物蛇"));

        TagDTO type = new TagDTO();
        type.setTagname("问题类型");
        type.setTags(Arrays.asList("求收留","找伴丫","小妙招","经验分享","闲来一言"));

        tagDTOS.add(location);
        tagDTOS.add(pet);
        tagDTOS.add(type);
        return tagDTOS;
    }

    public static String filterinvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = gettag();
        List<String> listTags = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String collect = Arrays.stream(split).filter(t -> !listTags.contains(t)).collect(Collectors.joining(","));
        return collect;
    }
}
