package com.forgerock.example.mtls.zuul;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class SimpleFilter extends ZuulFilter {

    @Autowired
    private RouteConfigurationProperties routeConfigurationProperties;

    private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    @Value("${server.port}")
    public String port;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        for(RouteConfigurationProperties.RouteConfig route: routeConfigurationProperties.routes) {
            if (request.getServerName().equals(route.hostname) && !request.getRequestURI().startsWith(route.prefix)) {
                String url = UriComponentsBuilder.fromHttpUrl("https://" + route.hostname + ":" + port).build()
                        .toUriString();
                try {
                    ctx.setRouteHost(new URL(url));
                    ctx.set("requestURL", url);
                    ctx.set("requestURI", route.prefix + request.getRequestURI());
                } catch(MalformedURLException mue) {
                    log.error("Cannot forward to outage period endpoint");
                }
                return null;
            }
        }
        return null;
    }
}
