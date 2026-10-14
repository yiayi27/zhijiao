package com.medusa.gruul.integral.web.controller.shipping;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.service.IGaoDeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaozheng
 */
@RestController
/**
 * @author zhaozheng
 */ /**
 * @author zhaozheng
 */
public class GaoDeController {
    @Autowired
    private IGaoDeService gaoDeService;

    @GetMapping(value = "/get/config/district")
    public Result<Object> configDistrict(@RequestParam(required = false) String keywords,
                                 @RequestParam(required = false) String subdistrict,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer offset,
                                 @RequestParam(required = false) String extensions,
                                 @RequestParam(required = false) String filter,
                                 @RequestParam(required = false) String callback,
                                 @RequestParam(required = false) String output) {

        return this.gaoDeService.configDistrict(keywords, subdistrict, page, offset, extensions, filter, callback, output);
    }

    @GetMapping(value = "/get/apiSearch")
    public Result<Object> apiSearch(@RequestParam(required = false) String location,
                            @RequestParam(required = false) String keywords,
                            @RequestParam(required = false) String types,
                            @RequestParam(required = false) String city,
                            @RequestParam(required = false) String radius,
                            @RequestParam(required = false) String sortrule,
                            @RequestParam(required = false) Integer offset,
                            @RequestParam(required = false) Integer page,
                            @RequestParam(required = false) String extensions,
                            @RequestParam(required = false) String sig,
                            @RequestParam(required = false) String output,
                            @RequestParam(required = false) String callback) {
        return this.gaoDeService.apiSearch(location, keywords, types, city, radius, sortrule, offset, page, extensions, sig, output, callback);
    }
}
