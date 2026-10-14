package com.medusa.gruul.common.system;

import com.medusa.gruul.common.system.filter.SystemFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * date 2022/2/11
 */
@Import(
        {
                SystemFilter.class,
                SystemFeignRequestInterceptor.class,
        }
)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
public class SystemAutoConfigure {
}
