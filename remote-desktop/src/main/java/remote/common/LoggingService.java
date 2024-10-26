package remote.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingService {
    public static SimpleDateFormat simpleDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String getCurrentTime() {
        return simpleDateFormat.format(new Date());
    }
}
