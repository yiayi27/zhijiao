package com.medusa.gruul.solitaire.web.controller.manager; 

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.common.core.constant.CommonConstants;


import com.medusa.gruul.solitaire.BaseTest;
import com.medusa.gruul.solitaire.api.constant.SolitaireConstant;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityDto;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class SolitaireActivityControllerTest extends BaseTest {

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
     * Method: getActivityList()
     */
    @Test
    public void testGetActivityList() throws Exception {
//        String url = "/manager/activity/list";
//        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("current", "1");
//        paramsMap.put("size", "10");
//        MvcResult result = super.getMockTestGet(url, paramsMap);
//        super.printResult(result);
//        super.assertCodeSuccess(result);
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        SolitaireActivityParam solitaireActivityParam = new SolitaireActivityParam();
        solitaireActivityParam.setCurrent(1);
        solitaireActivityParam.setSize(10);
        String obj = JSONUtil.toJsonStr(solitaireActivityParam);
        MvcResult result = mockMvc.perform(get("/manager/activity/list")
                .header("token", SolitaireConstant.TEST_USER_TOKEN)
                .content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

    /**
     * Method: getActivityById()
     */
    @Test
    public void testGetActivityById() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long id = 18L;
        MvcResult result = mockMvc.perform(get("/manager/activity/get/" + id)
                .header("token", SolitaireConstant.TEST_USER_TOKEN))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        ("单元测试结果：--------------" + result.getResponse().getContentAsString());
    }

    /**
     * Method: testAddActivity(@RequestBody @Validated SolitaireActivityDto solitaireActivityDto)
     */
    @Test
    public void testAddActivity() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        SolitaireActivityDto solitaireActivityDto = new SolitaireActivityDto();
        solitaireActivityDto.setName("单元测试接龙活动新增");
        solitaireActivityDto.setStartTime(DateUtil.parseDate("2024-08-22"));
        solitaireActivityDto.setEndTime(DateUtil.parseDate("2024-08-23"));
        solitaireActivityDto.setPreheatHour(1);
        solitaireActivityDto.setProductType(0);
        String obj = JSONUtil.toJsonStr(solitaireActivityDto);

        MvcResult ret = mockMvc.perform(
                post("/manager/activity/add")
                        .header("token", SolitaireConstant.TEST_USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(obj))
                .andReturn();
        ("单元测试结果：--------------" + ret.getResponse().getContentAsString());

    }

    /**
     * Method: testUpdateActivity(@RequestBody SolitaireActivityDto solitaireActivityDto)
     */
    @Test
    public void testUpdateActivity() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        

        SolitaireActivityDto solitaireActivityDto = new SolitaireActivityDto();
        solitaireActivityDto.setId(19L);
        solitaireActivityDto.setName("单元测试接龙活动修改");
        solitaireActivityDto.setStartTime(DateUtil.parseDate("2024-08-22"));
        solitaireActivityDto.setEndTime(DateUtil.parseDate("2024-08-23"));
        solitaireActivityDto.setPreheatHour(1);
        solitaireActivityDto.setProductType(0);

        String obj = JSONUtil.toJsonStr(solitaireActivityDto);

        MvcResult ret = mockMvc.perform(
                put("/manager/activity/update")
                        .header("token", SolitaireConstant.TEST_USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(obj))
                .andReturn();
        ("单元测试结果：--------------" + ret.getResponse().getContentAsString());

    }

    /**
     * Method: testChangeActivityStatus(@PathVariable Long id)
     */
    @Test
    public void testChangeActivityStatus() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long id = 19L;
        MvcResult ret = mockMvc.perform(
                put("/manager/activity/changeStatus/" + id)
                        .header("token", SolitaireConstant.TEST_USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        ("单元测试结果：--------------" + ret.getResponse().getContentAsString());
    }

    /**
     * Method: testDeleteActivity(@PathVariable Long id)
     */
    @Test
    public void testDeleteActivity() throws Exception {
        ISecurity.userMust().setShopId(SolitaireConstant.TEST_SHOP_ID);
        
        Long id = 19L;
        MvcResult ret = mockMvc.perform(
                delete("/manager/activity/delete/" + id)
                        .header("token", SolitaireConstant.TEST_USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        ("单元测试结果：--------------" + ret.getResponse().getContentAsString());
    }

} 
