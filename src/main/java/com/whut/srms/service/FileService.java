package com.whut.srms.service;

import com.whut.srms.mapper.DirMapper;
import com.whut.srms.mapper.FileMapper;
import com.whut.srms.pojo.dir;
import com.whut.srms.pojo.file;
import com.whut.srms.pojo.fileTreeNode;
import com.whut.srms.utils.TreeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private DirMapper dirMapper;

    @Autowired
    private FileMapper fileMapper;

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<fileTreeNode> queryTree(Long user_id) {

        List<fileTreeNode> dirfile_all = new ArrayList<>();

        //先查询目录表
        dir record_dir = new dir();
        record_dir.setUser_id(user_id);
        List<dir> result_dir = this.dirMapper.select(record_dir);
        if (result_dir == null) {
            return null;
        }
        for (dir dir_node : result_dir) {
            fileTreeNode node = new fileTreeNode();
            node.setId(dir_node.getId());
            node.setPid(dir_node.getPid());
            node.setName(dir_node.getName());
            node.setIsfile(0);
            node.setChildren(new ArrayList<>());
            dirfile_all.add(node);
        }

        //再查询文件表
        file record_file = new file();
        record_file.setUser_id(user_id);
        List<file> result_file = this.fileMapper.select(record_file);
        if (result_file == null) {
            return null;
        }
        for (file file_node : result_file) {
            fileTreeNode node = new fileTreeNode();
            node.setId(file_node.getId());
            node.setPid(file_node.getPid());
            node.setName(file_node.getName());
            node.setIsfile(1);
            node.setChildren(null);
            dirfile_all.add(node);
        }

        //建树
        dirfile_all = TreeUtils.initTree(dirfile_all, (long) 0);

        return dirfile_all;
    }

}
