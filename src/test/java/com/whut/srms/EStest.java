package com.whut.srms;

import com.whut.srms.pojo.Shares;
import com.whut.srms.utils.CodeUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SrmsApplication.class)
public class EStest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public EStest() {
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    @Test
    public void createIndex() {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Shares.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Shares.class);
    }

    @Test
    public void codeTest() {
        System.out.println(CodeUtils.generateShortUuid());
    }
}
