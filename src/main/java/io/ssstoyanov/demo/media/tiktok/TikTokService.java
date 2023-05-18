package io.ssstoyanov.demo.media.tiktok;

import io.ssstoyanov.demo.Utils;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Log4j2
@Service
public class TikTokService implements MediaService {

    public Content getContent(String url) {
        return new Content().setType(ContentType.VIDEO)
                .setMedia(Collections.singletonList(new Media().setType(MediaType.VIDEO)
                        .setUrl(Utils.getVideoUrl(url)).setThumbnailUrl(Utils.getThumbUrl(url))));
    }

}
