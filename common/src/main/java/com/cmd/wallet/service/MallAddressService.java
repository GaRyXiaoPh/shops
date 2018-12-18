package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.MallAddressMapper;
import com.cmd.wallet.common.model.TMallAddressModel;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MallAddressService {

    @Autowired
    private MallAddressMapper addressMapper;

    public List<TMallAddressModel> getAddresses(Integer userId) {
        List<TMallAddressModel> addressByUserId = addressMapper.getAddressByUserId(userId);
        return addressByUserId;
    }

    public void addAddress(TMallAddressModel tMallAddressModel) {
        List<TMallAddressModel> addresses = getAddresses(tMallAddressModel.getUserId());
        if(addresses == null || addresses.isEmpty()){
            tMallAddressModel.setIsDefault(1);
        }
        int i = addressMapper.saveMallAddress(tMallAddressModel);
        Assert.check(i == 0,ErrorCode.ERR_RECORD_DATA_ERROR);
    }

    public void delAddress(Integer userId, Integer id) {
        int i = addressMapper.delMallAddress(userId,id);
        Assert.check(i == 0,ErrorCode.ERR_RECORD_UPDATE);
    }
    public TMallAddressModel getAddress(Integer addressId) {
        return addressMapper.getAddressById(addressId);
    }

    public void editAddress(TMallAddressModel tMallAddressModel) {
        int i = addressMapper.updateAddress(tMallAddressModel);
        Assert.check(i != 1,ErrorCode.ERR_RECORD_UPDATE);
    }

    /**
     * 设置原来的默认地址为非默认地址
     */
    public void changeDefaultAddress(){
        Integer userId = ShiroUtils.getUser().getId();
        addressMapper.changeDefaultAddress(userId);
    }

    public void defaultAddress(Integer id) {
        changeDefaultAddress();
        addressMapper.defaultAddress(id);
    }
    public TMallAddressModel getDefaultAddresses() {
        TMallAddressModel addressByUserId = addressMapper.getDefaultAddress(ShiroUtils.getUser().getId());
        return addressByUserId;
    }

}
