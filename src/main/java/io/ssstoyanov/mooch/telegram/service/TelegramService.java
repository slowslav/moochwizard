package io.ssstoyanov.mooch.telegram.service;

import io.ssstoyanov.mooch.event.ParsedContentEvent;
import io.ssstoyanov.mooch.event.ParsedInlineContentEvent;
import io.ssstoyanov.mooch.media.entity.Content;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultAudio;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultVideo;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public abstract class TelegramService {

    protected static final String MARKDOWN = "Markdown";
    protected static final String VIDEO_MP_4 = "video/mp4";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final Pattern urlCleaner = Pattern.compile("(?<=https://www\\.)[a-zA-Z\\/\\.0-9-@]+");

    protected static int[] getImageDimensions(String imageUrl) {
        int[] dimensions = new int[2];

        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            dimensions[0] = image.getWidth();
            dimensions[1] = image.getHeight();
        } catch (IOException e) {
            log.error(e);
        }

        return dimensions;
    }

    protected String setSearchRequestInlineAudio(Update update) {
        var msg = update.getInlineQuery();
        var username = "@" + msg.getFrom().getUserName();
        if (msg.getFrom().getUserName() == null) {
            username = msg.getFrom().getFirstName();
        }
        return String.format("%s: ", username);
    }

    protected String setSearchRequestAudio(Update update) {
        var msg = update.getMessage();
        var username = "@" + msg.getFrom().getUserName();
        if (msg.getFrom().getUserName() == null) {
            username = msg.getFrom().getFirstName();
        }
        return String.format("%s: ", username);
    }

    protected String setSearchRequest(Update update) {
        var msg = update.getMessage();
        var url = cleanUrl(msg.getEntities().get(0).getText());
        var username = "@" + msg.getFrom().getUserName();
        if (msg.getFrom().getUserName() == null) {
            username = msg.getFrom().getFirstName();
        }
        return String.format("%s: %s", username, url);
    }

    protected String setSearchRequestInline(Update update) {
        var msg = update.getInlineQuery();
        var url = cleanUrl(msg.getQuery());
        var username = "@" + msg.getFrom().getUserName();
        if (msg.getFrom().getUserName() == null) {
            username = msg.getFrom().getFirstName();
        }
        return String.format("%s: %s", username, url);
    }

    protected String cleanUrl(String text) {
        Matcher matcher = urlCleaner.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return text;
    }

    protected String setCaption(String caption) {
        int maxLength = 1024;

        // Remove hashtags
        caption = caption.replaceAll("#\\S+", "").trim();

        // Truncate the caption if it exceeds the maximum length
        if (caption.length() > maxLength) {
            // Try truncating by sentences
            int lastDotIndex = caption.lastIndexOf('.', maxLength - 1);
            if (lastDotIndex > 0) {
                caption = caption.substring(0, lastDotIndex + 1);
            } else {
                // Try truncating by newline characters
                int lastNewLineIndex = caption.lastIndexOf('\n', maxLength - 1);
                if (lastNewLineIndex > 0) {
                    caption = caption.substring(0, lastNewLineIndex);
                } else {
                    // Truncate by space
                    int lastSpaceIndex = caption.lastIndexOf(' ', maxLength - 1);
                    if (lastSpaceIndex > 0) {
                        caption = caption.substring(0, lastSpaceIndex);
                    } else {
                        // If no space is found, force truncation at maxLength
                        caption = caption.substring(0, maxLength);
                    }
                }
            }
        }

        return caption;
    }

    protected void setCaption(ParsedContentEvent event, Update message, List<InputMedia> photos) {
        if (event.getObject().getText() != null) {
            photos.get(0).setCaption(setSearchRequest(message) + "\n" + setCaption(event.getObject().getText()));
        } else {
            photos.get(0).setCaption(setSearchRequest(message));
        }
    }

    protected void setCaption(ParsedContentEvent event, Update message, SendPhoto photo) {
        if (event.getObject().getText() != null) {
            photo.setCaption(setSearchRequestInline(message) + "\n\n" + setCaption(event.getObject().getText()));
        } else {
            photo.setCaption(setSearchRequestInline(message));
        }
    }

    protected void setCaption(ParsedInlineContentEvent event, Update update, InlineQueryResultVideo res) {
        if (event.getObject().getText() != null) {
            res.setCaption(setSearchRequestInline(update) + "\n\n" + setCaption(event.getObject().getText()));
        } else {
            res.setCaption(setSearchRequestInline(update));
        }
    }

    protected void setCaption(ParsedInlineContentEvent event, Update update, InlineQueryResultPhoto res) {
        if (event.getObject().getText() != null) {
            res.setCaption(setSearchRequestInline(update) + "\n\n" + setCaption(event.getObject().getText()));
        } else {
            res.setCaption(setSearchRequestInline(update));
        }
    }

    protected void setCaption(Update update, SendMessage message, Content content) {
        if (content.getText() != null) {
            message.setText(setSearchRequest(update) + "\n\n" + setCaption(content.getText()));
        } else {
            message.setText(setSearchRequest(update));
        }
    }


    protected void setCaption(Update update, InputTextMessageContent answer, Content content) {
        if (content.getText() != null) {
            answer.setMessageText(setSearchRequestInline(update) + "\n\n" + setCaption(content.getText()));
        } else {
            answer.setMessageText(setSearchRequestInline(update));
        }
    }

    protected void setCaption(Update update, Content content, InlineQueryResultAudio res) {
        if (content.getText() != null) {
            res.setCaption(setSearchRequestInlineAudio(update) + "\n\n" + setCaption(content.getText()));
        } else {
            res.setCaption(setSearchRequestInlineAudio(update));
        }
    }

    protected void setPhotoFields(ParsedInlineContentEvent event, Update update, String url, String thumb, InlineQueryResultPhoto res) {
        res.setPhotoUrl(url);
        int[] dimensions = getImageDimensions(url);
        res.setPhotoWidth(dimensions[0]);
        res.setPhotoHeight(dimensions[1]);
        setCaption(event, update, res);
        res.setThumbUrl(thumb);
        res.setId(UUID.randomUUID().toString());
        res.setTitle(UUID.randomUUID().toString());
        res.setMimeType(IMAGE_JPEG);
    }
}
