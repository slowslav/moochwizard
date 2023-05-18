package io.ssstoyanov.demo.media;

import io.ssstoyanov.demo.media.entity.Content;

public interface MediaService {
    Content getContent(String url);
}
