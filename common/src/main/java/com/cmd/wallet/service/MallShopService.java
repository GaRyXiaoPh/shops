package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.enums.ImageType;
import com.cmd.wallet.common.mapper.MallAddressMapper;
import com.cmd.wallet.common.mapper.MallApplyMapper;
import com.cmd.wallet.common.mapper.MallShopMapper;
import com.cmd.wallet.common.mapper.UserMapper;
import com.cmd.wallet.common.model.MallApply;
import com.cmd.wallet.common.model.MallShop;
import com.cmd.wallet.common.model.TImageModel;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.Assert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class MallShopService {

    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    private MallApplyMapper mallApplyMapper;
    @Autowired
    private ImageService imageService;
    public MallShop getMallShopByUserId(Integer userId){
        return mallShopMapper.getMallShopByUserId(userId);
    }

    public void editShop(MallShop mallShop) {
        MallShop mallShopDb = mallShopMapper.getMallShopByUserId(mallShop.getUserId());
        if(mallShopDb == null){
            int i = mallShopMapper.addMallShop(mallShop);
            Assert.check(i != 1, ErrorCode.ERR_RECORD_DATA_ERROR);
        }else{
            int i = mallShopMapper.updateMallShop(mallShop);
            Assert.check(i != 1, ErrorCode.ERR_RECORD_UPDATE);
        }
    }

    /**
     * 
     * @param user
     * @param mallApply
     */
    @Transactional
    public void applyShop(User user,MallApply mallApply) {

        MallApply mallApplyDb = mallApplyMapper.findByUserId(user.getId());
        if(mallApplyDb == null){
            mallApplyDb = mallApply;
            mallApplyDb.setStatus(0);
            mallApplyDb.setUserId(user.getId());
            mallApplyMapper.addMallApply(mallApplyDb);
        }else if(mallApplyDb.getStatus() == 1){
            mallApplyDb.setStatus(0).setBusLicense(mallApply.getBusLicense()).setShopPhotos(mallApply.getShopPhotos())
                    .setContacts(mallApply.getContacts()).setPhone(mallApply.getPhone());
            mallApplyMapper.update(mallApplyDb);
        }
        imageService.updateImages(mallApplyDb.getId(),ImageType.BUS_LICENSE.getValue(),Collections.singletonList(mallApplyDb.getBusLicense()));
        imageService.updateImages(mallApplyDb.getId(),ImageType.SHOP_PHOTO.getValue(),mallApplyDb.getShopPhotos());
    }

    public MallApply getMallApply(Integer id) {
        MallApply byUserId = mallApplyMapper.findById(id);
        List<TImageModel> busLicense = imageService.getImgByRefIdAndType(byUserId.getId(), ImageType.BUS_LICENSE.getValue());
        List<TImageModel> shopPhoto = imageService.getImgByRefIdAndType(byUserId.getId(), ImageType.SHOP_PHOTO.getValue());
        if(busLicense != null && !busLicense.isEmpty()){
            byUserId.setBusLicense(busLicense.get(0).getImgUrl());
        }
        if(shopPhoto != null){
            List<String> str = new ArrayList<>();
            for (TImageModel photo:
            shopPhoto) {
                String imgUrl = photo.getImgUrl();
                str.add(imgUrl);
            }
            byUserId.setShopPhotos(str);
        }
        return byUserId;
    }

//    public String init() {
//        List<User> users = userMapper.getSellerUserList();
//        int count = 0;
//        for (int i = 0; i < users.size(); i++) {
//            User user = users.get(i);
//            Integer id = user.getId();
//            MallShop mallShop = mallShopMapper.getMallShopByUserId(id);
//            if(mallShop == null){
//                mallShop = new MallShop().setUserId(id).setShopName(user.getNickName());
//                mallShopMapper.addMallShop(mallShop);
//                count++;
//            }
//        }
//        return "商家共有："+users.size()+",初始化："+count;
//    }
}
