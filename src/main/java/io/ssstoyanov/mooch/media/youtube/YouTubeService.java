package io.ssstoyanov.mooch.media.youtube;

import io.ssstoyanov.mooch.Utils;
import io.ssstoyanov.mooch.media.MediaService;
import io.ssstoyanov.mooch.media.entity.Content;
import io.ssstoyanov.mooch.media.entity.ContentType;
import io.ssstoyanov.mooch.media.entity.Media;
import io.ssstoyanov.mooch.media.entity.MediaType;
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
