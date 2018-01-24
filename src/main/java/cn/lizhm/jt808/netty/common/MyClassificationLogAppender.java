package cn.lizhm.jt808.netty.common;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

public class MyClassificationLogAppender extends DailyRollingFileAppender {

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		return this.getThreshold().equals(priority);
	}
}
