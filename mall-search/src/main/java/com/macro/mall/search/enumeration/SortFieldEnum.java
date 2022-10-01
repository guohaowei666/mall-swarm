package com.macro.mall.search.enumeration;

import lombok.AllArgsConstructor;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @title: SortFieldEnum
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/14 0014 7:03
 * @Version 1.0
 */
@AllArgsConstructor
public enum SortFieldEnum {
    /**
     * 替代魔法值，减少过多的if-elseif语句
     */
    NEW_PRODUCT("按新品从新到旧",1,"id", SortOrder.DESC),
    SALES_VOLUME("按销量从高到低",2,"sale",SortOrder.DESC),
    RISE_IN_PRICE("按价格从低到高",3,"price",SortOrder.ASC),
    DIP_IN_PRICE("按价格从高到低",4,"price",SortOrder.DESC),
    ;
    /**
     * 排序字段说明
     */
    private String desc;
    /**
     * 排序字段编码（前后端交互）
     */
    private Integer code;
    /**
     * 排序字段名
     */
    private String fieldName;
    /**
     * 排序规则
     */
    private SortOrder sortOrder;

    public static String getFieldNameByCode(Integer code){
        for (SortFieldEnum value : SortFieldEnum.values()) {
            if(value.code.equals(code)){
                return value.fieldName;
            }
        }
        return "";
    }
    public static SortOrder getSortOrderByCode(Integer code){
        for (SortFieldEnum value : SortFieldEnum.values()) {
            if(value.code.equals(code)){
                return value.sortOrder;
            }
        }
        return SortOrder.ASC;
    }
}
