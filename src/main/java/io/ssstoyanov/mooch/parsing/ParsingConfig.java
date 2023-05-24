package io.ssstoyanov.mooch.parsing;

import io.ssstoyanov.mooch.media.MediaService;
import io.ssstoyanov.mooch.media.ServiceType;
import io.ssstoyanov.mooch.media.instagram.InstagramService;
import io.ssstoyanov.mooch.media.music.MusicService;
import io.ssstoyanov.mooch.media.news.NewsService;
import io.ssstoyanov.mooch.media.pinterest.PinterestService;
import io.ssstoyanov.mooch.media.tiktok.TikTokService;
import io.ssstoyanov.mooch.media.twitter.TwitterService;
import io.ssstoyanov.mooch.media.youtube.YouTubeService;
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
