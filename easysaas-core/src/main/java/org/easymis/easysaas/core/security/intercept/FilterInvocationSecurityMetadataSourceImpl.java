package org.easymis.easysaas.core.security.intercept;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.easymis.easysaas.core.security.service.ResourceService;
import org.easymis.easysaas.core.security.service.RoleResourceService;
import org.easymis.easysaas.core.security.service.RoleService;
import org.easymis.easysaas.core.security.service.UserRoleService;
import org.easymis.easysaas.core.security.userdetail.ExpireDateGrantedAuthority;
import org.easymis.easysaas.core.security.userdetail.Resource;
import org.easymis.easysaas.core.security.userdetail.Role;
import org.easymis.easysaas.core.security.userdetail.RoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    RoleResourceService roleResourceService;

    @Autowired
    RoleService roleService;


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Collection<ConfigAttribute> configAttributes =  new ArrayList<>();
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String requestURI = filterInvocation.getRequest().getRequestURI();
        Resource resource = resourceService.findByEndPoint(requestURI);
        //默认所有用户可以访问；角色标识判断
        if (Objects.isNull(resource)) {
            log.warn("requestURI={} 请求资源路径未配置入resource,默认可以通过匿名角色访问", requestURI);
            configAttributes = SecurityConfig.createList(ExpireDateGrantedAuthority.anonymous);
        } else {
            List<RoleResource> roleResourceList = roleResourceService.findByResourceId(resource.getId());
            if (roleResourceList.size() > 0) {
                List<String> roleIdList = roleResourceList.stream().map(RoleResource::getRoleId).distinct().collect(Collectors.toList());
                List<Role> roleList = roleService.findByRoleIds(roleIdList);
                configAttributes = roleList.stream().map(role -> new SecurityConfig(role.getRoleSn())).collect(Collectors.toList());
            }
        }
        return configAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {   //返回全部roleSn
        return roleService.list().stream().map(role -> new SecurityConfig(role.getRoleSn())).collect(Collectors.toList());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


}
