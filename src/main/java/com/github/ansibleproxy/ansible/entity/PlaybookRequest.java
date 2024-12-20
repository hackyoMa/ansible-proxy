package com.github.ansibleproxy.ansible.entity;

import lombok.Data;

/**
 * Playbook执行参数
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class PlaybookRequest extends BaseRequest {

    /**
     * 执行的Playbook脚本
     */
    private String playbook;

}
