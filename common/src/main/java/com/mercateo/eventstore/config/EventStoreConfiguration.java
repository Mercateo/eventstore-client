/**
 * Copyright © 2018 Mercateo AG (http://www.mercateo.com)
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
 */
package com.mercateo.eventstore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mercateo.eventstore.connection.EventStoreFactory;
import com.mercateo.eventstore.connection.EventStores;
import com.mercateo.eventstore.json.EventJsonMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableConfigurationProperties
@Import({ EventStoreFactory.class, EventStores.class, EventJsonMapper.class })
public class EventStoreConfiguration {

    @Bean
    @ConfigurationProperties
    public EventStorePropertiesCollection eventStorePropertiesCollection() {
        return new EventStorePropertiesCollection();
    }

}
