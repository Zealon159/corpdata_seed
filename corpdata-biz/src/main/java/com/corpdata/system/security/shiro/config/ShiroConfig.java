package com.corpdata.system.security.shiro.config;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;

import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corpdata.system.security.shiro.ShiroRealm;
import com.corpdata.system.security.shiro.api.RedisCacheManager;
import com.corpdata.system.security.shiro.api.RedisSessionDAO;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;

@Configuration
public class ShiroConfig {

	@Autowired
	private EnterpriseCacheSessionDAO sessionDAO;
	
	//安全管理器
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }
    
	/**
	 * 请求资源拦截配置
	 * @param securityManager
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();

        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/image/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/font/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/doLogin", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/weixin/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        //filterChainDefinitionMap.put("/**", "anon");
        // 登录页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        
        //退出过滤器
//        Map<String,Filter> filters = new LinkedHashMap<String,Filter>();
//        filters.put("logout", new SystemLogoutFilter());
//        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

	@Bean
    public RedisCacheManager redisCacheManager() {
        return new RedisCacheManager();
    }
	
	//授权认证实现配置
	@Bean
    public ShiroRealm shiroRealm(){
        ShiroRealm shiroRealm = new ShiroRealm(redisCacheManager());
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        shiroRealm.setAuthorizationCachingEnabled(true);
        shiroRealm.setAuthorizationCacheName("authorizationCache");
        return shiroRealm;
    }

	//会话管理器配置
	@Bean
	public SessionManager sessionManager(){
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //EnterpriseCacheSessionDAO dao = new EnterpriseCacheSessionDAO();
        //RedisSessionDAO dao = new RedisSessionDAO();
        sessionManager.setSessionIdCookie(simpleCookie());
		sessionManager.setSessionDAO(sessionDAO);
		return sessionManager;
	}

	@Bean
	public Cookie simpleCookie(){
        Cookie cookie = new SimpleCookie("shiro.sesssion");
        cookie.setPath("/");
        return cookie;
    }

	/**
	 * 凭证匹配器
	 * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理）
	 * @return
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher(){
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(3);//散列的次数，比如散列两次，相当于 md5(md5(""));
		return hashedCredentialsMatcher;
	}
	
	/**
	 *  开启shiro aop注解支持.
	 *  使用代理方式;所以需要开启代码支持;
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}
	
	@Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }
	
	@Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
	
	 /**
     * ShiroDialect，为了在thymeleaf里使用shiro的标签的bean
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
