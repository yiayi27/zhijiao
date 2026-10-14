package com.medusa.gruul.datasets.web.remote;


import com.medusa.gruul.datasets.api.model.CallerDto;
import com.medusa.gruul.datasets.service.IDcShopActiveAnalyseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Feign接口
 * @Author: xiaoq
 * @Date: 2024/5/7 16:42
 */
@RestController(value = "remoteDataSetsService")
@RequestMapping("/")
@Tag(name = "Feign接口")
public class RemoteDataSetsService {

    @Autowired
    private IDcShopActiveAnalyseService dcShopActiveAnalyseService;

    /**
     * 根据时间查询 访客量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 情况1 传了开始时间及结束时间 返回该时间内的 总访问量
     * 情况2 只传了开始时间 是获取今日时间 返回 今日浏览量 及 昨日浏览量
     */
    @GetMapping(value = "caller")
    @Operation(summary = "访客信息获取")
    @PreAuthorize("permitAll()")
    public CallerDto callerInfo(
            @RequestParam(value = "startTime") @NotNull(message = "startTime不能为null") Integer startTime,
            @RequestParam(value = "endTime", required = false) Integer endTime) {
        return dcShopActiveAnalyseService.getCallerInfo(startTime, endTime);
    }
}
