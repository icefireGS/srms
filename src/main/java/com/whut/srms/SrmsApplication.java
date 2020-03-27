package com.whut.srms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.whut.srms.mapper")
public class SrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrmsApplication.class, args);
    }

}
