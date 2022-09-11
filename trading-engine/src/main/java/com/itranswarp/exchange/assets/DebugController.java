package com.itranswarp.exchange.assets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    AssetService assetService;

    @ResponseBody
    @GetMapping("dump/era/{era}")
    public String dump( @PathVariable("era") int era) {
        assetService.dumpDB(era);
        return "ok";
    }

    @GetMapping("load/era/{era}")
    public String load( @PathVariable("era") int era) {
        boolean b = assetService.loadFromDB(era);
        return "ok";
    }

}
