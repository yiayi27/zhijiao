package com.medusa.gruul.solitaire.web.controller.api;



import com.medusa.gruul.solitaire.api.constant.SolitaireConstant;
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

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class ApiSolitaireActivityControllerTest {

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
     * Method: getCurrentActivity()
     */
    @Test
    public void testGetCurrentActivity() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/api/weChat/current/activity")
                        .header("token", SolitaireConstant.TEST_USER_TOKEN))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

    /**
     * Method: getCurrentActivityById(@Parameter(value = "接龙活动id", required = true) @PathVariable("activityId") Long activityId)
     */
    @Test
    public void testGetCurrentActivityById() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long activityId = 18L;
        MvcResult result = mockMvc.perform(get("/api/weChat/get/activity/" + activityId)
                        .header("token", SolitaireConstant.TEST_USER_TOKEN))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

    /**
     * Method: getCurrentActivityProduct(@Parameter(value = "接龙活动id", required = true) @PathVariable("activityId") Long activityId, @Parameter(value = "接龙活动跳转路径", required = true) @RequestParam("path") String path)
     */
    @Test
    public void testGetCurrentActivityProduct() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long activityId = 18L;
        String path = "https://test.superprism.cn/";
        MvcResult result = mockMvc.perform(get("/api/weChat/current/activity/product/" + activityId + "?path=" + path)
                        .header("token", SolitaireConstant.TEST_USER_TOKEN))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

    /**
     * Method: getCurrentActivityRecord(@Parameter(value = "接龙活动id", required = true) @PathVariable("activityId") Long activityId, @Parameter(value = "接龙活动跳转路径", required = true) @RequestParam("path") String path)
     */
    @Test
    public void testGetCurrentActivityRecord() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long activityId = 18L;
        String path = "https://test.superprism.cn/";
        MvcResult result = mockMvc.perform(get("/api/weChat/current/activity/record/" + activityId + "?path=" + path)
                        .header("token", SolitaireConstant.TEST_USER_TOKEN))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

} 
