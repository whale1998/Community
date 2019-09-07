package test.myfisttest.fristtest.Service;


import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.DTO.SearchInfoDTO;
import test.myfisttest.fristtest.Entity.Question;
import test.myfisttest.fristtest.Mapper.QuestionExtMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchKeyService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    JestClient jestClient;
    @Autowired
    QuestionExtMapper questionExtMapper;

    public PaginationDTO list(String keywords, Integer page, Integer size) throws Exception {

        List<SearchInfoDTO> searchInfoDTOList=new ArrayList<>();
        String dslStr = getDslStr(keywords,page,size);
        //使用API进行查询
        Search search = new Search.Builder(dslStr).addIndex("pet").addType("searchinfo").build();
        SearchResult infos = jestClient.execute(search);
        List<SearchResult.Hit<SearchInfoDTO, Void>> hits = infos.getHits(SearchInfoDTO.class);
        for (SearchResult.Hit<SearchInfoDTO, Void> hit : hits) {
            SearchInfoDTO searchInfoDTO = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null){
                String questionTitle = highlight.get("questionTitle").get(0);
                searchInfoDTO.setQuestionTitle(questionTitle);
            }
            searchInfoDTOList.add(searchInfoDTO);
        }


        List<Question> questionList = questionExtMapper.selectAll();
        Integer totalcount= questionList.size();
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

        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalpage,page);


        paginationDTO.setData(searchInfoDTOList);
        return paginationDTO;

    }

//    获得查询语句
    public String getDslStr(String keyword,Integer page, Integer size) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

//        查询条件 query->bool->should->match
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder questionContent = new MatchQueryBuilder("questionContent", keyword);
            boolQueryBuilder.should(questionContent);
            MatchQueryBuilder questionTitle = new MatchQueryBuilder("questionTitle", keyword);
            boolQueryBuilder.should(questionTitle);
            MatchQueryBuilder questionTag = new MatchQueryBuilder("questionTag", keyword);
            boolQueryBuilder.should(questionTag);
            MatchQueryBuilder commentContent = new MatchQueryBuilder("CommentContent", keyword);
            boolQueryBuilder.should(commentContent);
            boolQueryBuilder.minimumShouldMatch(1);
        }

        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(size);
        sourceBuilder.from(size*(page-1));
//     highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        高亮的前标签
        highlightBuilder.preTags("<font style='color:red;'>");
//        高亮的字段名
        highlightBuilder.field("questionTitle");
//        高亮的后标签
        highlightBuilder.postTags("</font>");
        sourceBuilder.highlighter(highlightBuilder);

//生成查询语句
        String dslstr = sourceBuilder.toString();

        return dslstr;
    }

}
