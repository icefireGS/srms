package com.whut.srms.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "shares", type = "docs", shards = 1, replicas = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shares {
    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Date)
    private Date share_time;
    @Field(type = FieldType.Keyword)
    private String subject;
    @Field(type = FieldType.Keyword)
    private String period;
    @Field(type = FieldType.Keyword)
    private String school;
}
