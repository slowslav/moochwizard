package io.ssstoyanov.mooch.media.tiktok;

import io.ssstoyanov.mooch.Utils;
import io.ssstoyanov.mooch.media.MediaService;
import io.ssstoyanov.mooch.media.entity.Content;
import io.ssstoyanov.mooch.media.entity.ContentType;
import io.ssstoyanov.mooch.media.entity.Media;
import io.ssstoyanov.mooch.media.entity.MediaType;
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
