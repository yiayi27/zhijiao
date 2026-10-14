package com.medusa.gruul.common.system.filter;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.SystemHeaders;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.common.system.model.Systems;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * 当前app请求的版本号 与客户端ip
 *
 * @author 张治保
 * date 2024/12/3
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class SystemFilter extends AbstractServletFilter {

    private static final String ICO = "/favicon.ico";

    public static Systems toSystems(
            String uri,
            String ip,
            String clientTypeStr,
            String shopIdStr,
            String platformStr
    ) {
        String logFormat = """
                \nURI    : {}
                Client : [ip:{},clientType:{},platform:{}],
                Tenants: [shopId:{}]
                """;
        log.debug(
                logFormat,
                uri, ip, clientTypeStr, platformStr, shopIdStr
        );

        //处理数据
        Platform platform = StrUtil.isBlank(platformStr) ? null : Platform.valueOf(platformStr);
        ClientType clientType = StrUtil.isBlank(clientTypeStr) ? null : ClientType.valueOf(clientTypeStr);
        String shopId = StrUtil.isBlank(shopIdStr) ? null : shopIdStr.trim();
        return new Systems(shopId, ip, platform, clientType);
    }


    @Override
    @SneakyThrows
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (ICO.equals(request.getRequestURI())) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().flush();
            return;
        }
        Systems systems = SystemFilter.toSystems(
                request.getMethod() + StrPool.C_SPACE + request.getRequestURI(),
                request.getHeader(SystemHeaders.IP),
                request.getHeader(SystemHeaders.CLIENT_TYPE),
                request.getHeader(SystemHeaders.SHOP_ID),
                request.getHeader(SystemHeaders.PLATFORM)
        );
        try {
            ScopedValue.runWhere(
                    ISystem.SYSTEM,
                    systems,
                    () -> {
                        try {
                            filterChain.doFilter(request, response);
                        } catch (IOException | ServletException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
            );
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();
            throw cause instanceof IOException || cause instanceof ServletException ? cause : ex;
        }
    }
}
