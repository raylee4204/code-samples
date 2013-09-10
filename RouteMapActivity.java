
/**
 * @author Ray Created on 2013-07-05
 */

public class RouteMapActivity extends SherlockFragmentActivity {

    public static final String LAT_LNG_LIST = "extra_route";
    private GoogleMap map;
    private List<LatLng> mPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_map);
        mPoints = getIntent().getParcelableArrayListExtra(LAT_LNG_LIST);
        setUpMapIfNeeded();
        if (mPoints != null)
            initMapView();
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(DisplayUtils.changeFont(this, getTitle(), DisplayUtils.CHUNK_FIVE_FONT));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view))
                    .getMap();
        }
    }

    private void initMapView() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                PolylineOptions a = new PolylineOptions().addAll(mPoints).color(Color.GREEN)
                        .width(5);
                MarkerOptions start = new MarkerOptions().position(mPoints.get(0)).title("Start")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                        .anchor(0.5f, 0.9f);
                MarkerOptions end = new MarkerOptions().position(mPoints.get(mPoints.size() - 1))
                        .title("Finish").anchor(0.5f, 0.9f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker));
                map.addPolyline(a);
                map.addMarker(start);
                map.addMarker(end);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng m : mPoints) {
                    builder.include(m);
                }
                final LatLngBounds bounds = builder.build();

                map.setOnCameraChangeListener(new OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition arg0) {
                        // Move camera.
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                        // Remove listener to prevent position reset on camera
                        // move.
                        map.setOnCameraChangeListener(null);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
