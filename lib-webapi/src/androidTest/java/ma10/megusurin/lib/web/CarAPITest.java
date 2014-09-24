package ma10.megusurin.lib.web;

import android.test.InstrumentationTestCase;
import android.util.Log;

public class CarAPITest extends InstrumentationTestCase {

    private static final String TAG = CarAPITest.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test001GetInfo() {
        CarInfoGetter getter = new CarInfoGetter();
        CarInfo info = getter.getCarInfo();
        assertNotNull(info);

        Log.d(TAG, info.toString());
    }
}
