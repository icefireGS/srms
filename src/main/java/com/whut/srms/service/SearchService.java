package com.whut.srms.service;

import com.whut.srms.mapper.DirMapper;
import com.whut.srms.mapper.FileMapper;
import com.whut.srms.mapper.SharesRepository;
import com.whut.srms.pojo.*;
import com.whut.srms.property.UrlProperties;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private DirMapper dirMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UrlProperties urlProperties;

    @Autowired
    private SharesRepository sharesRepository;

    public List<fileListNode> searchFile(String key, Long user_id) {
        //搜索目录
        List<Dir> mydirs = this.dirMapper.fuzzyByNameUser(key, user_id);
        //搜索文件
        List<File> myfiles = this.fileMapper.fuzzyByNameUser(key, user_id);

        List<fileListNode> result = new ArrayList<>();
        //封装
        if (mydirs != null) {
            for (Dir mydir : mydirs) {
                fileListNode node = new fileListNode();
                node.setId(mydir.getId());
                node.setName(mydir.getName());
                node.setIsfile(0);
                node.setType(null);
                node.setSize(null);
                node.setUrl(null);
                node.setTime(mydir.getCreate_time());
                result.add(node);
            }
        }
        if (myfiles != null) {
            for (File myfile : myfiles) {
                fileListNode node = new fileListNode();
                node.setId(myfile.getId());
                node.setName(myfile.getName());
                node.setTime(myfile.getUpdate_time());
                node.setSize(myfile.getSize());
                node.setType(myfile.getType());
                node.setIsfile(1);
                String address = myfile.getAddress();
                if (address.startsWith(urlProperties.getTracker1_group_name())) {
                    node.setUrl("http://"+urlProperties.getTracker1()+":8888/"+address);
                } else if (address.startsWith(urlProperties.getTracker2_group_name())) {
                    node.setUrl("http://" + urlProperties.getTracker2() + ":8888/" + address);
                } else {
                    node.setUrl(null);
                }
                result.add(node);
            }
        }

        return result;
    }

    public searchResult searchShare(searchRequest request) {
        Integer page = request.getPage() - 1;// page 从0开始
        Integer size = request.getSize();

        String key = request.getKey();

        if (key == null) {
            return null;
        }

        //查询构建工具
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加了查询的过滤，只要这些字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","name","type","share_time"},null));

        //获取基本的查询条件
        QueryBuilder basicQuery = buildBasicQueryWithFilter(request);

        //把查询条件添加到构建器中（这里仅仅是我的查询条件）
        queryBuilder.withQuery(basicQuery);

        //把分页条件条件到构建器中
        queryBuilder.withPageable(PageRequest.of(page,size));

        AggregatedPage<Shares> sharesResult = (AggregatedPage<Shares>) sharesRepository.search(queryBuilder.build());

        Long total = sharesResult.getTotalElements();
        int totalPages = (total.intValue() + size - 1) / size;

        List<Shares> myshares = sharesResult.getContent();
        List<ShareListNode> itmes = new ArrayList<>();
        for (Shares myshare : myshares) {
            ShareListNode node = new ShareListNode();
            node.setId(myshare.getId());
            node.setType(myshare.getType());
            node.setShare_type(null);
            node.setName(myshare.getName());
            node.setShare_time(myshare.getShare_time());
            itmes.add(node);
        }

        return new searchResult(total, totalPages, itmes);
    }

    //这个方法用来构建查询条件以及过滤条件
    private QueryBuilder buildBasicQueryWithFilter(searchRequest request) {
        //构造布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.matchQuery("name",request.getKey()));

        //给这个查询加过滤
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();

        //过滤文件类型
        if (request.getType() != null && !request.getType().equals("不限")) {
            filterQueryBuilder.must(QueryBuilders.termQuery("type", request.getType()));
        }

        //过滤时间
        if (request.getTime() != null && !request.getTime().equals("不限")) {
            Calendar c = Calendar.getInstance();
            Date now = new Date();
            switch(request.getTime()) {
                case "一周内":
                    c.setTime(now);
                    c.add(Calendar.DATE, - 7);
                    filterQueryBuilder.must(QueryBuilders.rangeQuery("share_time").gte(c.getTime().getTime()).lt(now.getTime()));
                    break;
                case "一个月内":
                    c.setTime(now);
                    c.add(Calendar.MONTH, -1);
                    filterQueryBuilder.must(QueryBuilders.rangeQuery("share_time").gte(c.getTime().getTime()).lt(now.getTime()));
                    break;
                case "一年内":
                    c.setTime(now);
                    c.add(Calendar.YEAR, -1);
                    filterQueryBuilder.must(QueryBuilders.rangeQuery("share_time").gte(c.getTime().getTime()).lt(now.getTime()));
                    break;
                default:
                    break;
            }
        }

        //过滤学科
        if (request.getSubject() != null && !request.getSubject().equals("不限")) {
            filterQueryBuilder.must(QueryBuilders.termQuery("subject", request.getSubject()));
        }

        //过滤学段
        if (request.getPeriod() != null && !request.getPeriod().equals("不限")) {
            filterQueryBuilder.must(QueryBuilders.termQuery("period", request.getPeriod()));
        }

        //过滤学校
        if (request.getSchool() != null && !request.getSchool().equals("不限")) {
            filterQueryBuilder.must(QueryBuilders.termQuery("school", request.getSchool()));
        }


        return queryBuilder.filter(filterQueryBuilder);
    }
}
