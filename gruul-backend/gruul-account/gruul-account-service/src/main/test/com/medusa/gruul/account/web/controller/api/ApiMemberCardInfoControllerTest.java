package com.medusa.gruul.account.web.controller.api;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.account.model.vo.ConfirmPaymentVo;
import com.medusa.gruul.account.model.vo.RegisterDataVo;
import com.medusa.gruul.account.web.model.Param.PhoneParam;


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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Description ApiMemberCardInfoControllerTest 单元测试类
 * @Author zhaokw
 * @Date 10:05 2024\8\23 0023
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiMemberCardInfoControllerTest {
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
     * Method: getMemberCardInfo
     */
    @Test
    public void getMemberCardInfo() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/getMemberCardInfo"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: confirmPayment
     */
    @Test
    public void confirmPayment() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ConfirmPaymentVo confirmPaymentVo = new ConfirmPaymentVo();
        confirmPaymentVo.setAvailableAmount(new BigDecimal(12))
                .setMemberCardInfoId(42L)
                .setOpenType(1)
                .setPaymentType(1)
                .setUseAmount(new BigDecimal(0))
                .setUseInDate(360L);
        String obj = JSONUtil.toJsonStr(confirmPaymentVo);
        MvcResult result = mockMvc.perform(post("/api/confirmPayment").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: sendCode
     */
    @Test
    public void sendCode() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        PhoneParam phoneParam = new PhoneParam();
        phoneParam.setPhone("12867564567");
        String obj = JSONUtil.toJsonStr(phoneParam);
        MvcResult result = mockMvc.perform(get("/api/sendCode").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: updateRegisterData
     */
    @Test
    public void updateRegisterData() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        RegisterDataVo registerDataVo = new RegisterDataVo();
        registerDataVo.setCode("854545")
                .setPhone("13867567865");
        String obj = JSONUtil.toJsonStr(registerDataVo);
        MvcResult result = mockMvc.perform(post("/api/updateRegisterData").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: memberCenter
     */
    @Test
    public void memberCenter() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/memberCenter"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: getMemberCardRecord
     */
    @Test
    public void getMemberCardRecord() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/getMemberCardRecord"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: getRegisterForm
     */
    @Test
    public void getRegisterForm() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/getRegisterForm"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

} 
