package io.ssstoyanov.demo.parsing;

import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.ServiceType;
import io.ssstoyanov.demo.media.instagram.InstagramService;
import io.ssstoyanov.demo.media.music.MusicService;
import io.ssstoyanov.demo.media.news.NewsService;
import io.ssstoyanov.demo.media.pinterest.PinterestService;
import io.ssstoyanov.demo.media.tiktok.TikTokService;
import io.ssstoyanov.demo.media.twitter.TwitterService;
import io.ssstoyanov.demo.media.youtube.YouTubeService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@AllArgsConstructor
public class ParsingConfig {

    private final PinterestService pinterestService;
    private final TikTokService tikTokService;
    private final InstagramService instagramService;
    private final TwitterService twitterService;
    private final MusicService musicService;
    private final YouTubeService youTubeService;
    private final NewsService newsService;

    @Bean
    public Map<ServiceType, MediaService> getServices() {
        return Map.of(ServiceType.PINTEREST, pinterestService,
                ServiceType.TIKTOK, tikTokService,
                ServiceType.INSTAGRAM, instagramService,
                ServiceType.TWITTER, twitterService,
                ServiceType.MUSIC, musicService,
                ServiceType.NEWS, newsService,
                ServiceType.YOUTUBE, youTubeService);
    }
}
