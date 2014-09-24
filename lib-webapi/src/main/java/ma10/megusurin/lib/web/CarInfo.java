package ma10.megusurin.lib.web;

public class CarInfo {

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getAlat() {
        return mAlat;
    }

    public void setAlat(double mAlat) {
        this.mAlat = mAlat;
    }

    public double getALgt() {
        return mALgt;
    }

    public void setALgt(double mALgt) {
        this.mALgt = mALgt;
    }

    public double getYawRate() {
        return mYawRate;
    }

    public void setYawRate(double mYawRate) {
        this.mYawRate = mYawRate;
    }

    public int getAccelPedalRate() {
        return mAccelPedalRate;
    }

    public void setAccelPedalRate(int mAccelPedalRate) {
        this.mAccelPedalRate = mAccelPedalRate;
    }

    public boolean isBreakOn() {
        return mBreakOn;
    }

    public void setBreakOn(boolean mBreakOn) {
        this.mBreakOn = mBreakOn;
    }

    public int getSteerAg() {
        return mSteerAg;
    }

    public void setSteerAg(int mSteerAg) {
        this.mSteerAg = mSteerAg;
    }

    public String getGearPos() {
        return mGearPos;
    }

    public void setGearPos(String mGearPos) {
        this.mGearPos = mGearPos;
    }

    public int getEngN() {
        return mEngN;
    }

    public void setEngN(int mEngN) {
        this.mEngN = mEngN;
    }

    public int getOdoDst() {
        return mOdoDst;
    }

    public void setOdoDst(int mOdoDst) {
        this.mOdoDst = mOdoDst;
    }

    public int getDriveMode() {
        return mDriveMode;
    }

    public void setDriveMode(int mDriveMode) {
        this.mDriveMode = mDriveMode;
    }

    public int getEcoDrvgSts() {
        return mEcoDrvgSts;
    }

    public void setEcoDrvgSts(int mEcoDrvgSts) {
        this.mEcoDrvgSts = mEcoDrvgSts;
    }

    public boolean isParkingBrkOn() {
        return mParkingBrkOn;
    }

    public void setParkingBrkOn(boolean mParkingBrkOn) {
        this.mParkingBrkOn = mParkingBrkOn;
    }

    public int getSysPowerSts() {
        return mSysPowerSts;
    }

    public void setSysPowerSts(int mSysPowerSts) {
        this.mSysPowerSts = mSysPowerSts;
    }

    public int getRestFu() {
        return mRestFu;
    }

    public void setRestFu(int mRestFu) {
        this.mRestFu = mRestFu;
    }

    public double getCnsFu() {
        return mCnsFu;
    }

    public void setCnsFu(double mCnsFu) {
        this.mCnsFu = mCnsFu;
    }

    public int getEngTemp() {
        return mEngTemp;
    }

    public void setEngTemp(int mEngTemp) {
        this.mEngTemp = mEngTemp;
    }

    public int getOutTemp() {
        return mOutTemp;
    }

    public void setOutTemp(int mOutTemp) {
        this.mOutTemp = mOutTemp;
    }

    public int getHeadLightSts() {
        return mHeadLightSts;
    }

    public void setHeadLightSts(int mHeadLightSts) {
        this.mHeadLightSts = mHeadLightSts;
    }

    public int getWiperSts() {
        return mWiperSts;
    }

    public void setWiperSts(int mWiperSts) {
        this.mWiperSts = mWiperSts;
    }

    public int getDoorSts() {
        return mDoorSts;
    }

    public void setDoorSts(int mDoorSts) {
        this.mDoorSts = mDoorSts;
    }

    public int getDoorLockSts() {
        return mDoorLockSts;
    }

    public void setDoorLockSts(int mDoorLockSts) {
        this.mDoorLockSts = mDoorLockSts;
    }

    public int getWindowSts() {
        return mWindowSts;
    }

    public void setWindowSts(int mWindowSts) {
        this.mWindowSts = mWindowSts;
    }

    public static class LatLng {
        double lat;
        double lng;
    }

    public static final int DRIVE_MODE_NORMAL = 0;
    public static final int DRIVE_MODE_ECO = 1;
    public static final int DRIVE_MODE_POWER = 2;

    public static final int ECO_STATUS_UNKNOWN = 0;
    public static final int ECO_STATUS_PARKING = 1;
    public static final int ECO_STATUS_OFF = 2;
    public static final int ECO_STATUS_ON = 3;

    public static final int SYS_POWER_OFF = 0;
    public static final int SYS_POWER_AC = 1;
    public static final int SYS_POWER_ON = 2;

    public static final int HEAD_LIGHT_OFF = 0;
    public static final int HEAD_LIGHT_LOW = 1;
    public static final int HEAD_LIGHT_HIGH = 2;

    public static final int WIPER_OFF = 0;
    public static final int WIPER_INT = 0;
    public static final int WIPER_LOW = 0;
    public static final int WIPER_HIGH = 0;

    private LatLng mLatLng;

    private double mSpeed;

    private double mAlat;

    private double mALgt;

    private double mYawRate;

    private int mAccelPedalRate;

    private boolean mBreakOn;

    private int mSteerAg;

    private String mGearPos;

    private int mEngN;

    private int mOdoDst;

    private int mDriveMode;

    private int mEcoDrvgSts;

    private boolean mParkingBrkOn;

    private int mSysPowerSts;

    private int mRestFu;

    private double mCnsFu;

    private int mEngTemp;

    private int mOutTemp;

    private int mHeadLightSts;

    private int mWiperSts;

    private int mDoorSts;

    private int mDoorLockSts;

    private int mWindowSts;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Car info : \n");
        sb.append("\t Lat : " + mLatLng.lat);
        sb.append("\n");
        sb.append("\t Lon : " + mLatLng.lng);
        sb.append("\n");
        sb.append("\t System Power : " + mSysPowerSts);
        sb.append("\n");
        sb.append("\t Speed : " + mSpeed);
        sb.append("\n");
        sb.append("\t Parking Break : " + mParkingBrkOn);
        sb.append("\n");
        sb.append("\t DriveMode : " + mDriveMode);
        sb.append("\n");
        sb.append("\t EcoMode : " + mEcoDrvgSts);
        sb.append("\n");
        sb.append("\t GearPos : " + mGearPos);
        sb.append("\n");
        sb.append("\t EngN : " + mEngN);
        sb.append("\n");
        sb.append("\t ODO : " + mOdoDst);
        sb.append("\n");
        sb.append("\t Eng T : " + mEngTemp);
        sb.append("\n");
        sb.append("\t Out T : " + mOutTemp);
        sb.append("\n");

        return sb.toString();
    }
}
