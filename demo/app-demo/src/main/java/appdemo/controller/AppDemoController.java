/*
 * Copyright 2023 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package appdemo.controller;

import appdemo.entity.AppDemoEntity;
import appdemo.service.AppDemoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import mybatisplusdemo.service.MybatisPlusDemoService;
import org.springframework.web.bind.annotation.*;
import org.springplugin.core.util.PluginBeanUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author afěi
 * @version 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("appdemo")
public class AppDemoController {

    final AppDemoService appDemoService;

    final MybatisPlusDemoService mybatisPlusDemoService = getMybatisPlusDemoBean(MybatisPlusDemoService.class);

    /**
     * 获取其他插件里的bean
     * <p>
     * 是选择直接调用另一个插件里的bean还是复制一个到当前插件呢？
     * <p>
     * 直接调用无法声明类型，实例与类的classloader不一致
     * 如何我使用一个代理类维护另一个插件中的bean呢？
     * <p>
     * 复制需要维护状态，如何只复制bean对象，而不复制bean的属性和行为呢？这样可行吗？
     * 没办法做到，因为bean对象和属性行为不是一个classloader，必须要把整个bean的生命周期复制一份。这样的话要维护源和复制bean的状态
     * <p>
     * 采用动态代理去维护调用另一个插件的bean, 规避掉类冲突问题
     *
     * @param cls bean类型
     * @return mybatisplus demo中的bean
     */
    static <T> T getMybatisPlusDemoBean(Class<T> cls) {

        return PluginBeanUtils.getBean("mybatisplusdemo", cls);
    }

    @GetMapping("list")
    public List<AppDemoEntity> list() {

        mybatisPlusDemoService.logId();
        return appDemoService.list();
    }

    @PostMapping("save")
    public String save(@RequestBody AppDemoEntity appDemoEntity) {

        appDemoService.save(appDemoEntity);
        return "mybatis plus demo save";
    }

    @PostMapping("remove")
    public String remove(@RequestBody AppDemoEntity appDemoEntity) {

        appDemoService.remove(Wrappers.
                <AppDemoEntity>lambdaQuery()
                .eq(Objects.nonNull(appDemoEntity) && Objects.nonNull(appDemoEntity.getId()),
                        AppDemoEntity::getId, appDemoEntity.getId()));
        return "mybatis plus demo remove";
    }
}
