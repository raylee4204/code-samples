/**
 * @author Kanghee Lee Created on 12-10-08
 */

public class ApiController {

    @SuppressWarnings("unused")
    private static final String TAG = ApiController.class.getSimpleName();

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";
	
    private static final String API_BASE_URL = DEMO_BASE_URL + "/api/v4/";

    private static final String GET_USER_SETTINGS_URL = "users/%d/settings";
    private static final String UPDATE_USER_SETTINGS_URL = "users/%d/settings";
    private static final String COMPLETE_ACTIVITY_URL = "activities/%d";

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static final Gson mGson = gsonBuilder.create();

    public static void completeActivity(final Context context,
            final ApiListener<DetailedActivity> apiListener, int activityId, int time,
            String quantity, int calories, byte[] raceRoute, String authToken) {

        SofitRequestListener<InputStream> requestListener = new SofitRequestListener<InputStream>(
                apiListener) {
            @Override
            public void
                    handleOnSuccess(InputStream result, List<Cookie> cookies, int httpStatusCode)
                            throws JsonParseException {
                InputStreamReader inputStreamReader = new InputStreamReader(result);

                ActivityResponse completedActivity = mGson.fromJson(inputStreamReader,
                        ActivityResponse.class);
                if (httpStatusCode == OK && completedActivity.activity != null) {
                    apiListener.onSuccess(completedActivity.activity, httpStatusCode);
                } else {
                    checkFailureReasonForAuthTokenDeauthorized(completedActivity.reason,
                            completedActivity.reason, apiListener, httpStatusCode);
                }
                String json = null;
                try {
                    json = fromStream(result);
                } catch (Exception e) {
                    System.out.println("Error parsing json: " + json);
                    if (e instanceof JsonParseException)
                        throw (JsonParseException) e;
                    else
                        throw new RuntimeException(e);
                }
            }
        };

        JsonObject completeActivityJson = new JsonObject();
        completeActivityJson.addProperty("calories", calories);
        completeActivityJson.addProperty("quantity", quantity);
        completeActivityJson.addProperty("time", time);
        completeActivityJson.addProperty("current_time", TimeUtils.getCurrentTimeForServer());

        final InputStreamRequest inputStreamRequest = getMultipartRequest(requestListener,
                String.format(COMPLETE_ACTIVITY_URL, activityId), BasicRequest.PUT, authToken,
                completeActivityJson.toString(), raceRoute);
        RequestSession.getInstance().send(inputStreamRequest);
    }

    public static void getRaceRouteFromUrl(final ApiListener<RaceRouteResponse> apiListener,
            String url) {
        SofitRequestListener<InputStream> requestListener = new SofitRequestListener<InputStream>(
                apiListener) {
            @Override
            public void
                    handleOnSuccess(InputStream result, List<Cookie> cookies, int httpStatusCode)
                            throws JsonParseException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] buf = null;
                int len = 0;
                try {
                    buf = new byte[result.available()];
                    while ((len = result.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    e.getStackTrace();
                }
                RaceRouteResponse raceRouteResponse = new RaceRouteResponse();
                raceRouteResponse.raceRoute = buf;
                if (httpStatusCode == OK) {
                    apiListener.onSuccess(raceRouteResponse, httpStatusCode);
                } else {
                    apiListener.onFailure(
                            new ApiException("No Connection", SofitApplication
                                    .getStringForId(R.string.api_generic_failure)), httpStatusCode);
                }
            }
        };

        final InputStreamRequest inputStreamRequest = new InputStreamRequest(url, BasicRequest.GET,
                null, null, requestListener);
        inputStreamRequest.setConnectionTimeout(CONNECTION_TIMEOUT);
        inputStreamRequest.setReadTimeout(READ_TIMEOUT);
        RequestSession.getInstance().send(inputStreamRequest);
    }

    public static void getUserSettings(final ApiListener<SettingsResponse> apiListener,
            String authToken, int userId) {

        SofitRequestListener<InputStream> requestListener = new SofitRequestListener<InputStream>(
                apiListener) {

            @Override
            public void
                    handleOnSuccess(InputStream result, List<Cookie> cookies, int httpStatusCode)
                            throws JsonParseException {
                InputStreamReader inputStreamReader = new InputStreamReader(result);
                SettingsResponse settingsAvailable = mGson.fromJson(inputStreamReader,
                        SettingsResponse.class);

                if (httpStatusCode == OK) {
                    apiListener.onSuccess(settingsAvailable, httpStatusCode);
                } else {
                    checkFailureReasonForAuthTokenDeauthorized(settingsAvailable.reason,
                            settingsAvailable.reason, apiListener, httpStatusCode);
                }
            }
        };

        InputStreamRequest request = getAuthenticatedRequest(requestListener,
                String.format(GET_USER_SETTINGS_URL, userId), BasicRequest.GET, authToken, null);
        RequestSession.getInstance().send(request);
    }

    public static void updateUserSettings(final ApiListener<BaseResponse> apiListener,
            Settings settings, String authToken, int userId) {

        SofitRequestListener<InputStream> requestListener = new SofitRequestListener<InputStream>(
                apiListener) {

            @Override
            public void
                    handleOnSuccess(InputStream result, List<Cookie> cookies, int httpStatusCode)
                            throws JsonParseException {
                String errorMessage = SofitApplication
                        .getStringForId(R.string.error_saving_profile_to_server);
                InputStreamReader inputStreamReader = new InputStreamReader(result);
                BaseResponse apiResult = mGson.fromJson(inputStreamReader, BaseResponse.class);
                if (httpStatusCode == OK)
                    apiListener.onSuccess(null, httpStatusCode);
                else {
                    checkFailureReasonForAuthTokenDeauthorized(apiResult.reason, errorMessage,
                            apiListener, httpStatusCode);
                }
            }
        };

        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("lat_long_enabled", settings.locShareEnabled);
        updateJson.addProperty("distance_unit", settings.distanceUnit);
        updateJson.addProperty("weight_unit", settings.weightUnit);
        updateJson.addProperty("facebook_sharing", settings.fbShareEnabled);
        updateJson.addProperty("twitter_sharing", settings.twShareEnabled);

        InputStreamRequest request = getAuthenticatedRequest(requestListener,
                String.format(UPDATE_USER_SETTINGS_URL, userId), BasicRequest.PUT, authToken,
                updateJson.toString());
        RequestSession.getInstance().send(request);
    }

    private static InputStreamRequest getBasicPostInputStream(
            RequestListener<InputStream> requestListener, String url, String postData) {
        final InputStreamRequest inputStreamRequest = new InputStreamRequest(API_BASE_URL + url,
                BasicRequest.POST, null, null, requestListener);
        inputStreamRequest.addHeaderParam(ACCEPT, APPLICATION_JSON);
        inputStreamRequest.addHeaderParam(CONTENT_TYPE, APPLICATION_JSON);
        inputStreamRequest.addHeaderParam(SOFIT_VERSION, SofitApplication.getSoFitVersion());
        inputStreamRequest.addHeaderParam("Accept", "application/json");
        inputStreamRequest.setPostData(postData);
        inputStreamRequest.setConnectionTimeout(CONNECTION_TIMEOUT);
        inputStreamRequest.setReadTimeout(READ_TIMEOUT);
        return inputStreamRequest;
    }

    private static InputStreamRequest getRequest(RequestListener<InputStream> requestListener,
            String url, String requestType) {
        String requestUrl = API_BASE_URL + url;

        final InputStreamRequest inputStreamRequest = new InputStreamRequest(requestUrl,
                requestType, null, null, requestListener);
        inputStreamRequest.addHeaderParam(ACCEPT, APPLICATION_JSON);
        inputStreamRequest.addHeaderParam(CONTENT_TYPE, APPLICATION_JSON);
        inputStreamRequest.addHeaderParam(SOFIT_VERSION, SofitApplication.getSoFitVersion());
        inputStreamRequest.addHeaderParam("Accept", "application/json");

        inputStreamRequest.setConnectionTimeout(CONNECTION_TIMEOUT);
        inputStreamRequest.setReadTimeout(READ_TIMEOUT);

        return inputStreamRequest;
    }

    private static InputStreamRequest getAuthenticatedRequest(
            RequestListener<InputStream> requestListener, String url, String requestType,
            String authToken, String postData) {
        InputStreamRequest req = getRequest(requestListener, url, requestType);
        req.setAuthToken(authToken);
        if (postData != null)
            req.setPostData(postData);
        return req;
    }

    private static InputStreamRequest getMultipartRequest(
            RequestListener<InputStream> requestListener, String url, String requestType,
            String authToken, String raceData, byte[] raceRoute) {
        String requestUrl = API_BASE_URL + url;

        final InputStreamRequest inputStreamRequest = new InputStreamRequest(requestUrl,
                requestType, null, null, requestListener);
        inputStreamRequest.setAuthToken(authToken);
        inputStreamRequest.addHeaderParam(SOFIT_VERSION, SofitApplication.getSoFitVersion());
        inputStreamRequest.setConnectionTimeout(CONNECTION_TIMEOUT);
        inputStreamRequest.setReadTimeout(READ_TIMEOUT);
        inputStreamRequest.addHeaderParam("Accept", "application/json");

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ByteArrayBody raceRouteBody = new ByteArrayBody(raceRoute, "race_route_text.rr");

        try {
            entity.addPart("json", new StringBody(raceData, Charset.forName("UTF-8")));
            entity.addPart("race_route", raceRouteBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStreamRequest.setMultiPartData(entity);

        return inputStreamRequest;
    }

    private static void checkFailureReasonForAuthTokenDeauthorized(String reason,
            String userFacingReason, ApiListener<?> apiListener, int httpStatusCode) {
        if (httpStatusCode == UNAUTHORIZED) {
            SofitApplication.forceLogout();
            apiListener.onDeauthorizedAuthToken();
        } else if (reason.contains("connection")) {
            apiListener.onFailure(new ApiException(reason, userFacingReason), httpStatusCode);
            Log.d("API FAIL: ", reason);
        } else {
            apiListener.onFailure(new ApiException(reason, reason), httpStatusCode);
            Log.d("API FAIL: ", reason);
        }
    }
}
