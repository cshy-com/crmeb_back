package com.cshy.admin.filter;


import cn.hutool.core.collection.CollUtil;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.utils.RequestUtil;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 返回值输出过滤器
 */
//@Component
public class ResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);//转换成代理类
        //请求前
//        //数据敏感词处理
//        //获取值
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        List<String> sensitiveWordList = Lists.newArrayList();
//        parameterMap.forEach((k, v) -> {
//            String str = String.join(",", v);
//            sensitiveWordList.addAll(SensitiveWordHelper.findAll(str));
//        });
//        if (CollUtil.isNotEmpty(sensitiveWordList)) {
//            String sensitiveWordStr = sensitiveWordList.stream().collect(Collectors.joining("，"));
//            throw new CrmebException("内容包含敏感词：" + sensitiveWordStr + "，请修改后重试");
//        }

        filterChain.doFilter(request, wrapperResponse);

        //返回前
        byte[] content = wrapperResponse.getContent();//获取返回值
        //判断是否有值
        if (content.length > 0) {
            String str = new String(content, StandardCharsets.UTF_8);

            try {
                HttpServletRequest req = (HttpServletRequest) request;
                str = new ResponseRouter().filter(str, RequestUtil.getUri(req));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //把返回值输出到客户端
            ServletOutputStream outputStream = response.getOutputStream();
            if (str.length() > 0) {
                outputStream.write(str.getBytes());
                outputStream.flush();
                outputStream.close();
                //最后添加这一句，输出到客户端
                response.flushBuffer();
            }
        }
    }
}
