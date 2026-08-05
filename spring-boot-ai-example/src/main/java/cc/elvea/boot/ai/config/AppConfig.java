package cc.elvea.boot.ai.config;

import cc.elvea.boot.ai.config.properties.AppProperties;
import cc.elvea.boot.ai.tools.CommonTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.session.compaction.SlidingWindowCompactionStrategy;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({AppProperties.class})
public class AppConfig {

    @Bean
    SessionMemoryAdvisor sessionMemoryAdvisor(SessionService sessionService) {
        return SessionMemoryAdvisor.builder(sessionService)
            .defaultUserId("alice")
            .compactionTrigger(new TurnCountTrigger(20))
            .compactionStrategy(SlidingWindowCompactionStrategy.builder().maxEvents(10).build())
            .build();
    }

    @Bean
    public CommonTools commonTools() {
        return new CommonTools();
    }

    @Bean
    public ToolCallbackProvider commonToolsProvider(CommonTools commonTools) {
        return MethodToolCallbackProvider.builder().toolObjects(commonTools).build();
    }

}
