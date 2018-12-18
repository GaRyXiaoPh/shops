package com.cmd.wallet.api;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.mapper.ConfigMapper;
import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.model.MallApply;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.DateUtil;
import com.cmd.wallet.common.vo.MallGoodListVO;
import com.cmd.wallet.service.MallCartService;
import com.cmd.wallet.service.MallGoodService;
import com.cmd.wallet.service.MallOrderService;
import com.cmd.wallet.service.MallShopService;
import com.cmd.wallet.service.ReleaseAwardService;
import com.cmd.wallet.service.UserService;
import com.github.pagehelper.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiApplicationTests {

    @Autowired
    MallGoodService mallGoodService;

    @Autowired
    private ReleaseAwardService releaseAwardService;

    @Autowired
    ConfigMapper configMapper;

    @Autowired
    MallShopService mallShopService;

    @Autowired
    UserService userService;
    @Autowired
    MallCartService mallCartService;
    @Autowired
    MallOrderService mallOrderService;

    @Test
    public void contextLoads(){
        User user = userService.getUserByUserId(138);
        MallApply mallApply = new MallApply();
       // mallOrderService.getMyOrdersByStatus(1, 10, Integer userId , Integer status)
       // mallCartService.getMallCartByUserId(68);
       // mallApply.s
       // mallShopService.applyShop(user,MallApply mallApply)
        //MallApply mallApply =  mallShopService.getMallApply(138);
       /* int currentTime =   (int)(DateUtil.getDateByString("00:00:00").getTime()/1000);
        Config config = configMapper.getConfigByName(ConfigKey.LAST_RELEASE_TIME);
        int configTime = Integer.valueOf(config.getConfValue());
        if(currentTime>configTime) {
            if (releaseAwardService.relaseBool) {

                releaseAwardService.releaseAwardBanlance();

                //修改配置文件配置信息

            }
        }
*/
    }
}
