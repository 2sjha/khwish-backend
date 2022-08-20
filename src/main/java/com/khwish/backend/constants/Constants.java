package com.khwish.backend.constants;

import com.khwish.backend.models.Event;

public class Constants {

    private static ProfileConfig profileConfig;

    public static final String APPLICATION_NAME = "Khwish";

    public static final String WEB_THANKS_URL = "/thanks?goal-id=";
    public static final String WEB_GIFT_URL = "/gift?event-id=";

    public static final String BACKEND_INSTAMOJO_WEBHOOK_PATH = "/gift/instamojo-webhook";

    public static final String EMAIL_FROM = "#############@#####.com";

    public static final int NEGATIVE = -1;
    public static final int POSITIVE = +1;

    public static final String REGISTERED_VIA_EMAIL = "email";
    public static final String REGISTERED_VIA_PHONE_NUMBER = "phone_number";

    public static final String UPI_REGEX_PATTERN = "^\\w+@\\w+$";

    public static final String WITHDRAWAL_METHOD_UPI = "UPI Transfer";
    public static final String WITHDRAWAL_METHOD_BANK = "Bank Transfer";

    public static final String INSTAMOJO_PAYMENT_SUCCESS_STATUS = "Credit";

    public static ProfileConfig getProfileConfig() {
        return profileConfig;
    }

    public static void setProfileConfig(ProfileConfig profileConfig) {
        Constants.profileConfig = profileConfig;
    }

    public static String getEventInviteMessage(Event event) {
        return "Hi, Inviting you to my Community Funding Event " + event.getName()
                + ". Please click on the link below to check out my event.\n" + getProfileConfig().getKhwishWebBaseUrl()
                + WEB_GIFT_URL + event.getId();
    }
}
