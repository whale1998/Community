package test.myfisttest.fristtest.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class savaUploadFile {
    @Autowired
    private UserMapper userMapper;


//    @Value("${server.port}")
//    //获取主机端口
//    private String POST;

    public   String UploadFile(String filePathNew,Long id) {
//        //获取本机IP
//        String host = null;
//        try {
//            host = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//        }
        User uplocalFileEntity = new User();
        uplocalFileEntity.setAvatarUrl(filePathNew);

        UserExample example = new UserExample();
        example.createCriteria()
                .andIdEqualTo(id);
        int i = userMapper.updateByExampleSelective(uplocalFileEntity, example);
        System.out.println("插入了"  + i + "数据");

        System.out.println("uplocalFileEntity.getProfilePhoto():" + uplocalFileEntity.getAvatarUrl());
        return uplocalFileEntity.getAvatarUrl();
    }
}
