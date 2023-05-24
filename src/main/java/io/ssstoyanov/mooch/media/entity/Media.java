package io.ssstoyanov.mooch.media.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media {
    MediaType type;
    String url;
    String thumbnailUrl;

    public Media(MediaType type, String url) {
        this.type = type;
        this.url = url;
    }

}
