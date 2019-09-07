package test.myfisttest.fristtest.Utils;


import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import test.myfisttest.fristtest.DTO.AccessTokenDTO;
import test.myfisttest.fristtest.DTO.GithubUser;

import java.io.IOException;
@Component
public class GithubUtil {
    //获取AccessToken
    //方法作用：将数据传送给服务器。 POST TO A SERVER
    public String getAccessToken(AccessTokenDTO atdot){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(atdot));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token") //调用地址
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String string =response.body().string();
                //把拿到的url 分解出 token，拿到token，返回token
                String[] split = string.split("&");
                String token = split[0].split("=")[1];
//                System.out.println(token);
                return token;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


    //通过AccessToken获取用户信息
   /* GET A URL
    This program downloads a URL and prints its contents as a string.*/
    public GithubUser getuser(String accessToken)  {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            //将URL转换为string类型
            Response response = client.newCall(request).execute();
            String string =response.body().string();
            //将JSON对象 转换成githubuser对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }
}
