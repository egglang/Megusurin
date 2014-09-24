package ma10.megusurin.lib.web;

import android.test.InstrumentationTestCase;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MegusurinEventCheckerTest extends InstrumentationTestCase
    implements MegusurinEventChecker.IMegusurinEventListener{

    private static final String TAG = MegusurinEventCheckerTest.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private CountDownLatch mGameStartLatch;

    private CountDownLatch mEncountLatch;

    private CountDownLatch mWaitBattlePrepare;

    private CountDownLatch mBattlePreparedLatch;

    public void test001CheckEvent() {

        mGameStartLatch = new CountDownLatch(1);
        mEncountLatch = new CountDownLatch(1);
        mWaitBattlePrepare = new CountDownLatch(1);
        mBattlePreparedLatch = new CountDownLatch(1);

        MegusurinEventChecker eventChecker = new MegusurinEventChecker(this);
        eventChecker.startEventCheck();

        try {
            boolean await = mGameStartLatch.await(5, TimeUnit.MINUTES);
            assertTrue(await);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            boolean await = mEncountLatch.await(5, TimeUnit.MINUTES);
            assertTrue(await);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            boolean await = mWaitBattlePrepare.await(5, TimeUnit.MINUTES);
            assertTrue(await);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            boolean await = mBattlePreparedLatch.await(5, TimeUnit.MINUTES);
            assertTrue(await);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        eventChecker.stopEventCheck();
    }

    @Override
    public void onGameStart() {
        Log.d(TAG, "onGameStart");
        mGameStartLatch.countDown();
    }

    @Override
    public void onEnemyEncount() {
        Log.d(TAG, "onEnemyEncount");
        mEncountLatch.countDown();
    }

    @Override
    public void onWaitBattlePrepare() {
        Log.d(TAG, "onWaitBattlePrepare");
        mWaitBattlePrepare.countDown();
    }

    @Override
    public void onBattlePrepared() {
        Log.d(TAG, "onBattlePrepared");
        mBattlePreparedLatch.countDown();
    }
}
