package com.github.ansibleproxy.ansible.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.File;

/**
 * 脚本执行参数
 *
 * @author hackyo
 * @since 2022/4/1
 */
@Data
public class ScriptRequest extends BaseRequest {

    /**
     * 执行的脚本
     */
    private String script;
    /**
     * 执行的命令行参数
     */
    private String args;
    /**
     * 执行脚本的可执行文件名称或路径
     */
    private String executable;
    /**
     * 执行脚本的文件后缀
     */
    private String scriptExt;

    @JsonIgnore
    private File scriptFile;

}
