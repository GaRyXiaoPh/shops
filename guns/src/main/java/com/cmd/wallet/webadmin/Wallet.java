package com.cmd.wallet.webadmin;

import org.springframework.context.annotation.ComponentScan;

// 该类用于配置自动加载的组件，因为guns配置这堆东西会导致其无法正常运行
//@ComponentScan(basePackages = {"com.cmd.exchange.webadmin"})
@ComponentScan(value = {"com.cmd.wallet.service", "com.cmd.wallet.common", "com.cmd.wallet.webadmin", "com.cmd.wallet"})
public class Wallet {
    public Wallet() {
        System.out.println("Wallet");
    }
}
