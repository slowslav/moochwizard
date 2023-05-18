package io.ssstoyanov.demo.media.youtube;

import io.ssstoyanov.demo.Utils;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import io.ssstoyanov.demo.media.entity.Media;
import io.ssstoyanov.demo.media.entity.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class YouTubeService implements MediaService {
    @Override
    public Content getContent(String url) {
        url = url.replace("shorts/", "watch?v=");
        return new Content().setType(ContentType.VIDEO)
                .setMedia(Collections.singletonList(new Media().setType(MediaType.VIDEO)
                        .setUrl(Utils.getVideoUrlYT(url)).setThumbnailUrl(Utils.getThumbUrl(url))));
    }
}
