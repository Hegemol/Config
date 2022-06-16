package org.hegemol.config.client.example;

import org.hegemol.config.common.model.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author KevinClair
 **/
@RestController
public class DemoController {

    private final DemoConfig demoConfig;

    public DemoController(final DemoConfig demoConfig) {
        this.demoConfig = demoConfig;
    }

    @PostMapping("/get")
    public Result<String> getConfig() {
        return Result.success(demoConfig.getTestKey());
    }
}
