package com.cmd.wallet.common.mapper;


import com.cmd.wallet.common.model.TSearchHistoryModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-13 10:41:10
 */
@Mapper
public interface SearchHistoryMapper {
    TSearchHistoryModel queryObject(@Param("value")Integer id);

    List<TSearchHistoryModel> queryList(@Param("userId")Integer userId);

    int queryTotal(@Param("userId")Integer userId);

    void save(TSearchHistoryModel searchHistory);

    void update(TSearchHistoryModel searchHistory);

    void delete(@Param("value") Integer id);

    void deleteBatch(Integer[] ids);
	
}
