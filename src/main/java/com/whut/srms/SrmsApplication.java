package com.whut.srms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.whut.srms.mapper")
public class SrmsApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(SrmsApplication.class, args);
    }

}
