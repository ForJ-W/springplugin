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

package mybatisplusdemo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import mybatisplusdemo.entity.MybatisPlusDemoEntity;
import mybatisplusdemo.service.MybatisPlusDemoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author afěi
 * @version 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("mybatisplusdemo")
public class MybatisPlusDemoController {

    final MybatisPlusDemoService mybatisPlusDemoService;

    @GetMapping("list")
    public List<MybatisPlusDemoEntity> list() {

        return mybatisPlusDemoService.list();
    }


    @PostMapping("save")
    public String save(@RequestBody MybatisPlusDemoEntity mybatisPlusDemoEntity) {

        mybatisPlusDemoService.save(mybatisPlusDemoEntity);
        return "mybatis plus demo save";
    }

    @PostMapping("remove")
    public String remove(@RequestBody MybatisPlusDemoEntity mybatisPlusDemoEntity) {

        mybatisPlusDemoService.remove(Wrappers.
                <MybatisPlusDemoEntity>lambdaQuery()
                .eq(Objects.nonNull(mybatisPlusDemoEntity) && Objects.nonNull(mybatisPlusDemoEntity.getId()),
                        MybatisPlusDemoEntity::getId, mybatisPlusDemoEntity.getId()));
        return "mybatis plus demo remove";
    }
}
