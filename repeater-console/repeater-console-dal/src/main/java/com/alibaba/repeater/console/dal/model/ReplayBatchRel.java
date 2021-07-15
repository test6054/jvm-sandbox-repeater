package com.alibaba.repeater.console.dal.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @类描述：批量回放关联表
 * @创建人：zhiang
 * @创建时间：2021/7/14 10:29
 * @version：V1.0
 */
@Entity
@Table(name = "t_replay_batch_rel")
@Data
public class ReplayBatchRel implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "repeat_id")
    private Long repeatId;

    private String message;

    private String data;

}
