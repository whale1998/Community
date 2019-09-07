package test.myfisttest.fristtest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import test.myfisttest.fristtest.DTO.FileUploadDTO;
import test.myfisttest.fristtest.Utils.UCloudUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class UploadController {
    @Autowired
    private UCloudUtil uCloudUtil;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileUploadDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartrequest =(MultipartHttpServletRequest) request;
        MultipartFile file = multipartrequest.getFile("editormd-image-file");
        try {
            String filename = uCloudUtil.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            FileUploadDTO fileUploadDTO = new FileUploadDTO();
            fileUploadDTO.setSuccess(1);
            fileUploadDTO.setUrl(filename);
            return  fileUploadDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
