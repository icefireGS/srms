package com.whut.srms;

import com.whut.srms.mapper.DirMapper;
import com.whut.srms.pojo.dir;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SrmsApplication.class)
public class MapperTest {

    @Autowired
    private DirMapper dirMapper;

    @Test
    public void testNull(){
        List<dir> dirtest = null;
        System.out.println(dirtest);
    }

    @Test
    public void testQuery(){
        dir record = new dir();
        record.setUser_id((long) 1);
        List<dir> dirList = dirMapper.select(record);
        System.out.println(dirList);
    }

}
