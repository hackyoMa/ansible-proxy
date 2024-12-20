package com.github.ansibleproxy.ansible.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.File;

/**
 * 复制文件参数
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class CopyRequest extends BaseRequest {

    /**
     * 复制到远程的绝对路径
     */
    private String dest;
    /**
     * 文件的权限
     */
    private String mode;
    /**
     * 文件的所属组
     */
    private String group;
    /**
     * 文件的所属用户
     */
    private String owner;

    @JsonIgnore
    private File copyFile;

}
