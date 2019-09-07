package test.myfisttest.fristtest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("test.myfisttest.fristtest.Mapper")
@EnableScheduling
public class FristtestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FristtestApplication.class, args);
    }

}
