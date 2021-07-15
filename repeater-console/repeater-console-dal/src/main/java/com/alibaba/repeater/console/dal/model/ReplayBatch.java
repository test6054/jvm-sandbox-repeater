package com.alibaba.repeater.console.dal.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @类描述：批量回放信息
 * @创建人：zhiang
 * @创建时间：2021/7/14 10:29
 * @version：V1.0
 */
@Entity
@Table(name = "t_replay_batch")
@Data
public class ReplayBatch implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    private String ip;

    @Column(name = "app_name")
    private String appName;

    private String environment;

    @Column(name = "batch_repeat_id")
    private String batchRepeatId;

    private Integer total;

    private Integer status;

    private Long cost;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;
}
