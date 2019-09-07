package test.myfisttest.fristtest.Utils;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import cn.ucloud.ufile.http.OnProgressListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.Exception.ErrorCode;
import test.myfisttest.fristtest.Exception.RuntimeError;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UCloudUtil {
/*可以对该方法进行封装，对其他的变量进行封装，从而处理成一个通用的类*/
    @Value("${ucloud.ufile.public-key}")
    private String publickey;

    @Value("${ucloud.ufile.private-key}")
    private String privatekey;

    private String bucketName = "mypet";


    public String upload(InputStream fileStream, String mimetype,String filename) {
//        保证上传两张相同的图片时，保存图片的名字不同
        String generatedFilename;
//        根据 . 切割 需要进行转义
        String[] filepath = filename.split("\\.");
        if(filepath.length>1){
//            生成新名字   》filepath.length-1 文件的扩展名
            generatedFilename= UUID.randomUUID().toString()+"."+filepath[filepath.length-1] ;
        }else {
            return null;
        }

        try {
            // 对象相关API的授权器
//            Spring在实例化才会给属性赋值，而如果在放在上面，因为objectAuthorization已经实例化了，所以不能赋值了。因此取值为null
            ObjectAuthorization objectAuthorization = new UfileObjectLocalAuthorization(publickey, privatekey);
            // 对象操作需要ObjectConfig来配置您的地区和域名后缀
            ObjectConfig config = new ObjectConfig("cn-gd", "ufileos.com");
            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(fileStream, mimetype)
//                   保存为该文件的关键名。通过关键名去找图片 》 .nameAs("save as keyName")
                    .nameAs(generatedFilename)
                    .toBucket(bucketName)
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(long bytesWritten, long contentLength) {
                        }
                    })
                    .execute();
//            判断response是否有值，如果有值，则获得该图片的URL 并且 设置其过期时间
//            简单的防止别人对图片URL的窃取，加入过期时间和公钥（更换公钥则失效）
                    if(response !=null && response.getRetCode()==0){
                        String url=UfileClient.object(objectAuthorization, config)
                                .getDownloadUrlFromPrivateBucket(generatedFilename,bucketName,24*60*60*365*10)
                                .createUrl();
                        return url;
                    }else{
                        throw new RuntimeError(ErrorCode.FILE_UPLOAD_FAIL);
                    }
        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new RuntimeError(ErrorCode.FILE_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new RuntimeError(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }
}
