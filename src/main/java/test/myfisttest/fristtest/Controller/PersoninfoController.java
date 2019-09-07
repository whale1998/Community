package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Exception.RuntimeError;
import test.myfisttest.fristtest.Mapper.UserMapper;
import test.myfisttest.fristtest.Utils.FileUtils;
import test.myfisttest.fristtest.Utils.savaUploadFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class PersoninfoController {

    private final ResourceLoader resourceLoader;

    @Autowired
    public PersoninfoController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Value("${web.upload-path}")
    private String path;


    //图片存放根目录下的子目录
    @Value("${file.sonPath}")
    private String SON_PATH;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private savaUploadFile savaUploadFile;

    @GetMapping("/person")
    public String person( HttpServletRequest request,
                          Model model){

        User user=(User) request.getSession().getAttribute("user");
        if(user == null){
//            返回枚举未登录
            throw new RuntimeError(ErrorCode.NO_LOGIN);
        }

        User olduser = userMapper.selectByPrimaryKey(user.getId());
        model.addAttribute("name",olduser.getName());
        model.addAttribute("email",olduser.getEmail());
        model.addAttribute("bio",olduser.getBio());
        model.addAttribute("id",olduser.getId());
        model.addAttribute("avatar",olduser.getAvatarUrl());
        return "person";
    }

    @RequestMapping("/personinfomation")
//    requesbody 用来接收前端的JSON格式的数据
    public String modifipersoninfor(String name,String email,String bio,
                                    HttpServletRequest request,
                                    Model model){
        User user=(User) request.getSession().getAttribute("user");
        if(user == null){
//            返回枚举未登录
            throw new RuntimeError(ErrorCode.NO_LOGIN);
        }
//        String email=personInforDTO.getEmail();
//        String name=personInforDTO.getName();
//        String description=personInforDTO.getBio();

        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()!=0){
            model.addAttribute("name","该昵称已经存在辣");
        }

        UserExample example1 = new UserExample();
        example1.createCriteria().andNameEqualTo(email);
        List<User> userlist = userMapper.selectByExample(example1);
        if(userlist.size()!=0){
            model.addAttribute("email","该邮箱已经被注册辣！");
        }

        User updatauser = new User();
        updatauser.setName(name);
        updatauser.setBio(bio);
        updatauser.setEmail(email);

        UserExample example = new UserExample();
        example.createCriteria()
                .andIdEqualTo(user.getId());
        userMapper.updateByExampleSelective(updatauser, example);

        return "redirect:/person";
    }



    @PostMapping("/useravatar")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("id") Long id){
        //1定义要上传文件 的存放路径
        String localPath="E:\\TestAll";
        //2获得文件名字
        String fileName=file.getOriginalFilename();
        //2上传失败提示
        String warning="";
        if(FileUtils.upload(file, localPath, fileName)){
            //上传成功
            warning="上传成功";

        }else{
            warning="上传失败";
        }
        System.out.println(warning);

        String filePathNew = SON_PATH + fileName;
        String uploadFile = savaUploadFile.UploadFile(filePathNew, id);
        System.out.println(uploadFile);
        return "redirect:/person";
    }

}
