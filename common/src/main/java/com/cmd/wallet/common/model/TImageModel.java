package com.cmd.wallet.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TImageModel {
    private Long id;
    private Integer refrenceId;
    private String imgUrl;
    private Date createTime;
    private Integer type;
}
