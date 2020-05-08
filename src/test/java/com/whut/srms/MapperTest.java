package com.whut.srms;

import com.whut.srms.mapper.DirMapper;
import com.whut.srms.mapper.FileMapper;
import com.whut.srms.mapper.VersionMapper;
import com.whut.srms.pojo.Dir;
import com.whut.srms.pojo.Version;
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

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private VersionMapper versionMapper;

    @Test
    public void testNull(){
        List<Dir> dirtest = null;
        System.out.println(dirtest);
    }

    @Test
    public void testQuery(){
        Dir record = new Dir();
        record.setUser_id((long) 1);
        List<Dir> dirList = dirMapper.select(record);
        System.out.println(dirList);
    }

    @Test
    public void testTrim() {
        String test = "  uy  ii  ";
        test = test.trim();
        System.out.println(test);
    }

    @Test
    public void testVersion() {
        List<Version> myversions = versionMapper.selectByFileIdDESC((long) 11);
        System.out.println(myversions);

        while (myversions.size() > 1) {
            int deletenum = versionMapper.delete(myversions.get(myversions.size() - 1));
            System.out.println("deleteNum:" + deletenum);
            myversions.remove(myversions.size() - 1);
        }

        List<Version> myversionk = versionMapper.selectByFileIdDESC((long) 11);
        System.out.println(myversionk);
    }
}
