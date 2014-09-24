package ma10.megusurin.lib.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarInfoParser {

    private static final String TAG = CarInfoParser.class.getSimpleName();

    private static final String KEY_VIINFO = "vehicleinfo";
    private static final String KEY_DATA = "data";
    private static final String KEY_POSN = "Posn";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_SPD = "Spd";
    private static final String KEY_ALAT = "ALat";
    private static final String KEY_ALGT = "ALgt";
    private static final String KEY_YAWRATE = "YawRate";
    private static final String KEY_ACCRPEDLRAT = "AccrPedlRat";
    private static final String KEY_BRKINDCR = "BrkIndcr";
    private static final String KEY_STEERAR = "SteerAg";
    private static final String KEY_TRSMGEARPOSN = "TrsmGearPosn";
    private static final String KEY_ENGN = "EngN";
    private static final String KEY_ODODST = "OdoDst";
    private static final String KEY_DRVGMOD = "DrvgMod";
    private static final String KEY_ECODRVGSTS = "EcoDrvgSts";
    private static final String KEY_PRKGBRK = "PrkgBrk";
    private static final String KEY_SYSPWRSTS = "SysPwrSts";
    private static final String KEY_RESTFU = "RestFu";
    private static final String KEY_CNSFU = "CnsFu";
    private static final String KEY_ENGT = "EngT";
    private static final String KEY_OUTDT = "OutdT";
    private static final String KEY_HDLAMPLTGINDCN = "HdLampLtgIndcn";
    private static final String KEY_WIPRSTS = "WiprSts";
    private static final String KEY_DOORSTS = "DoorSts";
    private static final String KEY_DOORLOCKPOSN = "DoorLockPosn";
    private static final String KEY_PWRWINACTTN = "PwrWinActtn";


    public static CarInfo parse(final String src) {

        CarInfo info = null;

        try {
            JSONObject jsonObject = new JSONObject(src);

            if (jsonObject.has(KEY_VIINFO)) {
                JSONArray jsonArray = jsonObject.getJSONArray(KEY_VIINFO);

                JSONObject viInfoObject = jsonArray.getJSONObject(0);
                if(viInfoObject != null) {

                    if (viInfoObject.has(KEY_DATA)) {
                        JSONArray jsonArrayData = viInfoObject.getJSONArray(KEY_DATA);

                        JSONObject dataObject = jsonArrayData.getJSONObject(0);
                        if (dataObject != null) {
                            info = new CarInfo();

                            if (dataObject.has(KEY_POSN)) {
                                JSONObject posnObj = dataObject.getJSONObject(KEY_POSN);
                                if (posnObj.has(KEY_LAT) && posnObj.has(KEY_LON)) {
                                    CarInfo.LatLng latLng = new CarInfo.LatLng();
                                    latLng.lat = posnObj.getDouble(KEY_LAT);
                                    latLng.lng = posnObj.getDouble(KEY_LON);
                                    info.setLatLng(latLng);
                                }
                            }

                            if (dataObject.has(KEY_SPD)) {
                                info.setSpeed(dataObject.getDouble(KEY_SPD));
                            }

                            if (dataObject.has(KEY_ALAT)) {
                                info.setAlat(dataObject.getDouble(KEY_ALAT));
                            }

                            if (dataObject.has(KEY_ALGT)) {
                                info.setALgt(dataObject.getDouble(KEY_ALGT));
                            }

                            if (dataObject.has(KEY_YAWRATE)) {
                                info.setYawRate(dataObject.getDouble(KEY_YAWRATE));
                            }

                            if (dataObject.has(KEY_ACCRPEDLRAT)) {
                                info.setAccelPedalRate(dataObject.getInt(KEY_ACCRPEDLRAT));
                            }

                            if (dataObject.has(KEY_STEERAR)) {
                                info.setSteerAg(dataObject.getInt(KEY_STEERAR));
                            }

                            if (dataObject.has(KEY_TRSMGEARPOSN)) {
                                info.setGearPos(dataObject.getString(KEY_TRSMGEARPOSN));
                            }

                            if (dataObject.has(KEY_ENGN)) {
                                info.setEngN(dataObject.getInt(KEY_ENGN));
                            }

                            if (dataObject.has(KEY_ECODRVGSTS)) {
                                info.setEcoDrvgSts(dataObject.getInt(KEY_ECODRVGSTS));
                            }

                            if (dataObject.has(KEY_PRKGBRK)) {
                                int value = dataObject.getInt(KEY_PRKGBRK);
                                if (value == 0) {
                                    info.setParkingBrkOn(false);
                                } else {
                                    info.setParkingBrkOn(true);
                                }
                            }

                            if (dataObject.has(KEY_SYSPWRSTS)) {
                                info.setSysPowerSts(dataObject.getInt(KEY_SYSPWRSTS));
                            }

                            if (dataObject.has(KEY_CNSFU)) {
                                info.setCnsFu(dataObject.getDouble(KEY_CNSFU));
                            }

                            if (dataObject.has(KEY_DOORSTS)) {
                                info.setDoorSts(dataObject.getInt(KEY_DOORSTS));
                            }

                            if (dataObject.has(KEY_DOORLOCKPOSN)) {
                                info.setDoorLockSts(dataObject.getInt(KEY_DOORLOCKPOSN));
                            }

                            if (dataObject.has(KEY_PWRWINACTTN)) {
                                info.setWindowSts(dataObject.getInt(KEY_PWRWINACTTN));
                            }

                            if (dataObject.has(KEY_ENGT) && !dataObject.isNull(KEY_ENGT)) {
                                info.setEngTemp(dataObject.getInt(KEY_ENGT));
                            }

                            if (dataObject.has(KEY_BRKINDCR) && !dataObject.isNull(KEY_BRKINDCR)) {
                                int value = dataObject.getInt(KEY_BRKINDCR);
                                if (value == 0) {
                                    info.setBreakOn(false);
                                } else {
                                    info.setBreakOn(true);
                                }
                            }

                            if (dataObject.has(KEY_HDLAMPLTGINDCN) && !dataObject.isNull(KEY_HDLAMPLTGINDCN)) {
                                info.setHeadLightSts(dataObject.getInt(KEY_HDLAMPLTGINDCN));
                            }

                            if (dataObject.has(KEY_WIPRSTS) && !dataObject.isNull(KEY_WIPRSTS)) {
                                info.setWiperSts(dataObject.getInt(KEY_WIPRSTS));
                            }

                            if (dataObject.has(KEY_DRVGMOD) && !dataObject.isNull(KEY_DRVGMOD)) {
                                info.setDriveMode(dataObject.getInt(KEY_DRVGMOD));
                            }

                            if (dataObject.has(KEY_OUTDT) && !dataObject.isNull(KEY_OUTDT)) {
                                info.setOutTemp(dataObject.getInt(KEY_OUTDT));
                            }

                            if (dataObject.has(KEY_RESTFU) && !dataObject.isNull(KEY_RESTFU)) {
                                info.setRestFu(dataObject.getInt(KEY_RESTFU));
                            }

                            if (dataObject.has(KEY_ODODST) && !dataObject.isNull(KEY_ODODST)) {
                                info.setOdoDst(dataObject.getInt(KEY_ODODST));
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

}
