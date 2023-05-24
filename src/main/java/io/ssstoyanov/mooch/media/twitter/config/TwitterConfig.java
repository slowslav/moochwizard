package io.ssstoyanov.mooch.media.twitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;

@Configuration
public class TwitterConfig {

    @Bean
    public Twitter twitterApi() {
        return Twitter.getInstance();
    }

}
