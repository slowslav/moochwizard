package io.ssstoyanov.demo.media.news;

import io.ssstoyanov.demo.Utils;
import io.ssstoyanov.demo.media.MediaService;
import io.ssstoyanov.demo.media.entity.Content;
import io.ssstoyanov.demo.media.entity.ContentType;
import org.springframework.stereotype.Service;

@Service
public class NewsService implements MediaService {
    @Override
    public Content getContent(String url) {
        return new Content().setType(ContentType.TEXT).setText(Utils.getSummary(url));
    }
}
