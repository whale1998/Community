package test.myfisttest.fristtest.Controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import test.myfisttest.fristtest.Cache.TagCache;
import test.myfisttest.fristtest.DTO.QuestionUserDTO;
import test.myfisttest.fristtest.Entity.Question;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Service.QuestionService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class publishController {

    @Autowired
    private  QuestionService questionService;

    //更新发布问题
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model) {
        QuestionUserDTO question = questionService.getbyid(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags",TagCache.gettag());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags",TagCache.gettag());
        return "publish";

    }

//    JAVA中Long 类型建立的时候如果没赋值，会默认给一个0.而long类型建立的时候如果没有赋值，则是null！
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required =false ) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model
    ) throws IOException {

//      将参数放入模板 传到前端页面,要放置在验证之前，不然在检验时候出错，参数还未放入model中，无法将参数回调给前端页面

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",TagCache.gettag());

//        对前端页面的检验
        if(title==null || title==""){
             model.addAttribute("error","标题不能为空")   ;
             return "publish";
        }
        if(description==null || description==""){
            model.addAttribute("error","问题补充不能为空")   ;
            return "publish";
        }
        if(tag==null || tag==""){
            model.addAttribute("error","标签不能为空")   ;
            return "publish";
        }
        String filterinvalid = TagCache.filterinvalid(tag);
        if(StringUtils.isNotBlank(filterinvalid)){
            model.addAttribute("error","标签非法输入")   ;
            return "publish";
        }

        //检验是否登陆
        User user=null;
        user = (User) request.getSession().getAttribute("user");
//        若不为空则放回错误信息
        if(user == null){
            model.addAttribute("error","用户未登录");
        return "publish";
        }

//        否则，发布文章，将文章存入数据库
        Question question=new Question();
        question.setTitle(title);
        question.setTag(tag);
        question.setDescription(description);
        question.setCreator(user.getId());
        question.setId(id);
        question.setCommentCount(0);
        question.setViewCount(0);
        question.setLikeCount(0);
        Question newquestion = questionService.createOrUpdate(question);
//        将问题存入索引库
//        Document document = LuceneUtils.javaBean2Document(newquestion);
        /**
         * IndexWriter将我们的document对象写到硬盘中
         *
         * 参数一：Directory d,写到硬盘中的目录路径是什么
         * 参数二：Analyzer a, 以何种算法来对document中的原始记录表数据进行拆分成词汇表
         * 参数三：MaxFieldLength mfl 最多将文本拆分出多少个词汇
         *
         * */
//        IndexWriter indexWriter = new IndexWriter(LuceneUtils.getDirectory(), LuceneUtils.getAnalyzer(), LuceneUtils.getMaxFieldLength());
//        //将Document对象通过IndexWriter对象写入索引库中
//        indexWriter.addDocument(document);
//        //关闭IndexWriter对象
//        indexWriter.close();
        return "redirect:/index";
    }
}
