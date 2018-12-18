package com.cmd.wallet.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserRelationVO {
    private Integer id;
    private String mobile;
    private String email;
    private String userName;
    private String nickName;
    private Integer leftChild;
    private Integer rightChild;
    private UserRelationVO leftChildNode;
    private UserRelationVO rightChildNode;
}
