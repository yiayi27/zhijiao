package com.medusa.gruul.finance.api.feign;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * The interface Remote finance service.
 * @author alan
 */
@FeignClient(value = "finance")
@Hidden
public interface RemoteFinanceService {

}
