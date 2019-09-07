package test.myfisttest.fristtest.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Entity.UserExample;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdata(User user) {
        UserExample userExample = new UserExample();
        // Criteria类是UserExample类里面的内部类，Criteria类是干什么用的呢？它专门用于封装自定义查询条件的
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0){
//           插入
           //获取当前系统的时间戳
           user.setGmtCreate(System.currentTimeMillis());
           user.setGmtModified(user.getGmtCreate());
           userMapper.insert(user);
       }else{
//           更新
            User dbuser = users.get(0);
            User updateUser= new  User();
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setToken(user.getToken());
            updateUser.setName(user.getName());
            updateUser.setGmtModified(System.currentTimeMillis());
            UserExample example=new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbuser.getId());
            //updateByExampleSelective只更新一部分，updateUser更新内容，example更新规则
            userMapper.updateByExampleSelective(updateUser,example);
       }
    }

    public void userlogin(User user) {

    }
}
