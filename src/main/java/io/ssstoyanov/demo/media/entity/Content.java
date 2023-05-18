package io.ssstoyanov.demo.media.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Content {
    String text;
    ContentType type;
    List<Media> media;
    String name;

    public void addMedia(Media media) {
        if (this.media == null) {
            this.media = new ArrayList<>();
        }
        this.media.add(media);
    }

    @Nullable
    public Media getFirstMedia() {
        if (this.media == null || this.media.isEmpty()) {
            return null;
        } else {
            return media.get(0);
        }
    }

}
