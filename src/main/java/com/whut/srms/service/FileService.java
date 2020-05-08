package com.whut.srms.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.whut.srms.mapper.*;
import com.whut.srms.pojo.*;
import com.whut.srms.property.CustomInfoProperties;
import com.whut.srms.property.UrlProperties;
import com.whut.srms.property.UserProperties;
import com.whut.srms.utils.TreeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private DirMapper dirMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UserProperties userProperties;

    @Autowired
    private UrlProperties urlProperties;

    @Autowired
    private CustomInfoProperties customInfoProperties;

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<fileTreeNode> queryTree(Long user_id) {

        List<fileTreeNode> dirfile_all = new ArrayList<>();

        //先查询目录表
        Dir record_dir = new Dir();
        record_dir.setUser_id(user_id);
        List<Dir> result_dir = this.dirMapper.select(record_dir);
        if (result_dir == null) {
            return null;
        }
        for (Dir dir_node : result_dir) {
            fileTreeNode node = new fileTreeNode();
            node.setId(dir_node.getId());
            node.setPid(dir_node.getPid());
            node.setName(dir_node.getName());
            node.setIsfile(0);
            node.setSize(null);
            node.setTime(dir_node.getCreate_time());
            node.setChildren(new ArrayList<>());
            node.setUrl(null);
            dirfile_all.add(node);
        }

        //再查询文件表
        File record_file = new File();
        record_file.setUser_id(user_id);
        List<File> result_file = this.fileMapper.select(record_file);
        if (result_file == null) {
            return null;
        }
        for (File file_node : result_file) {
            fileTreeNode node = new fileTreeNode();
            node.setId(file_node.getId());
            node.setPid(file_node.getPid());
            node.setName(file_node.getName());
            node.setIsfile(1);
            node.setSize(file_node.getSize());
            node.setTime(file_node.getUpdate_time());
            node.setChildren(null);
            String address = file_node.getAddress();
            if (address.startsWith(urlProperties.getTracker1_group_name())) {
                node.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
            } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
                node.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
            } else {
                node.setUrl(null);
            }
            dirfile_all.add(node);
        }

        //建树
        dirfile_all = TreeUtils.initTree(dirfile_all, (long) 0);

        return dirfile_all;
    }

    public List<fileListNode> getfileList(Long id, Long user_id) {

        List<fileListNode> dirfile = new ArrayList<>();

        //先查询目录表
        Dir record_d = new Dir();
        record_d.setPid(id);
        record_d.setUser_id(user_id);
        List<Dir> mydirs = this.dirMapper.select(record_d);
        if (mydirs != null && !mydirs.isEmpty()) {
            for (Dir mydir : mydirs) {
                fileListNode node = new fileListNode();
                node.setId(mydir.getId());
                node.setIsfile(0);
                node.setName(mydir.getName());
                node.setSize(null);
                node.setType(null);
                node.setTime(mydir.getCreate_time());
                node.setUrl(null);
                dirfile.add(node);
            }
        }

        //再查询文件表
        File record_f = new File();
        record_f.setPid(id);
        record_f.setUser_id(user_id);
        List<File> myfiles = this.fileMapper.select(record_f);
        if (myfiles != null && !myfiles.isEmpty()) {
            for (File myfile : myfiles) {
                fileListNode node = new fileListNode();
                node.setId(myfile.getId());
                node.setName(myfile.getName());
                node.setIsfile(1);
                node.setSize(myfile.getSize());
                node.setType(myfile.getType());
                node.setTime(myfile.getUpdate_time());
                String address = myfile.getAddress();
                if (address.startsWith(urlProperties.getTracker1_group_name())) {
                    node.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
                } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
                    node.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
                } else {
                    node.setUrl(null);
                }
                dirfile.add(node);
            }
        }

        return dirfile;
    }

    public Boolean mkdir(Dir dir) {
        //去除文件夹命名前后空格
        dir.setName(dir.getName().trim());

        //pid不为0先判断父目录是否存在
        if (dir.getPid() != 0) {
            Dir record = new Dir();
            record.setId(dir.getPid());
            record.setUser_id(dir.getUser_id());
            Dir father = this.dirMapper.selectOne(record);
            if (father == null) {
                return false;
            }
        }

        //判断同目录下是否有同名目录
        dir.setId(null);
        dir.setCreate_time(null);
        Dir samename = this.dirMapper.selectOne(dir);
        if (samename != null) {
            return false;
        }

        //插入数据库
        dir.setId(null);
        dir.setCreate_time(new Date());
        return this.dirMapper.insertSelective(dir) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(File file_info, MultipartFile file) throws Exception {
        //去除文件命名前后空格
        file_info.setName(file_info.getName().trim());

        //判断文件是否为空
        if (file == null || file.getSize() == 0) {
            return customInfoProperties.getErrorHeader()+":"+"上传文件不能为空";
        }

        //pid不为0先判断父目录是否存在
        if (file_info.getPid() != 0) {
            Dir record = new Dir();
            record.setId(file_info.getPid());
            record.setUser_id(file_info.getUser_id());
            Dir father = this.dirMapper.selectOne(record);
            if (father == null) {
                return customInfoProperties.getErrorHeader()+":"+"父目录不存在";
            }
        }

        //判断同目录下是否含有同名文件
        file_info.setId(null);
        file_info.setAddress(null);
        file_info.setSize(null);
        file_info.setType(null);
        file_info.setUpdate_time(null);
        File samename = this.fileMapper.selectOne(file_info);
        if (samename != null) {
            return customInfoProperties.getErrorHeader()+":"+"当前目录下含有同名文件";
        }

        //查询空间是否足够
        Long size = file.getSize();
        User record_u = new User();
        record_u.setId(file_info.getUser_id());
        User myuser = this.userMapper.selectOne(record_u);
        if (myuser == null) {
            return customInfoProperties.getErrorHeader()+":"+"用户不存在";
        }
        Long currentSize = myuser.getSize();
        Long maxSize = myuser.getMax_size();
        if (size + currentSize > maxSize) {
            return customInfoProperties.getErrorHeader()+":"+"空间不足";
        }

        //更新用户信息
        myuser.setSize(currentSize + size);
        if (this.userMapper.updateByPrimaryKey(myuser) != 1) {
            throw new RuntimeException("更新用户数据失败!");
        }

        //上传文件至STORAGE
        String extension = StringUtils.substringAfterLast(file_info.getName(), ".");
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), size, extension, null);
        if (storePath == null) {
            throw new RuntimeException("上传至文件服务器失败!");
        }

        //插入数据库
        file_info.setId(null);
        file_info.setUpdate_time(new Date());
        file_info.setType(extension);
        file_info.setSize(size);
        file_info.setAddress(storePath.getFullPath());
        if (this.fileMapper.insertSelective(file_info) != 1) {
            storageClient.deleteFile(storePath.getFullPath());
            throw new RuntimeException("插入文件数据库失败!");
        }

        return storePath.getFullPath();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFile(Long file_id, Long user_id) throws Exception {
        //查询数据库获取文件实际地址和文件大小
        File record_f = new File();
        record_f.setId(file_id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return false;
        }
        String address = myfile.getAddress();
        Long size = myfile.getSize();

        //删除版本表中可能存在的历史文件
        Version record_v = new Version();
        record_v.setFile_id(file_id);
        List<Version> versions = this.versionMapper.select(record_v);
        if (versions != null && !versions.isEmpty()) {
            if (this.versionMapper.delete(record_v) < 1) {
                throw new RuntimeException("删除历史版本文件失败!");
            }
        }

        //删除分享表中可能存在的分享文件 ** share操作
        Share record_s = new Share();
        record_s.setId(file_id);
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare != null) {
            if (this.shareMapper.delete(record_s) != 1) {
                throw new RuntimeException("删除分享文件失败!");
            }
        }

        //更新用户信息
        User record_u = new User();
        record_u.setId(user_id);
        User myuser = this.userMapper.selectOne(record_u);
        if (myuser == null) {
            throw new RuntimeException("用户信息获取失败!");
        }
        myuser.setSize(myuser.getSize() - size);
        if (this.userMapper.updateByPrimaryKey(myuser) != 1) {
            throw new RuntimeException("用户信息更新失败!");
        }

        //删除文件表中的文件节点
        if (this.fileMapper.delete(record_f) != 1) {
            throw new RuntimeException("删除文件节点失败!");
        }

        //删除STORAGE文件
        storageClient.deleteFile(address);          //删除失败自动抛出异常触发回滚
        delteVerFile(versions);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDir(Long dir_id, Long user_id) throws Exception {
        //检查目录是否存在
        Dir record_e = new Dir();
        record_e.setId(dir_id);
        record_e.setUser_id(user_id);
        if (this.dirMapper.selectOne(record_e) == null) {
            return false;
        }

        //查看是否有子目录，有则先删除子目录
        Dir record_d = new Dir();
        record_d.setPid(dir_id);
        List<Dir> mydirs = this.dirMapper.select(record_d);
        if (mydirs != null && !mydirs.isEmpty()) {
            for (Dir mydir : mydirs) {
                deleteDir(mydir.getId(), user_id);
            }
        }

        //查看是否有子文件，有则先删除子文件
        File record_f = new File();
        record_f.setPid(dir_id);
        List<File> myfiles = this.fileMapper.select(record_f);
        if (myfiles != null && !myfiles.isEmpty()) {
            for (File myfile : myfiles) {
                deleteFile(myfile.getId(), user_id);
            }
        }

        //删除目录
        if (this.dirMapper.delete(record_e) != 1) {
            throw new RuntimeException("删除目录失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateFile(Long id, MultipartFile file, Long user_id) throws Exception {
        //检查原文件是否存在
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return customInfoProperties.getErrorHeader() + ":" + "原文件不存在";
        }
        Long size_old = myfile.getSize();

        //检查文件是否为空
        if (file == null || file.getSize() == 0) {
            return customInfoProperties.getErrorHeader() + ":" + "上传文件不能为空";
        }

        //检查空间大小
        User record_u = new User();
        record_u.setId(myfile.getUser_id());
        User myuser = this.userMapper.selectOne(record_u);
        if (myuser != null) {
            //检查空间是否足够
            Long currentSize = myuser.getSize() - size_old + file.getSize();
            if (currentSize > myuser.getMax_size()) {
                return customInfoProperties.getErrorHeader() + ":" + "空间大小不足";
            }
        } else {
            throw new RuntimeException("获取用户信息失败!");
        }

        //备份历史文件
        Version myVersion = new Version();
        myVersion.setId(null);
        myVersion.setFile_id(myfile.getId());
        myVersion.setAddress(myfile.getAddress());
        myVersion.setName(myfile.getName());
        myVersion.setUpdate_time(myfile.getUpdate_time());
        myVersion.setUser_id(myfile.getUser_id());
        //检查历史文件数是否达到最大，若超过最大数量，则删除最早的版本
        List<Version> versions = this.versionMapper.selectByFileIdDESC(id);
        List<Version> deleteverlist = new ArrayList<>();
        while (versions.size() > Integer.parseInt(userProperties.getMax_version_num()) - 1) {
            if (this.versionMapper.delete(versions.get(versions.size() - 1)) != 1) {
                throw new RuntimeException("删除版本文件出错!");
            }
            //storageClient.deleteFile(versions.get(versions.size() - 1).getAddress());
            deleteverlist.add(versions.get(versions.size() - 1));
            versions.remove(versions.size() - 1);
        }
        //插入数据库
        if (this.versionMapper.insertSelective(myVersion) != 1) {
            throw new RuntimeException("备份历史文件出错!");
        }


        //上传更新文件至STORAGE
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), myfile.getType(), null);
        if (storePath == null) {
            throw new RuntimeException("上传至文件服务器失败!");
        }

        //更新file表
        myfile.setAddress(storePath.getFullPath());
        myfile.setUpdate_time(new Date());
        myfile.setSize(file.getSize());
        if (this.fileMapper.updateByPrimaryKey(myfile) != 1) {
            storageClient.deleteFile(storePath.getFullPath());
            throw new RuntimeException("更新file表失败!");
        }

        //更新share表 ** share操作
        Share record_s = new Share();
        record_s.setId(id);
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare != null) {
            myshare.setAddress(storePath.getFullPath());
            if (this.shareMapper.updateByPrimaryKey(myshare) != 1) {
                storageClient.deleteFile(storePath.getFullPath());
                throw new RuntimeException("更新share表失败!");
            }
        }

        //更新user表
        myuser.setSize(myuser.getSize() - size_old + file.getSize());
        if (this.userMapper.updateByPrimaryKey(myuser) != 1) {
            storageClient.deleteFile(storePath.getFullPath());
            throw new RuntimeException("更新user表失败!");
        }

        delteVerFile(deleteverlist);
        return storePath.getFullPath();
    }

    @Transactional(rollbackFor = Exception.class)
    public String copyFile(Long id, Long pid_new, Long user_id) throws Exception {
        //查询原文件
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return customInfoProperties.getErrorHeader() + ":" + "原文件不存在";
        }
        String address = myfile.getAddress();

        //获取文件流
        String group = address.substring(0, address.indexOf("/"));
        String path = address.substring(address.indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = this.storageClient.downloadFile(group, path, downloadByteArray);
        MyMultipartFile file = new MyMultipartFile(bytes);

        //重新上传文件
        File file_info = new File();
        file_info.setName(myfile.getName());
        file_info.setUser_id(myfile.getUser_id());
        file_info.setPid(pid_new);
        return uploadFile(file_info, file);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean renameFile(Long id, String name_new, Long user_id) throws Exception {
        //查询原文件
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return false;
        }

        //检查后缀名是否更改
        String extension = StringUtils.substringAfterLast(name_new, ".");
        if (!extension.equals(myfile.getType())) {
            return false;
        }

        //检查同目录下是否有同名文件
        File record_n = new File();
        record_n.setName(name_new);
        record_n.setPid(myfile.getPid());
        record_n.setUser_id(myfile.getUser_id());
        File samename = this.fileMapper.selectOne(record_n);
        if (!name_new.equals(myfile.getName())) {
            if (samename != null) {
                return false;
            }
        }

        //更新file表
        myfile.setName(name_new);
        if (this.fileMapper.updateByPrimaryKey(myfile) != 1) {
            throw new RuntimeException("更新file表失败!");
        }

        //更新share表
        Share record_s = new Share();
        record_s.setId(myfile.getId());
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare != null) {
            myshare.setName(name_new);
            if (this.shareMapper.updateByPrimaryKey(myshare) != 1) {
                throw new RuntimeException("更新share表失败!");
            }
        }

        return true;
    }

    public Boolean moveFile(Long id, Long pid_new, Long user_id) throws Exception {
        //查询原文件
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return false;
        }

        //查询目录
        if (pid_new != 0) {
            Dir record_n = new Dir();
            record_n.setId(pid_new);
            record_n.setUser_id(myfile.getUser_id());
            Dir father = this.dirMapper.selectOne(record_n);
            if (father == null) {
                return false;
            }
        }

        //查询目录下是否含同名文件
        File record_n = new File();
        record_n.setName(myfile.getName());
        record_n.setPid(pid_new);
        record_n.setUser_id(myfile.getUser_id());
        File samename = this.fileMapper.selectOne(record_n);
        if (samename != null) {
            if (pid_new != myfile.getPid()) {
                return false;
            }
        }

        //更新file表
        myfile.setPid(pid_new);
        if (this.fileMapper.updateByPrimaryKey(myfile) != 1) {
            throw new RuntimeException("更新file表失败!");
        }

        return true;
    }

    public Boolean renameDir(Long id, String name_new, Long user_id) throws RuntimeException {
        //检查目录是否存在
        Dir record_d = new Dir();
        record_d.setId(id);
        record_d.setUser_id(user_id);
        Dir myDir = this.dirMapper.selectOne(record_d);
        if (myDir == null) {
            return false;
        }

        //检查是否有同名目录
        Dir record_n = new Dir();
        record_n.setName(name_new);
        record_n.setUser_id(myDir.getUser_id());
        record_n.setPid(myDir.getPid());
        Dir samename = this.dirMapper.selectOne(record_n);
        if (!name_new.equals(myDir.getName())) {
            if (samename != null) {
                return false;
            }
        }

        //更新dir表
        myDir.setName(name_new);
        if (this.dirMapper.updateByPrimaryKey(myDir) != 1) {
            throw new RuntimeException("更新dir表失败!");
        }

        return true;
    }

    public Boolean moveDir(Long id, Long pid_new, Long user_id) throws RuntimeException {
        //检查目标目录是否为自身
        if (id == pid_new) {
            return false;
        }

        //查询原目录
        Dir record_d = new Dir();
        record_d.setId(id);
        record_d.setUser_id(user_id);
        Dir mydir = this.dirMapper.selectOne(record_d);
        if (mydir == null) {
            return false;
        }

        //查询目标目录
        if (pid_new != 0) {
            Dir record_p = new Dir();
            record_p.setId(pid_new);
            record_p.setUser_id(mydir.getUser_id());
            Dir father = this.dirMapper.selectOne(record_p);
            if (father == null) {
                logger.info("目标目录不存在");
                return false;
            }
        }

        //查询目标目录下是否有同名目录
        Dir record_n = new Dir();
        record_n.setName(mydir.getName());
        record_n.setPid(pid_new);
        record_n.setUser_id(mydir.getUser_id());
        Dir samename = this.dirMapper.selectOne(record_n);
        if (samename != null) {
            if (pid_new != mydir.getPid()) {
                logger.info("存在同名目录");
                return false;
            }
        }

        //更新dir表
        mydir.setPid(pid_new);
        if (this.dirMapper.updateByPrimaryKey(mydir) != 1) {
            throw new RuntimeException("更新dir表失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<String> copyDir(Long id, Long pid_new, Long user_id) throws Exception {
        List<String> address_all = new ArrayList<>();

        //查询原目录
        Dir record_d = new Dir();
        record_d.setId(id);
        record_d.setUser_id(user_id);
        Dir mydir = this.dirMapper.selectOne(record_d);
        if (mydir == null) {
            return null;
        }

        //查询目标目录
        if (pid_new != 0) {
            Dir record_p = new Dir();
            record_p.setId(pid_new);
            record_p.setUser_id(mydir.getUser_id());
            Dir father = this.dirMapper.selectOne(record_p);
            if (father == null) {
                return null;
            }
        }

        //查询目标目录是否包含同名目录
        Dir record_n = new Dir();
        record_n.setName(mydir.getName());
        record_n.setPid(pid_new);
        record_n.setUser_id(mydir.getUser_id());
        Dir samename = this.dirMapper.selectOne(record_n);
        if (samename != null) {
            return null;
        }

        //检查原目录下是否有子目录，有则一同复制
        Dir record_dc = new Dir();
        record_dc.setPid(id);
        List<Dir> dirs = this.dirMapper.select(record_dc);

        //在新目录下创建目录
        Dir dir_new = new Dir();
        dir_new.setId(null);
        dir_new.setUser_id(mydir.getUser_id());
        dir_new.setPid(pid_new);
        dir_new.setName(mydir.getName());
        dir_new.setCreate_time(new Date());
        if (this.dirMapper.insertSelective(dir_new) != 1) {
            throw new RuntimeException("创建目录失败!");
        }

        //获取新创建的目录
        Dir record_dp = new Dir();
        record_dp.setUser_id(user_id);
        record_dp.setName(mydir.getName());
        record_dp.setPid(pid_new);
        Dir dir_p = this.dirMapper.selectOne(record_dp);
        if (dir_p == null) {
            throw new RuntimeException("查找目录失败!");
        }

        //执行复制操作
        if (dirs != null && !dirs.isEmpty()) {
            for (Dir dir : dirs) {
                address_all.addAll(copyDir(dir.getId(), dir_p.getId(), user_id));
            }
        }

        //检查原目录下是否有子文件，有则一同复制
        File record_f = new File();
        record_f.setPid(id);
        List<File> myfiles = this.fileMapper.select(record_f);
        if (myfiles != null && !myfiles.isEmpty()) {
            for (File myfile : myfiles) {
                address_all.add(copyFile(myfile.getId(), dir_p.getId(), user_id));
            }
        }

        return address_all;
    }

    public String getAddress(Long id, Long user_id) {
        //查询文件
        File record_f = new File();
        record_f.setId(id);
        record_f.setUser_id(user_id);
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return null;
        }

        return myfile.getAddress();
    }

    private void delteVerFile(List<Version> vers) {
        try {
            if (vers != null && !vers.isEmpty()) {
                for (Version ver : vers) {
                    storageClient.deleteFile(ver.getAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
