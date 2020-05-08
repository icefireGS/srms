package com.whut.srms.service;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.whut.srms.mapper.FileMapper;
import com.whut.srms.mapper.VersionMapper;
import com.whut.srms.pojo.File;
import com.whut.srms.pojo.Version;
import com.whut.srms.pojo.versionListNode;
import com.whut.srms.property.UrlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VersionService {

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UrlProperties urlProperties;

    public List<versionListNode> queryVersions(Long id, Long user_id) {
        //查询原文件
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return null;
        }

        //查询历史文件
        Version record_v = new Version();
        record_v.setFile_id(myfile.getId());
        List<Version> myversions = this.versionMapper.select(record_v);
        List<versionListNode> result = new ArrayList<>();
        for (Version mynode : myversions) {
            String address = mynode.getAddress();
            versionListNode node = new versionListNode();
            node.setId(mynode.getId());
            node.setName(mynode.getName());
            node.setUpdate_time(mynode.getUpdate_time());
            if (address.startsWith(urlProperties.getTracker1_group_name())) {
                node.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
            } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
                node.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
            } else {
                node.setUrl(null);
            }
            result.add(node);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVersion(Long id, Long user_id) {
        //查询历史文件
        Version record_v = new Version();
        record_v.setId(id);
        record_v.setUser_id(user_id);
        Version myversion = this.versionMapper.selectOne(record_v);
        if (myversion == null) {
            return false;
        }

        //删除对应历史文件
        if (this.versionMapper.deleteByPrimaryKey(myversion) != 1) {
            throw new RuntimeException("删除历史文件失败");
        }

        //删除实际文件
        String address = myversion.getAddress();
        storageClient.deleteFile(address);

        return true;
    }

    public String getVerAddress(Long id, Long user_id) {
        //查询历史文件
        Version record_v = new Version();
        record_v.setId(id);
        record_v.setUser_id(user_id);
        Version myversion = this.versionMapper.selectOne(record_v);
        if (myversion == null) {
            return null;
        }

        return myversion.getAddress();
    }

}
