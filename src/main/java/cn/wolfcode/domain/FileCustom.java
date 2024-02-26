package cn.wolfcode.domain;

import lombok.Data;

@Data
public class FileCustom {

    //唯⼀标识
    private Long id;
    //⽂件名
    private String name;
    //⽂件类型
    private String type;
    //⽂件内容
    private String content;
    //是否已备份
    private Boolean backedUp = false;
    public FileCustom(){}
    public FileCustom(Long id, String name, String type, String content){
        this.id = id;
        this.name = name;
        this.type = type;
        this.content = content;
    }
}
