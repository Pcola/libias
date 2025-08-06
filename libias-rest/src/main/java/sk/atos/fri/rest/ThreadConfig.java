package sk.atos.fri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import sk.atos.fri.configuration.ServerConfig;

/**
 *
 * @author Jaroslav Kollar
 */
@Configuration
public class ThreadConfig { 
  @Autowired
  ServerConfig configServer;
  
    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
 
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configServer.getNumberOfThreads());
        executor.setMaxPoolSize(configServer.getNumberOfThreads());        
        executor.initialize();        
        return executor;
    }
    
    @Bean
    public Integer threadCount() {
        return configServer.getNumberOfThreads();
    }
}
