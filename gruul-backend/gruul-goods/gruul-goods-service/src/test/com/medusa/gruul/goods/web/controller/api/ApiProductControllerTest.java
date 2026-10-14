package com.medusa.gruul.goods.web.controller.api;

import cn.hutool.json.JSONUtil;


import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ApiProductController Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10/15/2023</pre>
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest
public class ApiProductControllerTest {
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
     * Method: get(@PathVariable("id") Long id)
     */
    @Test
    public void testGet() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/product/get/1"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: indexList(ApiProductParam productParam)
     */
    @Test
    public void testIndexList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/index/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: categoryList(ApiProductParam productParam)
     */
    @Test
    public void testCategoryList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/category/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: activitiesList(ApiProductParam productParam)
     */
    @Test
    public void testActivitiesList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/activities/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: supermarketList(ApiProductParam productParam)
     */
    @Test
    public void testSupermarketList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/supermarket/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: groupList(ApiProductParam productParam)
     */
    @Test
    public void testGroupList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/group/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: flashSaleList(ApiProductParam productParam)
     */
    @Test
    public void testFlashSaleList() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        ApiProductParam param = new ApiProductParam();
        String obj = JSONUtil.toJsonStr(param);
        MvcResult result = mockMvc.perform(get("/api/product/flash/sale/list").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }


} 
