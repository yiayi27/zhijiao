package com.medusa.gruul.account.web.controller.manager;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.account.model.vo.UpdateOrderNoteVo;
import com.medusa.gruul.account.web.model.Param.MemberInfoParam;


import com.medusa.gruul.goods.api.constant.GoodsConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Description MemberOrderInfoControllerTest 单元测试类
 * @Author zhaokw
 * @Date 10:05 2024\8\23 0023
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberOrderInfoControllerTest {
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
    }

    @After
    public void after() {
    }

    /**
     * Method: getMemberOrderInfo
     */
    @Test
    public void getMemberOrderInfo() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MemberInfoParam memberInfoParam = new MemberInfoParam();
        memberInfoParam.setPage(1);
        memberInfoParam.setSize(20);
        String obj = JSONUtil.toJsonStr(memberInfoParam);
        MvcResult result = mockMvc.perform(get("/manager/getMemberOrderInfo")
                .content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: note
     */
    @Test
    public void note() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        UpdateOrderNoteVo updateOrderNoteVo = new UpdateOrderNoteVo();
        updateOrderNoteVo.setId(2204925L)
                .setNote("123333")
                .setState(0);
        String obj = JSONUtil.toJsonStr(updateOrderNoteVo);
        MvcResult result = mockMvc.perform(post("/manager/note").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

} 
