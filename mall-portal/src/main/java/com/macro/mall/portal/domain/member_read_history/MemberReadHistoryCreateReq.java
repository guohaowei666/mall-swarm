package com.macro.mall.portal.domain.member_read_history;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @title: MemberReadHistoryReq
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/20 0020 7:09
 * @Version 1.0
 */
@Data
public class MemberReadHistoryCreateReq implements Serializable {
    @ApiModelProperty("商品id")
    private Long productId;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品图片链接")
    private String productPic;
    @ApiModelProperty("商品副标题")
    private String productSubTitle;
    @ApiModelProperty("商品价格")
    private String productPrice;
}
