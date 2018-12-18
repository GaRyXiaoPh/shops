package cn.stylefeng.guns.config;

import com.cmd.wallet.webadmin.Wallet;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({Wallet.class})
public class ImportCmdConfig {
    public ImportCmdConfig() {
        System.out.println("test");
    }
}
