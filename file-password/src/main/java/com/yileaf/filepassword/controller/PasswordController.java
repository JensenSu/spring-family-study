package com.yileaf.filepassword.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpStatus;
import com.yileaf.filepassword.config.SystemParams;
import com.yileaf.filepassword.constant.Messages;
import com.yileaf.filepassword.entity.NormalLog;
import com.yileaf.filepassword.model.Result;
import com.yileaf.filepassword.model.Ssm;
import com.yileaf.filepassword.service.SsmPasswordService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 解压密码请求接口
 *
 * @author Haotian
 * @version 1.0.0
 * @date 2020/7/3 21:15
 **/
@RestController
public class PasswordController {
    @Resource
    private SystemParams systemParams;
    @Resource
    private SsmPasswordService ssmPasswordService;
    @Resource
    private MongoTemplate mongoTemplate;

    @GetMapping("/docker")
    public Result returnDockerPassword(@RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String password) {
        if (username.equalsIgnoreCase( systemParams.getLoginUsername() ) && password.equalsIgnoreCase( systemParams.getDockerPassword() )) {
            return Result.success(
                    "密文=" + Base64.encode( systemParams.getLoginUsername() ),
                    Messages.DOCKER_PASSWORD_OK
            );
        }
        return Result.error( HttpStatus.HTTP_OK, Messages.CHECK_PASSWORD_ERROR );
    }

    @PostMapping("/ssm")
    public Result returnSsmPassword(@Validated @RequestBody Ssm ssm) {
        boolean flag = ssmPasswordService.checkSsmLoginNameAndPassword( ssm );
        if (flag) {
            return Result.success(
                    "密文=" + Base64.encode( systemParams.getSsmPassword() ),
                    Messages.SSM_PASSWORD_OK );
        }
        return Result.error( HttpStatus.HTTP_OK, Messages.CHECK_PASSWORD_ERROR );
    }

    /**
     * 查询所有正常日志
     *
     * @return 正常日志集合
     */
    @GetMapping("/findAll")
    public List<NormalLog> findAll() {
        return mongoTemplate.findAll( NormalLog.class );
    }

}