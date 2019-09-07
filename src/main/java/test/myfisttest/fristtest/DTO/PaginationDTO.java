package test.myfisttest.fristtest.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
//    是否有下一页、上一页、第一页、最后一页、要展示的页数、当前页
    private boolean showPrevious;
    private boolean showNext;

    private boolean showFirstPage;
    private boolean showEndPage;
    private List<Integer> pages = new ArrayList<>();
    private Integer page;
    private Integer totalpage;

    public void setPagination(Integer totalpage, Integer page) {

        this.totalpage=totalpage;

//      当前属性page等于传过来参数的page
        this.page=page;

//       将页面加入页面集
        pages.add(page);
        for(int i =1 ; i<=3 ; i++){
            if(page - i >0){
//                从0个元素开始插入，即开头插入
                pages.add(0,page-i);
            }
            if(page + i <=totalpage){
//                从尾部插入
                pages.add(page+i);
            }
        }

//        是否展示上一页
        if(page == 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }

//        是否展示下一页
        if(page == totalpage){
            showNext = false;
        }else{
            showNext = true;
        }

//        是否展示第一页
        if(pages.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }

//        是否展示最后一页
        if(pages.contains(totalpage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
