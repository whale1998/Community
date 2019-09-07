package test.myfisttest.fristtest.Controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import test.myfisttest.fristtest.DTO.PassValueDTO;
import test.myfisttest.fristtest.DTO.PrivateLetterDTO;
import test.myfisttest.fristtest.Entity.Privateletter;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Mapper.PrivateletterMapper;
import test.myfisttest.fristtest.Service.PrivateLetterService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PrivateLetterController {

    @Autowired
    PrivateLetterService privateLetterService;
    @Autowired
    PrivateletterMapper privateletterMapper;

    @RequestMapping("/privateLetter")
    public String privateLetter(@RequestParam(required = false,name = "sendId")String sendId,
                                ModelMap model,
                                HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        List<PrivateLetterDTO> privateLetterDTOs = privateLetterService.list(user.getId());
        model.addAttribute("personList",privateLetterDTOs);
        if(StringUtils.isNotBlank(sendId)){
            long sendid = Long.parseLong(sendId);
            List<PrivateLetterDTO> personletterDTOList = privateLetterService.selectpersonletter(sendid, user.getId());
            model.addAttribute("personInfoList",personletterDTOList);
        }
        return "privateLetter";
    }

    @ResponseBody
    @RequestMapping("privateContent")
    public List<PrivateLetterDTO> privateContent(@RequestBody PassValueDTO passValueDTO){
//        将内容增加到数据库，再返回页面
        long Sid = Long.parseLong(passValueDTO.getSendId());
        long Rid = Long.parseLong(passValueDTO.getReceiveId());
        Privateletter record = new Privateletter();
        record.setContent(passValueDTO.getContent());
        record.setGmtCreate(System.currentTimeMillis());
        record.setReceiverId(Sid);
        record.setSendId(Rid);
        record.setStatus(0);
        privateletterMapper.insertSelective(record);
//        返回数据   (s-id s-name r-id content)
        List<PrivateLetterDTO> personInfoList = privateLetterService.selectpersonletter(Sid, Rid);
        return personInfoList;
    }

}
