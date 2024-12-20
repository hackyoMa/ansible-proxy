package com.github.ansibleproxy.ansible.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ansibleproxy.common.CredentialType;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 基础执行参数
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class BaseRequest implements Serializable {

    /**
     * 主机访问地址
     */
    private String host;
    /**
     * 主机访问端口
     */
    private Integer port;
    /**
     * 主机登录用户
     */
    private String user;
    /**
     * 主机登录认证类型
     */
    private CredentialType credentialType;
    /**
     * 主机登录私钥
     */
    private String privateKey;
    /**
     * 主机登录密码
     */
    private String password;
    /**
     * 是否需要提权
     */
    private Boolean become;
    /**
     * 是否为Windows主机
     */
    private Boolean isWindows;
    /**
     * 任务执行超时时间(秒)
     */
    private Integer timeout;
    /**
     * 访问代理配置
     */
    private BaseRequest proxy;

    @JsonIgnore
    private String taskId;
    @JsonIgnore
    private File taskFolder;
    @JsonIgnore
    private File privateKeyFile;
    @JsonIgnore
    private File inventoryFile;
    @JsonIgnore
    private File playbookFile;

}
