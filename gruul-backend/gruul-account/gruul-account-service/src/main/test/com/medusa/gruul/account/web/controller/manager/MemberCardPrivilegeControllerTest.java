package com.medusa.gruul.account.web.controller.manager;

import cn.hutool.json.JSONUtil;
import com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto;
import com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto;


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
 * @Description MemberCardPrivilegeControllerTest 单元测试类
 * @Author zhaokw
 * @Date 10:05 2024\8\26 0023
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberCardPrivilegeControllerTest {
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
     * Method: getMemberCardPrivilege
     */
    @Test
    public void getMemberCardPrivilege() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MvcResult result = mockMvc.perform(get("/manager/getMemberCardPrivilege"))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: updateMemberCardPrivilege
     */
    @Test
    public void updateMemberCardPrivilege() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        UpdateMemberCardPrivilegeDto updateMemberCardPrivilegeDto = new UpdateMemberCardPrivilegeDto();
        updateMemberCardPrivilegeDto.setId(7L)
                .setIllustrate("申请维权时，系统自动同意维权申请，无需等待人工审核")
                .setIcon("https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/798a80e89e61489c944fcfe663aba04a.png")
                .setOpen(1)
                .setAllowDel(0);
        String obj = JSONUtil.toJsonStr(updateMemberCardPrivilegeDto);
        MvcResult result = mockMvc.perform(post("/manager/updateMemberCardPrivilege").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: updateMemberCardPrivilegeStatus
     */
    @Test
    public void updateMemberCardPrivilegeStatus() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MemberCardPrivilegeStatusDto memberCardPrivilegeStatusDto = new MemberCardPrivilegeStatusDto();
        memberCardPrivilegeStatusDto.setId(7L)
                .setOpen(1);
        String obj = JSONUtil.toJsonStr(memberCardPrivilegeStatusDto);
        MvcResult result = mockMvc.perform(post("/manager/updateMemberCardPrivilegeStatus").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: deleteMemberCardPrivilege
     */
    @Test
    public void deleteMemberCardPrivilege() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        DeleteMemberCardPrivilegeDto deleteMemberCardPrivilegeDto = new DeleteMemberCardPrivilegeDto();
        deleteMemberCardPrivilegeDto.setId(7L)
                .setIsDeleted(1);
        String obj = JSONUtil.toJsonStr(deleteMemberCardPrivilegeDto);
        MvcResult result = mockMvc.perform(post("/manager/deleteMemberCardPrivilege").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }

    /**
     * Method: insertMemberCardPrivilege
     */
    @Test
    public void insertMemberCardPrivilege() throws Exception {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        InsertMemberCardPrivilegeDto insertMemberCardPrivilegeDto = new InsertMemberCardPrivilegeDto();
        insertMemberCardPrivilegeDto.setIllustrate("自定义权益")
                .setIcon("https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/798a80e89e61489c944fcfe663aba04a.png")
                .setOpen(1)
                .setAllowDel(0)
                .setPType("0")
                .setPrivilegeName("自定义权益");
        String obj = JSONUtil.toJsonStr(insertMemberCardPrivilegeDto);
        MvcResult result = mockMvc.perform(post("/manager/insertMemberCardPrivilege").content(obj))
                .andExpect(status().isOk())// 模拟向testRest发送post请求
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
                .andReturn();// 返回执行请求的结果
        (result.getResponse().getContentAsString());
    }


} 
