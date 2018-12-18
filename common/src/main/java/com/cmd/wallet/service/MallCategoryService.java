package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.MallCategoryMapper;
import com.cmd.wallet.common.model.MallCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MallCategoryService {

    @Autowired
    private MallCategoryMapper mallCategoryMapper;


    public List<MallCategory> getAllMallCategory() {
        return mallCategoryMapper.getAll();
    }
}
