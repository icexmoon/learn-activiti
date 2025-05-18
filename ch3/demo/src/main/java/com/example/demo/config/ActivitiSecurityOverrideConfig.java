package com.example.demo.config;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : 魔芋红茶
 * @version : 1.0
 * @Project : demo
 * @Package : com.example.demo.config
 * @ClassName : .java
 * @createTime : 2025/5/18 11:33
 * @Email : icexmoon@qq.com
 * @Website : https://icexmoon.cn
 * @Description : 禁用 Activiti 的 SpringSecurity 使用
 */
@Configuration
public class ActivitiSecurityOverrideConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException("Security is disabled");
        };
    }

    @Bean
    public UserGroupManager userGroupManager() {
        return new UserGroupManager() {
            @Override
            public List<String> getUserGroups(String userId) {
                return Collections.emptyList();
            }

            @Override
            public List<String> getUserRoles(String userId) {
                return Collections.emptyList();
            }

            @Override
            public List<String> getGroups() {
                return null;
            }

            @Override
            public List<String> getUsers() {
                return null;
            }
        };
    }
}
