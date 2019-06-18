package com.melardev.spring.mongo.entities.auditors;


import com.melardev.spring.mongo.entities.TimeStampedDocument;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimestampAuditor extends AbstractMongoEventListener<TimeStampedDocument> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<TimeStampedDocument> event) {
        if (event.getSource().getCreatedAt() == null)
            event.getSource().setCreatedAt(LocalDateTime.now());
        event.getSource().setUpdatedAt(LocalDateTime.now());
    }
}
