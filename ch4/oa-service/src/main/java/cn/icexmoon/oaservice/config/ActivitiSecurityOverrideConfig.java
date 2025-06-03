package cn.icexmoon.oaservice.config;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName ActivitiSecurityOverrideConfig
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/3 下午6:54
 * @Version 1.0
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
