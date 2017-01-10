package com.odaniait.jobframework.notifications;

import com.odaniait.jobframework.models.Build;
import com.odaniait.jobframework.models.Pipeline;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationManager {
	private static Logger logger = LoggerFactory.getLogger(NotificationManager.class);

	@Getter
	private Map<String, Notifier> notifier = new HashMap<>();

	@Autowired
	public NotificationManager(EmailNotifier emailNotifier) {
		notifier.put("email", emailNotifier);
	}

	public void notifyFailure(Pipeline pipeline, Build build) {
		String notificationText = "[FAILURE] Executing Pipeline " + pipeline.getId() + " (Build: " + build.getBuildNr() + ")";
		notify(pipeline, build, notificationText);
	}

	public void notifyBackToNormal(Pipeline pipeline, Build build) {
		String notificationText = "[BACK-TO-NORMAL] Executing Pipeline " + pipeline.getId() + " (Build: " + build.getBuildNr() + ")";
		notify(pipeline, build, notificationText);
	}

	private void notify(Pipeline pipeline, Build build, String notificationText) {
		for (String notifyType : pipeline.getNotify().keySet()) {
			if (notifier.containsKey(notifyType)) {
				notifier.get(notifyType).exec(build, pipeline.getNotify().get(notifyType), notificationText);
			} else {
				logger.error("Notification Type " + notifyType + " does not exist");
			}
		}
	}
}
