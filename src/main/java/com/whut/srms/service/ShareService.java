package com.whut.srms.service;

import com.whut.srms.mapper.FileMapper;
import com.whut.srms.mapper.ShareMapper;
import com.whut.srms.mapper.SharesRepository;
import com.whut.srms.mapper.UserMapper;
import com.whut.srms.pojo.*;
import com.whut.srms.property.UrlProperties;
import com.whut.srms.utils.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private SharesRepository sharesRepository;

    @Autowired
    private UrlProperties urlProperties;

    @Autowired
    private UserMapper userMapper;

    public List<ShareListNode> queryShare(Long user_id) {
        Share record_s = new Share();
        record_s.setUser_id(user_id);
        List<Share> myshares = this.shareMapper.select(record_s);
        List<ShareListNode> result = new ArrayList<>();

        for (Share myshare : myshares) {
            ShareListNode node = new ShareListNode();
            node.setId(myshare.getId());
            node.setName(myshare.getName());
            node.setShare_time(myshare.getShare_time());
            node.setShare_type(myshare.getShare_type());
            node.setType(myshare.getType());
            result.add(node);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean createPublicShare(Share pubShare) throws Exception {
        //查询原文件
        File record_f = new File();
        record_f.setId(pubShare.getId());
        record_f.setUser_id(pubShare.getUser_id());
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return false;
        }

        //查询是否存在分享
        Share record_s = new Share();
        record_s.setId(pubShare.getId());
        if (this.shareMapper.selectOne(record_s) != null) {
            return false;
        }

        //插入数据库
        pubShare.setName(myfile.getName());
        pubShare.setType(myfile.getType());
        pubShare.setAddress(myfile.getAddress());
        pubShare.setShare_type(0);
        pubShare.setCode(null);
        pubShare.setShare_time(new Date());
        if (this.shareMapper.insert(pubShare) != 1) {
            throw new RuntimeException("插入数据库失败!");
        }

        //创建es索引
        Shares shares = new Shares();
        shares.setId(pubShare.getId());
        shares.setName(pubShare.getName());
        shares.setType(pubShare.getType());
        shares.setShare_time(pubShare.getShare_time());
        shares.setSubject(pubShare.getSubject());
        shares.setPeriod(pubShare.getPeriod());
        shares.setSchool(pubShare.getSchool());
        this.sharesRepository.save(shares);

        return true;
    }

    public String createPrivateShare(Share priShare) {
        //查询原文件
        File record_f = new File();
        record_f.setId(priShare.getId());
        record_f.setUser_id(priShare.getUser_id());
        File myfile = this.fileMapper.selectOne(record_f);
        if (myfile == null) {
            return null;
        }

        //查询是否存在分享
        Share record_s = new Share();
        record_s.setId(priShare.getId());
        if (this.shareMapper.selectOne(record_s) != null) {
            return null;
        }

        //生成唯一8位分享码
        String code = CodeUtils.generateShortUuid();
        Share record_c = new Share();
        record_c.setCode(code);
        while (this.shareMapper.selectOne(record_c) != null) {
            code = CodeUtils.generateShortUuid();
            record_c.setCode(code);
        }

        //插入数据库
        priShare.setCode(code);
        priShare.setShare_time(new Date());
        priShare.setShare_type(1);
        priShare.setAddress(myfile.getAddress());
        priShare.setName(myfile.getName());
        priShare.setType(myfile.getType());
        priShare.setPeriod(null);
        priShare.setSubject(null);
        priShare.setSchool(null);
        if (this.shareMapper.insert(priShare) != 1) {
            throw new RuntimeException("插入数据库失败!");
        }

        return code;
    }

    public ShareInfo getShareInfo(Long id, Long user_id) {
        //查询分享文件
        Share record_s = new Share();
        record_s.setId(id);
        record_s.setUser_id(user_id);
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare == null) {
            if (this.sharesRepository.existsById(id)) {
                this.sharesRepository.deleteById(id);
            }
            return null;
        }

        //封装
        ShareInfo info = new ShareInfo();
        info.setId(myshare.getId());
        info.setName(myshare.getName());
        info.setCode(myshare.getCode());
        info.setType(myshare.getType());
        info.setPeriod(myshare.getPeriod());
        info.setSchool(myshare.getSchool());
        info.setShare_time(myshare.getShare_time());
        info.setShare_type(myshare.getShare_type());
        info.setSubject(myshare.getSubject());
        //设置url
        String address = myshare.getAddress();
        if (address.startsWith(urlProperties.getTracker1_group_name())) {
            info.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
        } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
            info.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
        } else {
            info.setUrl(null);
        }
        //设置用户名
        User record_u = new User();
        record_u.setId(user_id);
        User myuser = this.userMapper.selectOne(record_u);
        if (myuser == null) {
            return null;
        }
        info.setUsername(myuser.getUsername());

        return info;
    }

    public ShareInfo extractShare(String code) {
        //查询分享文件
        Share record_s = new Share();
        record_s.setCode(code);
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare == null) {
            return null;
        }

        //封装
        ShareInfo info = new ShareInfo();
        info.setId(myshare.getId());
        info.setName(myshare.getName());
        info.setCode(myshare.getCode());
        info.setType(myshare.getType());
        info.setPeriod(null);
        info.setSchool(null);
        info.setShare_time(myshare.getShare_time());
        info.setShare_type(myshare.getShare_type());
        info.setSubject(null);
        //设置url
        String address = myshare.getAddress();
        if (address.startsWith(urlProperties.getTracker1_group_name())) {
            info.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
        } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
            info.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
        } else {
            info.setUrl(null);
        }
        //设置用户名
        User record_u = new User();
        record_u.setId(myshare.getUser_id());
        User myuser = this.userMapper.selectOne(record_u);
        if (myuser == null) {
            return null;
        }
        info.setUsername(myuser.getUsername());

        return info;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShare(Long id, Long user_id) throws Exception {
        //查询分享文件
        Share record_s = new Share();
        record_s.setId(id);
        record_s.setUser_id(user_id);
        Share myshare = this.shareMapper.selectOne(record_s);
        if (myshare == null) {
            return false;
        }

        //删除数据库记录
        if (this.shareMapper.delete(myshare) != 1) {
            throw new RuntimeException("删除分享失败!");
        }

        //公开分享删除es库记录
        if (myshare.getShare_type() == 0) {
            if (this.sharesRepository.existsById(id)) {
                this.sharesRepository.deleteById(id);
            }
        }

        return true;
    }
}
