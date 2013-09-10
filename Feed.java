
/**
 * @author Kanghee Lee Created on 13-02-04
 */

public abstract class Feed implements Comparable<Feed> {

    public static final String NEW_CHALLENGE = "new_challenge";
    public static final String NEW_USER = "new_account";
    public static final String ACTIVITY_COMPLETED = "did_activity";
    public static final String JOINED_CHALLENGE = "joined_challenge";
    public static final String WEB_PAGE = "advert";

    public String date;
    public String type;
    public String description;

    private DateTimeComparator mComparator;

    public Feed() {
        mComparator = DateTimeComparator.getInstance();
    }

    public String getDate() throws ParseException {
        return TimeUtils.getDateTimeFromServer(date);
    }

    @Override
    public int compareTo(Feed another) {
        return mComparator.compare(new DateTime(another.date), new DateTime(date));
    }
    
    public abstract int getType();
}
