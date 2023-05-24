package io.ssstoyanov.mooch.media.news;

import io.ssstoyanov.mooch.Utils;
import io.ssstoyanov.mooch.media.MediaService;
import io.ssstoyanov.mooch.media.entity.Content;
import io.ssstoyanov.mooch.media.entity.ContentType;
import org.springframework.stereotype.Service;

@Service
public class NewsService implements MediaService {
    @Override
    public Content getContent(String url) {
        return new Content().setType(ContentType.TEXT).setText(Utils.getSummary(url));
    }
}
