package com.whut.srms.mapper;

import com.whut.srms.pojo.Shares;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SharesRepository extends ElasticsearchRepository<Shares, Long> {
}
