package com.sohrab.obd.reader.trip;


import android.content.Context;

import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.constants.DefineObdReader;
import com.sohrab.obd.reader.enums.AvailableCommandNames;
import com.sohrab.obd.reader.enums.FuelType;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.SpeedCommand;
import com.sohrab.obd.reader.obdCommand.engine.LoadCommand;
import com.sohrab.obd.reader.obdCommand.engine.MassAirFlowCommand;
import com.sohrab.obd.reader.obdCommand.engine.RPMCommand;
import com.sohrab.obd.reader.obdCommand.engine.RuntimeCommand;
import com.sohrab.obd.reader.obdCommand.fuel.AirFuelRatioCommand;
import com.sohrab.obd.reader.obdCommand.fuel.ConsumptionRateCommand;
import com.sohrab.obd.reader.obdCommand.fuel.FindFuelTypeCommand;
import com.sohrab.obd.reader.obdCommand.fuel.WidebandAirFuelRatioCommand;
import com.sohrab.obd.reader.obdCommand.pressure.IntakeManifoldPressureCommand;
import com.sohrab.obd.reader.obdCommand.temperature.AirIntakeTemperatureCommand;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by sohrab on 30/11/2017.
 */

public class TripRecord implements DefineObdReader {
    private static Context sContext;
    int MINUS_ONE = -1;
    private static final int SPEED_GAP = 20;
    private static final float GRAM_TO_LITRE_GASOLIN = 748.9f;
    private static final float GRAM_TO_LITRE_DIESEL = 850.8f;
    private static final float DIESEL_DENSITY = 0.8508f;
    private static final float GRAM_TO_LITRE_CNG = 128.2f;
    private static final float GRAM_TO_LITRE_METHANOL = 786.6f;
    private static final float GRAM_TO_LITRE_ETHANOL = 789f;
    private static final float GRAM_TO_LITRE_PROPANE = 493f;
    private final static String ENGINE_RPM = "Engine RPM";
    private final static String VEHICLE_SPEED = "Vehicle Speed";
    private final static String ENGINE_RUNTIME = "Engine Runtime";
    private final static String MAF = "Mass Air Flow";
    private final static String FUEL_LEVEL = "Fuel Level";
    private final static String FUEL_TYPE = "Fuel Type";
    private final static String INTAKE_MANIFOLD_PRESSURE = "Intake Manifold Pressure";
    private final static String AIR_INTAKE_TEMPERATURE = "Air Intake Temperature";
    private final static String TROUBLE_CODES = "Trouble Codes";
    private final static String AMBIENT_AIR_TEMP = "Ambient Air Temperature";
    private final static String ENGINE_COOLANT_TEMP = "Engine Coolant Temperature";
    private final static String BAROMETRIC_PRESSURE = "Barometric Pressure";
    private final static String FUEL_PRESSURE = "Fuel Pressure";
    private final static String ENGINE_LOAD = "Engine Load";
    private final static String THROTTLE_POS = "Throttle Position";
    private final static String FUEL_CONSUMPTION_RATE = "Fuel Consumption Rate";
    private final static String TIMING_ADVANCE = "Timing Advance";
    private final static String PERMANENT_TROUBLE_CODES = "Permanent Trouble Codes";
    private final static String PENDING_TROUBLE_CODES = "Pending Trouble Codes";
    private final static String EQUIV_RATIO = "Command Equivalence Ratio";
    private final static String DISTANCE_TRAVELED_AFTER_CODES_CLEARED = "Distance since codes cleared";
    private final static String CONTROL_MODULE_VOLTAGE = "Control Module Power Supply ";
    private final static String ENGINE_FUEL_RATE = "Engine Fuel Rate";
    private final static String FUEL_RAIL_PRESSURE = "Fuel Rail Pressure";
    private final static String VIN = "Vehicle Identification Number (VIN)";
    private final static String DISTANCE_TRAVELED_MIL_ON = "Distance traveled with MIL on";
    private final static String DTC_NUMBER = "Diagnostic Trouble Codes";
    private final static String TIME_SINCE_TC_CLEARED = "Time since trouble codes cleared";
    private final static String REL_THROTTLE_POS = "Relative throttle position";
    private final static String ABS_LOAD = "Absolute load";
    private final static String ENGINE_OIL_TEMP = "Engine oil temperature";
    private final static String AIR_FUEL_RATIO = "Air/Fuel Ratio";
    private final static String WIDEBAND_AIR_FUEL_RATIO = "Wideband Air/Fuel Ratio";
    private final static String DESCRIBE_PROTOCOL = "Describe protocol";
    private final static String DESCRIBE_PROTOCOL_NUMBER = "Describe protocol number";
    private final static String IGNITION_MONITOR = "Ignition monitor";

    private    boolean ENGINE_RPM_AVAILABLE = false;
    private    boolean VEHICLE_SPEED_AVAILABLE = false;
    private    boolean ENGINE_RUNTIME_AVAILABLE = false;
    private    boolean MAF_AVAILABLE = false;
    private    boolean FUEL_LEVEL_AVAILABLE = false;
    private    boolean FUEL_TYPE_AVAILABLE = false;
    private    boolean INTAKE_MANIFOLD_PRESSURE_AVAILABLE = false;
    private    boolean AIR_INTAKE_TEMPERATURE_AVAILABLE = false;
    private    boolean TROUBLE_CODES_AVAILABLE = false;
    private    boolean AMBIENT_AIR_TEMP_AVAILABLE = false;
    private    boolean ENGINE_COOLANT_TEMP_AVAILABLE = false;
    private    boolean BAROMETRIC_PRESSURE_AVAILABLE = false;
    private    boolean FUEL_PRESSURE_AVAILABLE = false;
    private    boolean ENGINE_LOAD_AVAILABLE = false;
    private    boolean THROTTLE_POS_AVAILABLE = false;
    private    boolean FUEL_CONSUMPTION_RATE_AVAILABLE = false;
    private    boolean TIMING_ADVANCE_AVAILABLE = false;
    private    boolean PERMANENT_TROUBLE_CODES_AVAILABLE = false;
    private    boolean PENDING_TROUBLE_CODES_AVAILABLE = false;
    private    boolean EQUIV_RATIO_AVAILABLE = false;
    private    boolean DISTANCE_TRAVELED_AFTER_CODES_CLEARED_AVAILABLE = false;
    private    boolean CONTROL_MODULE_VOLTAGE_AVAILABLE = false;
    private    boolean ENGINE_FUEL_RATE_AVAILABLE = false;
    private    boolean FUEL_RAIL_PRESSURE_AVAILABLE = false;
    private    boolean VIN_AVAILABLE = false;
    private    boolean DISTANCE_TRAVELED_MIL_ON_AVAILABLE = false;
    private    boolean DTC_NUMBER_AVAILABLE = false;
    private    boolean TIME_SINCE_TC_CLEARED_AVAILABLE = false;
    private    boolean REL_THROTTLE_POS_AVAILABLE = false;
    private    boolean ABS_LOAD_AVAILABLE = false;
    private    boolean ENGINE_OIL_TEMP_AVAILABLE = false;
    private    boolean AIR_FUEL_RATIO_AVAILABLE = false;
    private    boolean WIDEBAND_AIR_FUEL_RATIO_AVAILABLE = false;
    private    boolean DESCRIBE_PROTOCOL_AVAILABLE = false;
    private    boolean DESCRIBE_PROTOCOL_NUMBER_AVAILABLE = false;
    private    boolean IGNITION_MONITOR_AVAILABLE = false;

    private long measurements = 0;
    private double fuelUsed = 0;
    private Integer engineRpmMax = 0;
    private String engineRpm;
    private Integer speed = -1;
    private Integer speedMax = 0;
    private String engineRuntime;
    private static TripRecord sInstance;
    private long tripStartTime;
    private float idlingDuration;
    private float drivingDuration;
    private long mAddSpeed;
    private long mSpeedCount;
    private float mDistanceTravel;
    private int mRapidAccTimes;
    private int mRapidDeclTimes;
    private float mInsFuelConsumption = 0.0f;
    private float mDrivingFuelConsumption = 0.0f;
    private float mIdlingFuelConsumption = 0.0f;
    private String mFuelLevel;
    private long mLastTimeStamp;
    private float mFuelTypeValue = 0; // default is diesel fuel ratio
    private float mDrivingMaf;
    private int mDrivingMafCount;
    private float mIdleMaf;
    private float mInsMAF = 0;
    private  float mInsLoad = 0;
    private int mIdleMafCount;
    private int mSecondAgoSpeed;
    private float gramToLitre = GRAM_TO_LITRE_DIESEL;
    private String mTripIdentifier;
    private boolean mIsMAFSupported = false;
    private boolean mIsEngineRuntimeSupported = false;
    private boolean mIsTempPressureSupported = false;
    private float mIntakeAirTemp = 0.0f;
    private float mIntakePressure = 0.0f;
    private double mWideBandAirFuelRatioValue = 0;
    private double mInsCO2_gs = 0;
    private double mInsCO2_gkm = 0;
    private String mFaultCodes = "";
    private String mAmbientAirTemp;
    private String mEngineCoolantTemp;
    private String mEngineOilTemp;
    private String mFuelConsumptionRate;
    private String mEngineFuelRate;
    private String mFuelPressure;
    private String mEngineLoad;
    private String mBarometricPressure;
    private String mThrottlePos;
    private String mTimingAdvance;
    private String mPermanentTroubleCode;
    private String mPendingTroubleCode;
    private String mEquivRatio;
    private String mDistanceTraveledAfterCodesCleared;
    private String mControlModuleVoltage;
    private String mFuelRailPressure;
    private String mVehicleIdentificationNumber;
    private String mDistanceTraveledMilOn;
    private String mDtcNumber;
    private String mTimeSinceTcClear;
    private String mRelThottlePos;
    private String mAbsLoad;
    private String mAirFuelRatio;
    private String mWideBandAirFuelRatio;
    private String mDescribeProtocol;
    private String mDescribeProtocolNumber;
    private String mIgnitionMonitor;





    private TripRecord() {
        tripStartTime = System.currentTimeMillis();
        mTripIdentifier = UUID.randomUUID().toString();
    }

    public static TripRecord getTripRecode(Context context) {
        sContext = context;
        if (sInstance == null)
            sInstance = new TripRecord();
        return sInstance;
    }

    public void clear() {
        sInstance = null;
    }

    public void setSpeed(Integer currentSpeed) {
        calculateIdlingAndDrivingTime(currentSpeed);
        findRapidAccAndDeclTimes(currentSpeed);
        speed = currentSpeed;
        if (speedMax < currentSpeed)
            speedMax = currentSpeed;

        // find travelled distance
        if (speed != 0) {
            mAddSpeed += speed;
            mSpeedCount++;

            mDistanceTravel = (mAddSpeed / mSpeedCount * (drivingDuration / (60 * 60 * 1000)));
        }

    }

    private void findRapidAccAndDeclTimes(Integer currentSpeed) {
        if (speed == -1)
            return;

        if (System.currentTimeMillis() - mLastTimeStamp > 1000) {

            int speedDiff = currentSpeed - mSecondAgoSpeed;
            boolean acceleration = speedDiff > 0;

            if (Math.abs(speedDiff) > SPEED_GAP) {

                if (acceleration)
                    mRapidAccTimes++;
                else
                    mRapidDeclTimes++;
            }

            mSecondAgoSpeed = currentSpeed;
            mLastTimeStamp = System.currentTimeMillis();
        }
    }

    private void calculateIdlingAndDrivingTime(int currentSpeed) {

        long currentTime = System.currentTimeMillis();
        if ((speed == -1 || speed == 0) && currentSpeed == 0) {
            idlingDuration = currentTime - tripStartTime - drivingDuration;
        }
        drivingDuration = currentTime - tripStartTime - idlingDuration;
    }


    public Integer getSpeed() {
        if (speed == -1)
            return 0;

        return speed;
    }


    public Integer getEngineRpmMax() {
        return this.engineRpmMax;
    }

    public float getDrivingDuration() {
        return drivingDuration / 60000; //time in minutes
    }

    public float getIdlingDuration() {
        return (idlingDuration / 60000); // time in minutes
    }

    public Integer getSpeedMax() {
        return speedMax;
    }

    public float getmDistanceTravel() {
        return mDistanceTravel;
    }

    public int getmRapidAccTimes() {
        return mRapidAccTimes;
    }

    public int getmRapidDeclTimes() {
        return mRapidDeclTimes;
    }

    public String getEngineRuntime() {
        return engineRuntime;
    }

    public String getmTripIdentifier() {
        return mTripIdentifier;
    }

    public float getmGasCost() {
        return (mIsMAFSupported || mIsTempPressureSupported) ? (mIdlingFuelConsumption + mDrivingFuelConsumption) * ObdPreferences.get(sContext.getApplicationContext()).getGasPrice() : MINUS_ONE;
    }

    public boolean ismIsMAFSupported() {
        return mIsMAFSupported;
    }

    public void setmIsMAFSupported(boolean mIsMAFSupported) {
        this.mIsMAFSupported = mIsMAFSupported;
    }

    public boolean ismIsEngineRuntimeSupported() {
        return mIsEngineRuntimeSupported;
    }

    public void setmIsEngineRuntimeSupported(boolean mIsEngineRuntimeSupported) {
        this.mIsEngineRuntimeSupported = mIsEngineRuntimeSupported;
    }

    public void findInsFualConsumption(float massAirFlow) {

        mInsMAF = massAirFlow;
//
        double grFuelPerSec = massAirFlow / mWideBandAirFuelRatioValue;
        mInsCO2_gs = grFuelPerSec * 3.168f;

        double grFuelPerHour = (grFuelPerSec) * 3600;

        //liters of fuel / HOUR
        double litersFuelPerHour = grFuelPerHour/ 1000 / DIESEL_DENSITY;
        if (speed > 0)
        {
            if(mInsLoad > 0)
            {
                mInsFuelConsumption = 100 * (float)(litersFuelPerHour) / speed; // in  litre/100km
                fuelUsed += mInsFuelConsumption;
                mInsCO2_gkm = (mInsCO2_gs * 3600) / speed;
                measurements++;
            }
            else
            {
                mInsFuelConsumption = 0;
                mInsCO2_gkm = 0;
                measurements++;
            }
        }
        else
        {
            mIdlingFuelConsumption = (float)litersFuelPerHour;
        }

    }

    public float getmInsFuelConsumption() {
        return (mIsMAFSupported || mIsTempPressureSupported) ? mInsFuelConsumption : MINUS_ONE;
    }

    public void setEngineRpm(String value) {
        engineRpm = value;
        if (value != null && this.engineRpmMax < Integer.parseInt(value)) {
            this.engineRpmMax = Integer.parseInt(value);
        }
    }

    public void findIdleAndDrivingFuelConsumtion(float currentMaf) {

        float literPerSecond = 0;
        if (speed > 0) {
            mDrivingMaf += currentMaf;
            mDrivingMafCount++;
            literPerSecond = ((((mDrivingMaf / mDrivingMafCount) / mFuelTypeValue) / gramToLitre));
            mDrivingFuelConsumption = (literPerSecond * (drivingDuration / 1000));

        } else {
            mIdleMaf += currentMaf;
            mIdleMafCount++;
            literPerSecond = ((((mIdleMaf / mIdleMafCount) / mFuelTypeValue) / gramToLitre));
            mIdlingFuelConsumption = (literPerSecond * (idlingDuration / 1000));
        }
    }


    public float getmDrivingFuelConsumption() {
        return (mIsMAFSupported || mIsTempPressureSupported) ? mDrivingFuelConsumption : MINUS_ONE;
    }

    public float getmIdlingFuelConsumption() {
        return (mIsMAFSupported || mIsTempPressureSupported) ? mIdlingFuelConsumption : MINUS_ONE;
    }

    public String getEngineRpm() {
        return engineRpm;
    }

    public String getmFaultCodes() {
        return mFaultCodes;
    }

    public void updateTrip(String name, ObdCommand command) {

        switch (name) {

            case VEHICLE_SPEED:
                setSpeed(((SpeedCommand) command).getMetricSpeed());
                VEHICLE_SPEED_AVAILABLE = command.isAvailable();
                break;

            case ENGINE_RPM:
                setEngineRpm(command.getCalculatedResult());
                setmIsEngineRuntimeSupported(false);
                ENGINE_RPM_AVAILABLE = command.isAvailable();
                break;

            case MAF:
                findInsFualConsumption(Float.parseFloat(command.getFormattedResult()));
                setmIsMAFSupported(false);
                MAF_AVAILABLE = command.isAvailable();
                break;

            case ENGINE_RUNTIME:
                engineRuntime = command.getFormattedResult();
                ENGINE_RUNTIME_AVAILABLE = command.isAvailable();
                break;

            case FUEL_LEVEL:
                mFuelLevel = command.getFormattedResult();
                FUEL_LEVEL_AVAILABLE = command.isAvailable();
                break;

            case FUEL_TYPE:
                if (ObdPreferences.get(sContext.getApplicationContext()).getFuelType() == 0)
                {
                    getFuelTypeValue(command.getFormattedResult());
                    FUEL_TYPE_AVAILABLE = command.isAvailable();
                }
                break;

            case INTAKE_MANIFOLD_PRESSURE:
                mIntakePressure = Float.parseFloat(command.getCalculatedResult());
                INTAKE_MANIFOLD_PRESSURE_AVAILABLE = command.isAvailable();
                break;

            case AIR_INTAKE_TEMPERATURE:
                mIntakeAirTemp = Float.parseFloat(command.getCalculatedResult()) + 273.15f;
                AIR_INTAKE_TEMPERATURE_AVAILABLE = command.isAvailable();
                break;

            case TROUBLE_CODES:
                mFaultCodes = command.getFormattedResult();
                TROUBLE_CODES_AVAILABLE = command.isAvailable();
                break;

            case AMBIENT_AIR_TEMP:
                mAmbientAirTemp = command.getFormattedResult();
                AMBIENT_AIR_TEMP_AVAILABLE = command.isAvailable();
                break;

            case ENGINE_COOLANT_TEMP:
                mEngineCoolantTemp = command.getFormattedResult();
                ENGINE_COOLANT_TEMP_AVAILABLE = command.isAvailable();
                break;

            case ENGINE_OIL_TEMP:
                mEngineOilTemp = command.getFormattedResult();
                ENGINE_OIL_TEMP_AVAILABLE = command.isAvailable();
                break;

            case FUEL_CONSUMPTION_RATE:
                mFuelConsumptionRate = command.getFormattedResult();
                FUEL_CONSUMPTION_RATE_AVAILABLE = command.isAvailable();
                break;

            case ENGINE_FUEL_RATE:
                mEngineFuelRate = command.getFormattedResult();
                ENGINE_FUEL_RATE_AVAILABLE = command.isAvailable();
                break;

            case FUEL_PRESSURE:
                mFuelPressure = command.getFormattedResult();
                FUEL_PRESSURE_AVAILABLE = command.isAvailable();
                break;


            case ENGINE_LOAD:
                mEngineLoad = command.getFormattedResult();
                mInsLoad = ((LoadCommand)command).getPercentage();
                ENGINE_LOAD_AVAILABLE = command.isAvailable();
                break;

            case BAROMETRIC_PRESSURE:
                mBarometricPressure = command.getFormattedResult();
                BAROMETRIC_PRESSURE_AVAILABLE = command.isAvailable();
                break;

            case THROTTLE_POS:
                mThrottlePos = command.getFormattedResult();
                THROTTLE_POS_AVAILABLE = command.isAvailable();
                break;

            case TIMING_ADVANCE:
                mTimingAdvance = command.getFormattedResult();
                TIMING_ADVANCE_AVAILABLE = command.isAvailable();
                break;

            case PERMANENT_TROUBLE_CODES:
                mPermanentTroubleCode = command.getFormattedResult();
                PERMANENT_TROUBLE_CODES_AVAILABLE = command.isAvailable();
                break;

            case PENDING_TROUBLE_CODES:
                mPendingTroubleCode = command.getFormattedResult();
                PENDING_TROUBLE_CODES_AVAILABLE = command.isAvailable();
                break;

            case EQUIV_RATIO:
                mEquivRatio = command.getFormattedResult();
                EQUIV_RATIO_AVAILABLE = command.isAvailable();
                break;

            case DISTANCE_TRAVELED_AFTER_CODES_CLEARED:
                mDistanceTraveledAfterCodesCleared = command.getFormattedResult();
                DISTANCE_TRAVELED_AFTER_CODES_CLEARED_AVAILABLE = command.isAvailable();
                break;

            case CONTROL_MODULE_VOLTAGE:
                mControlModuleVoltage = command.getFormattedResult();
                CONTROL_MODULE_VOLTAGE_AVAILABLE = command.isAvailable();
                break;

            case FUEL_RAIL_PRESSURE:
                mFuelRailPressure = command.getFormattedResult();
                FUEL_PRESSURE_AVAILABLE = command.isAvailable();
                break;

            case VIN:
                mVehicleIdentificationNumber = command.getFormattedResult();
                VIN_AVAILABLE = command.isAvailable();
                break;

            case DISTANCE_TRAVELED_MIL_ON:
                mDistanceTraveledMilOn = command.getFormattedResult();
                DISTANCE_TRAVELED_AFTER_CODES_CLEARED_AVAILABLE = command.isAvailable();
                break;

            case DTC_NUMBER:
                mDtcNumber = command.getFormattedResult();
                DTC_NUMBER_AVAILABLE = command.isAvailable();
                break;

            case TIME_SINCE_TC_CLEARED:
                mTimeSinceTcClear = command.getFormattedResult();
                TIME_SINCE_TC_CLEARED_AVAILABLE = command.isAvailable();
                break;

            case REL_THROTTLE_POS:
                mRelThottlePos = command.getFormattedResult();
                REL_THROTTLE_POS_AVAILABLE = command.isAvailable();
                break;

            case ABS_LOAD:
                mAbsLoad = command.getFormattedResult();
                ABS_LOAD_AVAILABLE = command.isAvailable();
                break;


            case WIDEBAND_AIR_FUEL_RATIO:
                mWideBandAirFuelRatio = command.getFormattedResult();
                mWideBandAirFuelRatioValue = ((WidebandAirFuelRatioCommand)command).getWidebandAirFuelRatio();
                WIDEBAND_AIR_FUEL_RATIO_AVAILABLE = command.isAvailable();
                break;

            case DESCRIBE_PROTOCOL:
                mDescribeProtocol = command.getFormattedResult();
                DESCRIBE_PROTOCOL_AVAILABLE = command.isAvailable();
                break;

            case DESCRIBE_PROTOCOL_NUMBER:
                DESCRIBE_PROTOCOL_NUMBER_AVAILABLE = command.isAvailable();
                mDescribeProtocolNumber = command.getFormattedResult();
                break;

            case IGNITION_MONITOR:
                IGNITION_MONITOR_AVAILABLE = command.isAvailable();
                mIgnitionMonitor = command.getFormattedResult();
                break;


        }

    }

    private void getFuelTypeValue(String fuelType) {
        float fuelTypeValue = 0;
        if (FuelType.GASOLINE.getDescription().equals(fuelType)) {
            fuelTypeValue = 14.7f;
            gramToLitre = GRAM_TO_LITRE_GASOLIN;
        } else if (FuelType.PROPANE.getDescription().equals(fuelType)) {
            fuelTypeValue = 15.5f;
            gramToLitre = GRAM_TO_LITRE_PROPANE;
        } else if (FuelType.ETHANOL.getDescription().equals(fuelType)) {
            fuelTypeValue = 9f;
            gramToLitre = GRAM_TO_LITRE_ETHANOL;
        } else if (FuelType.METHANOL.getDescription().equals(fuelType)) {
            fuelTypeValue = 6.4f;
            gramToLitre = GRAM_TO_LITRE_METHANOL;
        } else if (FuelType.DIESEL.getDescription().equals(fuelType)) {
            fuelTypeValue = 14.6f;
            gramToLitre = GRAM_TO_LITRE_DIESEL;
        } else if (FuelType.CNG.getDescription().equals(fuelType)) {
            fuelTypeValue = 17.2f;
            gramToLitre = GRAM_TO_LITRE_CNG;
        }

        if (fuelTypeValue != 0) {
            ObdPreferences.get(sContext.getApplicationContext()).setFuelType(mFuelTypeValue);
            mFuelTypeValue = fuelTypeValue;
        }
    }

    public void setmIsTempPressureSupported(boolean mIsTempPressureSupported) {
        this.mIsTempPressureSupported = mIsTempPressureSupported;
    }

    public boolean ismIsTempPressureSupported() {
        return mIsTempPressureSupported;
    }

    public String getmAmbientAirTemp() {
        return mAmbientAirTemp;
    }

    public String getmEngineCoolantTemp() {
        return mEngineCoolantTemp;
    }

    public String getmEngineOilTemp() {
        return mEngineOilTemp;
    }

    public String getmFuelConsumptionRate() {
        return mFuelConsumptionRate;
    }

    public String getmEngineFuelRate() {
        return mEngineFuelRate;
    }

    public String getmFuelPressure() {
        return mFuelPressure;
    }

    public String getmEngineLoad() {
        return mEngineLoad;
    }

    public String getmBarometricPressure() {
        return mBarometricPressure;
    }

    public String getmThrottlePos() {
        return mThrottlePos;
    }

    public String getmTimingAdvance() {
        return mTimingAdvance;
    }

    public String getmPermanentTroubleCode() {
        return mPermanentTroubleCode;
    }

    public String getmPendingTroubleCode() {
        return mPendingTroubleCode;
    }

    public String getmEquivRatio() {
        return mEquivRatio;
    }

    public String getmDistanceTraveledAfterCodesCleared() {
        return mDistanceTraveledAfterCodesCleared;
    }

    public String getmControlModuleVoltage() {
        return mControlModuleVoltage;
    }

    public String getmFuelRailPressure() {
        return mFuelRailPressure;
    }

    public String getmVehicleIdentificationNumber() {
        return mVehicleIdentificationNumber;
    }

    public String getmDistanceTraveledMilOn() {
        return mDistanceTraveledMilOn;
    }

    public String getmDtcNumber() {
        return mDtcNumber;
    }

    public String getmTimeSinceTcClear() {
        return mTimeSinceTcClear;
    }

    public String getmRelThottlePos() {
        return mRelThottlePos;
    }

    public String getmAbsLoad() {
        return mAbsLoad;
    }

    public String getmAirFuelRatio() {
        return mAirFuelRatio;
    }

    public String getmWideBandAirFuelRatio() {
        return mWideBandAirFuelRatio;
    }

    public String getmDescribeProtocol() {
        return mDescribeProtocol;
    }

    public String getmDescribeProtocolNumber() {
        return mDescribeProtocolNumber;
    }

    public String getmIgnitionMonitor() {
        return mIgnitionMonitor;
    }
    public float getCO2gramsPerSecond(){

        return (float)mInsCO2_gs;
    }

   private ArrayList<ObdCommand> mObdCommandArrayList;

    public ArrayList<ObdCommand> getmObdCommandArrayList() {

        if (mObdCommandArrayList == null) {

            mObdCommandArrayList = new ArrayList<>();
            mObdCommandArrayList.add(new SpeedCommand());
            mObdCommandArrayList.add(new RPMCommand());

            if (ismIsMAFSupported()) {
                mObdCommandArrayList.add(new MassAirFlowCommand());
            } else if (ismIsTempPressureSupported()) {
                mObdCommandArrayList.add(new IntakeManifoldPressureCommand());
                mObdCommandArrayList.add(new AirIntakeTemperatureCommand());
            }

            if (ismIsEngineRuntimeSupported()) {
                mObdCommandArrayList.add(new RuntimeCommand());
            }
            mObdCommandArrayList.add(new FindFuelTypeCommand());
        }
        return mObdCommandArrayList;
    }

    public void addObdCommand(ObdCommand obdCommand) {
        if (mObdCommandArrayList == null) {
            mObdCommandArrayList = new ArrayList<>();
        }
        mObdCommandArrayList.add(obdCommand);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        return "OBD data ::" +
                (VEHICLE_SPEED_AVAILABLE  ?  ("\n" + AvailableCommandNames.SPEED.getValue() + ":  " + speed + " km/h") : "\n" ) +
                (MAF_AVAILABLE ? ("\n" + AvailableCommandNames.MAF.getValue() + ":  " + mInsMAF + " g/s ") : "\n" ) +
//                (ENGINE_LOAD_AVAILABLE ? ("\n" + AvailableCommandNames.ENGINE_LOAD.getValue() + ": " + mInsLoad + " %") : "\n") +
                (THROTTLE_POS_AVAILABLE ? ("\n" + AvailableCommandNames.THROTTLE_POS.getValue() + ": " + mThrottlePos + " %" ): "\n" ) +
                (speed > 0 ? ("\n" + "Driving ins consumption : " + df.format(mInsFuelConsumption) + " L/100km" ) : "\n" + "Idling fuel consumption : " + df.format(mIdlingFuelConsumption) + "L/h") +
                (speed > 0 ? ("\n" + "Ins CO2 emissions : " + df.format(mInsCO2_gkm) + " g/km" ) : "\n" + "Idling CO2 emission : " + df.format(mInsCO2_gs)) +" g/s" +

                ("\nAVG consump: " + fuelUsed/measurements)+
//                (ENGINE_RPM_AVAILABLE  ? ("\n" + AvailableCommandNames.ENGINE_RPM.getValue() + ":  " + engineRpm) : "" )+
//                (ENGINE_RUNTIME_AVAILABLE  ? "\n" + AvailableCommandNames.ENGINE_RUNTIME.getValue() + ":  " + engineRuntime + "hh:mm:ss" : "") +
//                (TROUBLE_CODES_AVAILABLE ? "\n" + AvailableCommandNames.TROUBLE_CODES.getValue() + ":  " + mFaultCodes : "" )+

//                (FUEL_TYPE_AVAILABLE ? "\n" + AvailableCommandNames.FUEL_TYPE.getValue() + ":  " + mFuelTypeValue : "" )+
//                "\nRapid Acceleration Times: " + mRapidAccTimes +
//                "\nRapid Decleration Times: " + mRapidDeclTimes +
//                "\nMax Rpm: " + engineRpmMax +
//                "\nMax Speed: " + speedMax + " km/h" +
//                "\nDriving Duration: " + getDrivingDuration() + " minute" +
//                "\nIdle Duration: " + getIdlingDuration() + " minute" +
//                (DISTANCE_TRAVELED_AFTER_CODES_CLEARED_AVAILABLE ? "\n" + AvailableCommandNames.DISTANCE_TRAVELED_AFTER_CODES_CLEARED.getValue() + ":  " + getmDistanceTraveledAfterCodesCleared() : "") +
//                (DISTANCE_TRAVELED_MIL_ON_AVAILABLE ? "\n" + AvailableCommandNames.DISTANCE_TRAVELED_MIL_ON.getValue() + ":  " + mDistanceTraveledMilOn : "" )+
//                (INTAKE_MANIFOLD_PRESSURE_AVAILABLE ?  "\n" + AvailableCommandNames.INTAKE_MANIFOLD_PRESSURE.getValue() + ":  " + mIntakePressure + " kpa" : "") +
//                (AIR_INTAKE_TEMPERATURE_AVAILABLE ? "\n" + AvailableCommandNames.AIR_INTAKE_TEMP.getValue() + ":  " + mIntakeAirTemp + " C" : "" )+
//                (FUEL_CONSUMPTION_RATE_AVAILABLE ? "\n" + AvailableCommandNames.FUEL_CONSUMPTION_RATE.getValue() + ":  " + mFuelConsumptionRate + " L/h" : "" )+
//                (FUEL_LEVEL_AVAILABLE ? "\n" + AvailableCommandNames.FUEL_LEVEL.getValue() + ":  " + mFuelLevel : "" )+
//                (FUEL_PRESSURE_AVAILABLE ? "\n" + AvailableCommandNames.FUEL_PRESSURE.getValue() + ":  " + mFuelPressure : "") +
//                (ENGINE_FUEL_RATE_AVAILABLE ? "\n" + AvailableCommandNames.ENGINE_FUEL_RATE.getValue() + ":  " + mEngineFuelRate : "")+
//                (ENGINE_COOLANT_TEMP_AVAILABLE ? "\n" + AvailableCommandNames.ENGINE_COOLANT_TEMP.getValue() + ":  " + mEngineCoolantTemp : "" )+
//                (ENGINE_LOAD_AVAILABLE ? "\n" + AvailableCommandNames.ENGINE_LOAD.getValue() + ":  " + mEngineLoad :"") +
//                (ENGINE_OIL_TEMP_AVAILABLE ? "\n" + AvailableCommandNames.ENGINE_OIL_TEMP.getValue() + ":  " + mEngineOilTemp : "" )+
////
////
//                (BAROMETRIC_PRESSURE_AVAILABLE ? "\n" + AvailableCommandNames.BAROMETRIC_PRESSURE.getValue() + ":  " + mBarometricPressure  : "")+
//                (AIR_FUEL_RATIO_AVAILABLE ? "\n" + AvailableCommandNames.AIR_FUEL_RATIO.getValue() + ":  " + mAirFuelRatio : "")+
                (WIDEBAND_AIR_FUEL_RATIO_AVAILABLE ? "\n" + AvailableCommandNames.WIDEBAND_AIR_FUEL_RATIO.getValue() + ":  " + mWideBandAirFuelRatio : "" );
//                (ABS_LOAD_AVAILABLE ? "\n" + AvailableCommandNames.ABS_LOAD.getValue() + ":  " + mAbsLoad : "")+
//                (CONTROL_MODULE_VOLTAGE_AVAILABLE ? "\n" + AvailableCommandNames.CONTROL_MODULE_VOLTAGE.getValue() + ":  " + mControlModuleVoltage : "") +
//                (EQUIV_RATIO_AVAILABLE ? "\n" + AvailableCommandNames.EQUIV_RATIO.getValue() + ":  " + mEquivRatio : "") +
//                (DTC_NUMBER_AVAILABLE ? "\n" + AvailableCommandNames.DTC_NUMBER.getValue() + ":  " + mDtcNumber : "" )+
//                (DESCRIBE_PROTOCOL_AVAILABLE ? "\n" + AvailableCommandNames.DESCRIBE_PROTOCOL.getValue() + ":  " + mDescribeProtocol : "")+
//                (PENDING_TROUBLE_CODES_AVAILABLE ? "\n" + AvailableCommandNames.PENDING_TROUBLE_CODES.getValue() + ":  " + mPendingTroubleCode : "");

    }

}
