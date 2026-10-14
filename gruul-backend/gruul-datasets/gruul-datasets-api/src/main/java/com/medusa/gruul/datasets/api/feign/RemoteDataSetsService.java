package com.medusa.gruul.datasets.api.feign;


import com.medusa.gruul.datasets.api.model.CallerDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * The interface Remote datasets service.
 *
 * @Description: RemoteDatasetsService.java
 * @Author: xiaoq
 * @Date: 2024/05/07 14:30
 */
@FeignClient(value = "datasets")
@Hidden
public interface RemoteDataSetsService {

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
    CallerDto callerInfo(
            @RequestParam(value = "startTime", required = true) @NotNull(message = "startTime不能为null") Integer startTime,
            @RequestParam(value = "endTime", required = false) Integer endTime);
}
