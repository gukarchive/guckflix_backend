package guckflix.backend.log;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SlackAlarm {

    @Value("${slack.webhook.url}")
    private String url;
    private final Slack slackClient = Slack.getInstance();

    /**
     * 2024-10-21T22:09:23.027650100 에 발생한 에러 로그
     * 로그 UUID
     * b5e4cacc-b7b3-446e-b719-ea30be6d5da7
     * 요청
     * GET http://localhost:8081/test
     * 예외가 발생한 클래스
     * String guckflix.backend.controller.TestController.test()
     * 예외 클래스
     * class guckflix.backend.exception.NotAllowedIdException
     */
    public void sendAlert(String uuid, String requestURL, String occuredClass, Exception e) {

        try {

            Attachment attachment = generateSlackAttachment(uuid, requestURL, occuredClass, e);

            Payload payload = Payload.builder().username("예외 감지기")
                    .text("예외가 감지되었습니다. 확인이 필요합니다.")
                    .attachments(List.of(attachment))
                    .build();

            slackClient.send(url, payload);
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
    }

    private Attachment generateSlackAttachment(String uuid, String requestURL, String occuredClass, Exception e) {

        return Attachment.builder()
                .color("ff0000")
                .title(LocalDateTime.now() + " 에 발생한 에러 로그")
                .fields(List.of(
                        generateSlackField("로그 UUID", uuid),
                        generateSlackField("요청", requestURL),
                        generateSlackField("예외가 발생한 클래스", occuredClass),
                        generateSlackField("예외 클래스", e.getClass().toString())
                )).build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .build();

    }

}
