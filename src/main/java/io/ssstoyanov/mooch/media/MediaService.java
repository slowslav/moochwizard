package io.ssstoyanov.mooch.media;

import io.ssstoyanov.mooch.media.entity.Content;

public interface MediaService {
    Content getContent(String url);
}
