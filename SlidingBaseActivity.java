
/**
 * @author Kanghee Lee Created on 12-12-07
 */

public class SlidingBaseActivity extends BaseActivity implements OnFeedLoadedListener {

    public final String TAG = this.getClass().getSimpleName();

    protected ImageLoader mImageLoader;

    protected AutoScrollListView mNavDrawer;
    protected BuddiesFragment mBuddiesDrawer;

    protected BuddiesAdapter mBuddiesAdapter;
    protected TextView mEmptyView;

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected boolean isRefreshing = false;

    protected String mAuthToken;
    protected BuddyProfile mUser;

    protected BaseFeedFragment mFeedFrag;

    public void onCreate(Bundle savedInstanceState, int contentId) {
        super.onCreate(savedInstanceState);

        mImageLoader = SofitApplication.getImageLoader();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(contentId);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.right_drawer_shadow, GravityCompat.END);

        if (savedInstanceState != null) {
            mBuddiesDrawer = (BuddiesFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "mBuddiesFrag");
        } else {
            mBuddiesDrawer = new BuddiesFragment();
        }

        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.buddies_drawer, mBuddiesDrawer).commit();

        mEmptyView = (TextView) findViewById(R.id.empty_text);

        servicesConnected();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
                /*
                 * If the result code is Activity.RESULT_OK, try to connect
                 * again
                 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        /*
                         * Try the request again
                         */
                        break;
                }
                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.d(TAG, "Unknown Request Code: " + requestCode);

                break;
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            return true;
        } else {
            // Get the error code
            int errorCode = resultCode;
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                    LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                InfoDialogFragment errorFragment = new InfoDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getSupportFragmentManager();
        if (mBuddiesDrawer != null) {
            fm.putFragment(outState, "mBuddiesFrag", mBuddiesDrawer);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.buddies_list) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                if (mNavDrawer != null) {
                    if (mDrawerLayout.isDrawerOpen(mNavDrawer))
                        mDrawerLayout.closeDrawer(mNavDrawer);
                }
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateBuddyListOnUiThread(final ArrayList<Buddy> buddies) {
        isRefreshing = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (buddies != null) {
                    mBuddiesDrawer.refreshBuddies(buddies);
                }
                supportInvalidateOptionsMenu();
            }
        });
    }

    public void getBuddies() {
        isRefreshing = true;
        supportInvalidateOptionsMenu();
        ApiController.getBuddies(mBuddyRefreshApiListener,
                SofitSession.getAuthTokenFromPreferences(this),
                SofitSession.getUserFromPreferences(this).id);
    }

    protected ApiListener<ArrayList<Buddy>> mBuddyRefreshApiListener = new ApiListener<ArrayList<Buddy>>() {

        @Override
        public void onSuccess(ArrayList<Buddy> result, int statusCode) {
            populateBuddyListOnUiThread(result);
        }

        @Override
        public void onFailure(RequestException requestException) {
            populateBuddyListOnUiThread(null);
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    requestException.getUserMessage());
        }

        @Override
        public void onFailure(ApiException apiException, int statusCode) {
            populateBuddyListOnUiThread(null);
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    apiException.getUserMessage());
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void onDeauthorizedAuthToken() {
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    getString(R.string.auth_token_deauthorized_message));
        };
    };

    protected ApiListener<SettingsResponse> mUserSettingsApiListener = new ApiListener<SettingsResponse>() {

        @Override
        public void onSuccess(SettingsResponse settings, int statusCode) {
            SofitSession.saveUserSettings(SlidingBaseActivity.this, settings.settings);
        }

        @Override
        public void onFailure(RequestException requestException) {
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    requestException.getUserMessage());
        }

        @Override
        public void onFailure(ApiException apiException, int statusCode) {
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    apiException.getUserMessage());
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void onDeauthorizedAuthToken() {
            DisplayUtils.showToastOnUIThread(SlidingBaseActivity.this,
                    getString(R.string.auth_token_deauthorized_message));
        };
    };

    @Override
    public void onFeedLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isRefreshing = false;
                supportInvalidateOptionsMenu();
            }
        });
    }
    
    @Override
    public void onFeedLoadFail(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isRefreshing = false;
                supportInvalidateOptionsMenu();
            }
        });
    }
}
