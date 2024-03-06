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

package mybatisplusdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import mybatisplusdemo.entity.MybatisPlusDemoEntity;
import mybatisplusdemo.mapper.MybatisPlusDemoDemoMapper;
import mybatisplusdemo.service.MybatisPlusDemoService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@Service
public class MybatisPlusDemoServiceImpl extends ServiceImpl<MybatisPlusDemoDemoMapper, MybatisPlusDemoEntity> implements MybatisPlusDemoService {

    @Override
    public void logId() {
        log.info("mybatis demo service,log-------------- {}", UUID.randomUUID());
    }
}
