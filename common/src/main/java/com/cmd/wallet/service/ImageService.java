package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.ImageMapper;
import com.cmd.wallet.common.model.TImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    @Autowired
    private ImageMapper imageMapper;

    public int addImages(Integer refId, Integer type,List<String> imgUrls){
        List<TImageModel> imageModels = new ArrayList<>();
        for (int i = 0; i < imgUrls.size(); i++) {
            String url = imgUrls.get(i);
            TImageModel tImageModel = new TImageModel().setRefrenceId(refId).setImgUrl(url).setType(type);
            imageModels.add(tImageModel);
        }
        return imageMapper.addImages(imageModels);
    }
    @Transactional
    public int updateImages(Integer refId,Integer type,List<String> imgUrls){
        imageMapper.deleteImgByRefIdAndType(refId, type);
        return addImages(refId,type,imgUrls);
    }

    public List<TImageModel> getImgByRefIdAndType(Integer id, Integer type) {
        return imageMapper.getImgByRefIdAndType(id,type);
    }
}
