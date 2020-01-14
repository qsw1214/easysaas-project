package org.easymis.easysaas.gateway.security.handler;

import org.easymis.easysaas.common.result.RestResult;
import org.easymis.easysaas.gateway.entitys.vo.UserVo;
import org.easymis.easysaas.gateway.security.userdetail.SecurityUserDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler   {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        byte[]   dataBytes={};
        UserVo userVo = new UserVo();
        SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
        BeanUtils.copyProperties(userDetails,userVo);
        
        RestResult success = RestResult.buildSuccess(userVo);
        try {
        	String dataBytesString=JSON.toJSONString(success);
			dataBytes=dataBytesString.getBytes("UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
        
/*    	httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        Writer writer = httpServletResponse.getWriter();
        SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userDetails,userVo);
      //  String token = JwtTokenUtil.generateToken(userDetails.getPhoneNumber());
        RestResult success = RestResult.buildSuccess(userVo);
        writer.write(JSON.toJSONString(success));
        writer.flush();
        writer.close();*/
    }
   /* @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication){
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        //设置body
        WsResponse wsResponse = WsResponse.success();
       byte[]   dataBytes={};
        ObjectMapper mapper = new ObjectMapper();
        try {
        	SecurityUserDetails user=(SecurityUserDetails)authentication.getPrincipal();
            AuthUserDetails userDetails=buildUser(user);
            byte[] authorization=(userDetails.getUsername()+":"+userDetails.getPassword()).getBytes();
            String token= Base64.getEncoder().encodeToString(authorization);
            httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
            wsResponse.setResult(userDetails);
            dataBytes=mapper.writeValueAsBytes(wsResponse);
        }
        catch (Exception ex){
            ex.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("status", MessageCode.COMMON_FAILURE.getCode());
            result.addProperty("message", "授权异常");
            dataBytes=result.toString().getBytes();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }



    private AuthUserDetails  buildUser(SecurityUserDetails user){
        AuthUserDetails userDetails=new AuthUserDetails();
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword().substring(user.getPassword().lastIndexOf("}")+1,user.getPassword().length()));
        return userDetails;
    }
*/






}