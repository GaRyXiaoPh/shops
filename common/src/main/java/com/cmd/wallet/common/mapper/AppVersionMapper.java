package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.AppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppVersionMapper {
    AppVersion getAppVersion(@Param("platform") String platform);
    List<AppVersion> getAppVersionList();
    int updateAppVersion(AppVersion appVersion);
    AppVersion getAppVersionById(@Param("id") int id);
}
