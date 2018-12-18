package com.cmd.wallet.service;


import com.cmd.wallet.common.mapper.SearchHistoryMapper;
import com.cmd.wallet.common.model.TSearchHistoryModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * 
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-13 10:41:10
 */
@Service
public class SearchHistoryService {
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    public TSearchHistoryModel queryObject( Integer id){
        return searchHistoryMapper.queryObject(id);
    }

    public List<TSearchHistoryModel> queryList(Integer userId){
        return searchHistoryMapper.queryList(userId);
    }

    public int queryTotal(Integer userId){
        return searchHistoryMapper.queryTotal(userId);
    }

    public void save(TSearchHistoryModel searchHistory){
        searchHistoryMapper.save(searchHistory);
    }

    public void update(TSearchHistoryModel searchHistory){
        searchHistoryMapper.update(searchHistory);
    }

    public void delete( Integer id){
        searchHistoryMapper.delete(id);
    }

    public void deleteBatch(Integer[] ids){
        searchHistoryMapper.deleteBatch(ids);
    }
	
}
