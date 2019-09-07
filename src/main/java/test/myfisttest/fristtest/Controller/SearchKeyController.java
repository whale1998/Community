package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.DTO.PaginationDTO;
import test.myfisttest.fristtest.Service.SearchKeyService;


@Controller
public class SearchKeyController {

    @Autowired
    private SearchKeyService searchKeyService;


    @RequestMapping("/searchkey")
    public String findindexdb(@RequestParam("search") String keywords,
                              Model model,
                              @RequestParam(name = "page", defaultValue = "1") Integer page,
                              @RequestParam(name = "size", defaultValue = "5") Integer size) throws Exception {
        PaginationDTO list = searchKeyService.list(keywords, page, size);
        if (list.getData().size() == 0) {
            model.addAttribute("alert", "没有结果呢~要不换个关键词试试？");
        }
        model.addAttribute("Search", list);
        return "firstPage";
    }



}