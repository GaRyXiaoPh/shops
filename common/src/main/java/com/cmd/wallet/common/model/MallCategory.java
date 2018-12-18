package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel

public class MallCategory  {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @ApiModelProperty("分类ID")
    private Integer id;
    /**
     * 分类名称
     */
    @ApiModelProperty("分类名称")
    private String categoryName;
    /**
     * 分类英文
     */
    @ApiModelProperty("分类英文")
    private String categoryNameEng;
    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private Integer order;
}
