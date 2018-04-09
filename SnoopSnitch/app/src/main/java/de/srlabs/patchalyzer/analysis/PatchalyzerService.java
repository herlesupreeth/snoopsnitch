package de.srlabs.patchalyzer.analysis;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import de.srlabs.patchalyzer.Constants;
import de.srlabs.patchalyzer.ITestExecutorCallbacks;
import de.srlabs.patchalyzer.ITestExecutorServiceInterface;
import de.srlabs.patchalyzer.views.PatchalyzerSumResultChart;
import de.srlabs.patchalyzer.util.ServerApi;
import de.srlabs.patchalyzer.helpers.NotificationHelper;
import de.srlabs.patchalyzer.helpers.SharedPrefsHelper;
import de.srlabs.snoopsnitch.R;

public class PatchalyzerService extends Service {
    private JSONObject deviceInfoJson = null;
    private TestSuite testSuite = null;
    private DeviceInfoThread deviceInfoThread = null;
    private BasicTestCache basicTestCache = null;
    private static ITestExecutorCallbacks callback = null;
    private boolean apiRunning = false;
    private boolean basicTestsRunning = false;
    private boolean deviceInfoRunning = false;
    private boolean downloadingTestSuite = false;
    private Handler handler = null;
    private ServerApi api = null;
    private Vector<ProgressItem> progressItems;
    public static final String NO_INTERNET_CONNECTION_ERROR = "no_uplink";
    private boolean isAnalysisRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(Constants.LOG_TAG,"onCreate() of PatchalyzerService called...");

        handler = new Handler();
        api = new ServerApi();
    }

    private void doWorkAsync() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    if (Constants.IS_TEST_MODE) {
                        mBinder.startWork(true, true, true, false, false);
                    } else {
                        mBinder.startWork(true, true, true, true, true);
                    }
                } catch (Exception e) {
                    Log.e(Constants.LOG_TAG, "startTest Exception", e);
                }
            }
        };
        t.start();
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.LOG_TAG,"PatchalyzerService.onDestroy called");
        isAnalysisRunning = false;
    }

    protected void cancelAnalysis() {
        Log.d(Constants.LOG_TAG,"PatchalyzerService.cancelAnalysis called");
        stopForeground(true);
        stopSelf();
        System.exit(0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void parseTestSuiteFile(File testSuiteFile,final ProgressItem parseTestSuiteProgress) throws IOException{
        Log.d(Constants.LOG_TAG,"PatchalyzerService: Parsing testsuite...");
        Log.d(Constants.LOG_TAG, "PatchalyzerService: testSuiteFile:" + testSuiteFile.getAbsolutePath());
        testSuite = new TestSuite(this, testSuiteFile);
        testSuite.parseInfoFromJSON();
        parseTestSuiteProgress.update(1.0);
        Log.i(Constants.LOG_TAG,"Parsing testsuite and additional data chunks finished.");
        basicTestCache = new BasicTestCache(this, testSuite.getVersion(), Build.VERSION.SDK_INT);
        Log.d(Constants.LOG_TAG,"PatchalyzerService: Finished parsing testsuite!");
    }

    @Override
    public boolean onUnbind(Intent intent) { //TODO needed?
        //Log.v(Constants.LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) { //TODO needed?
        //Log.v(Constants.LOG_TAG, "onRebind() called");
        super.onRebind(intent);
    }

    private boolean assertAppIsUpToDate(){
        if(testSuite != null && testSuite.getMinAppVersion() != -1){
            int minAppVersion = testSuite.getMinAppVersion();
            Log.i(Constants.LOG_TAG, "assertAppIsUpToDate(): Found minAppVersion=" + minAppVersion);

            boolean outdated = minAppVersion > Constants.APP_VERSION;
            //outdated = true;
            if(outdated){
                Log.i(Constants.LOG_TAG, "Outdated app version! minAppVersion=" + minAppVersion);
                /*String upgradeUrlTmp;
                if(testSuite.getUpdradeUrl() != null && !testSuite.getUpdradeUrl().equals("")){
                    upgradeUrlTmp = testSuite.getUpdradeUrl();
                } else {
                    upgradeUrlTmp = Constants.DEFAULT_APK_UPGRADE_URL;
                }*/
                String errorMessage = "<p style=\"font-weight:bold;\">"+getResources().getString(R.string.patchalyzer_new_version_available_heading)
                        +"</p>"+ getResources().getString(R.string.patchalyzer_new_version_available_instructions);
                        //+": <a href=\"" + upgradeUrlTmp + "\">" + upgradeUrlTmp + "</a>";
                handleFatalErrorViaCallback(errorMessage);
            }
            return outdated;
        } else{
            return false;
        }
    }

    private final ITestExecutorServiceInterface.Stub mBinder = new ITestExecutorServiceInterface.Stub() {

        @Override
        public void updateCallback(final ITestExecutorCallbacks callback){
            Log.d(Constants.LOG_TAG,"Updating callbacks.");
            PatchalyzerService.callback = callback;
            updateProgress();
        }

        @Override
        public void startMakingDeviceInfo() throws RemoteException {
            if(deviceInfoThread != null && deviceInfoThread.isAlive()){
                return;
            }
            if(deviceInfoJson != null)
                deviceInfoJson = null;
            deviceInfoThread = new DeviceInfoThread(new ProgressItem(null, "DUMMY", 1.0), null);
            deviceInfoThread.start();
        }

        @Override
        public void requestCancelAnalysis() {
            PatchalyzerService.this.cancelAnalysis();
        }

        @Override
        public boolean isDeviceInfoFinished() throws RemoteException {
            if(deviceInfoThread != null && deviceInfoThread.isAlive()){
                return false;
            }
            return deviceInfoJson != null;
        }

        @Override
        public String getDeviceInfoJson() throws RemoteException {
            try {
                return deviceInfoJson.toString(4);
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, "JSONException in getDeviceInfoJson()", e);
                return e.toString();
            }
        }

        @Override
        public void startBasicTests() throws RemoteException {
            basicTestCache.startWorking();
        }

        public String evaluateVulnerabilitiesTests() throws RemoteException{
            try {
                Log.d(Constants.LOG_TAG, "Starting to create result JSON...");
                boolean is64BitSystem = TestUtils.is64BitSystem();
                JSONObject result = new JSONObject();

                if(testSuite == null)
                    return null;

                Log.i(Constants.LOG_TAG,"Creating result overview...");
                JSONObject vulnerabilities = null;
                try {
                    vulnerabilities = testSuite.getVulnerabilities();
                } catch(IOException e){
                    Log.e(Constants.LOG_TAG, "Exception in evaluateVulnerabilitiesTests", e);
                    handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                    return e.toString();
                }
                Iterator<String> identifierIterator = vulnerabilities.keys();
                Vector<String> identifiers = new Vector<String>();

                while (identifierIterator.hasNext()) {
                    identifiers.add(identifierIterator.next());
                }
                Collections.sort(identifiers);
                Log.d(Constants.LOG_TAG, "number of vulnerabilities to test: " + identifiers.size());

                for (String identifier : identifiers) {
                    JSONObject vulnerability = vulnerabilities.getJSONObject(identifier);

                    try {
                        Boolean testRequires64Bit = vulnerability.getBoolean("testRequires64bit");
                        if (!is64BitSystem && testRequires64Bit) {
                            //test will not be possible; skip test
                            continue;
                        }
                    } catch (JSONException e) {
                        //ignoring exception here, as the old test might not come with this info
                    }

                    String category = vulnerability.getString("category");
                    JSONObject test_not_affected = vulnerability.getJSONObject("testNotAffected");
                    Boolean notAffected = TestEngine.runTest(basicTestCache, test_not_affected);

                    JSONObject vulnerabilityResult = new JSONObject();
                    vulnerabilityResult.put("identifier", identifier);
                    vulnerabilityResult.put("title", vulnerability.getString("title"));
                    vulnerabilityResult.put("notAffected", notAffected);

                    if (!notAffected) {
                        JSONObject testVulnerable = vulnerability.getJSONObject("testVulnerable");
                        Boolean vulnerable = TestEngine.runTest(basicTestCache, testVulnerable);
                        vulnerabilityResult.put("vulnerable", vulnerable);
                        JSONObject testFixed = vulnerability.getJSONObject("testFixed");
                        Boolean fixed = TestEngine.runTest(basicTestCache, testFixed);
                        vulnerabilityResult.put("fixed", fixed);
                    }
                    if (!result.has(category)) {
                        result.put(category, new JSONArray());
                    }
                    result.getJSONArray(category).put(vulnerabilityResult);
                }

                basicTestCache.clearTemporaryTestResultCache();
                PatchalyzerSumResultChart.setResultToDrawFromOnNextUpdate(result);

                return SharedPrefsHelper.saveAnalysisResult(result, PatchalyzerService.this);

            } catch (Exception e) {
                Log.e(Constants.LOG_TAG, "Exception in evaluateVulnerabilitiesTests", e);
                handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_error_creating_result_overview));
                return e.toString();
            }
        }

        @Override
        public void clearCache(){
            basicTestCache.clearCache();
        }

        @Override
        public boolean isAnalysisRunning() {
            return isAnalysisRunning;
        }

        @Override
        public void startWork(boolean updateTests, boolean generateDeviceInfo, final boolean evaluateTests, final boolean uploadTestResults, final boolean uploadDeviceInfo){

            if(downloadingTestSuite){
                Log.i(Constants.LOG_TAG,"Still downloading test suite...please be patient!");
                return;
            }
            if(assertAppIsUpToDate()){
                return;
            }
            if(apiRunning){
                Log.i(Constants.LOG_TAG,"Already work in progress, not starting: apiRunning");
                return;
            }
            if(basicTestsRunning) {
                Log.i(Constants.LOG_TAG,"Already work in progress, not starting: basicTestsRunning");
                return;
            }
            if(deviceInfoRunning){
                Log.i(Constants.LOG_TAG,"Already work in progress, not starting: deviceInfoRunning");
                return;
            }

            clearProgress();
            updateProgress();

            //prepare progressitem's
            final ProgressItem uploadDeviceInfoProgress;
            if(uploadDeviceInfo) {
                uploadDeviceInfoProgress = addProgressItem("uploadDeviceInfo", 2.0);
            } else{
                uploadDeviceInfoProgress = null;
            }

            final ProgressItem uploadTestResultsProgress;
            if(uploadTestResults) {
                uploadTestResultsProgress = addProgressItem("uploadTestResults", 1.0);
            } else{
                uploadTestResultsProgress = null;
            }

            final ProgressItem basicTestsProgress;
            if(evaluateTests) {
                basicTestsProgress = addProgressItem("basicTests", 6.0);
            } else{
                basicTestsProgress = null;
            }

            final ProgressItem downloadTestSuiteProgress;
            final ProgressItem parseTestSuiteProgress;
            if(updateTests){
                downloadTestSuiteProgress = addProgressItem("downloadTestSuite", 1);
                parseTestSuiteProgress = addProgressItem("parseTestSuite",1.5);
            }
            else{
                downloadTestSuiteProgress = null;
                parseTestSuiteProgress = null;
            }

            updateProgress();

            ProgressItem downloadRequestsProgress = addProgressItem("downloadRequests", 0.5);
            Thread requestsThread = new RequestsThread(downloadRequestsProgress);
            requestsThread.start();

            //prepare finish runnables
            final Runnable pendingTestResultsUploadRunnable = new Runnable(){
                @Override
                public void run() {
                    basicTestsRunning = false;
                    try{
                        if(uploadTestResults){
                            apiRunning = true;
                            if(!isConnectedToInternet()){
                                handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                                return;
                            }
                            Log.i(Constants.LOG_TAG,"Reporting test results to server...");
                            api.reportTest(basicTestCache.toJson(), TestUtils.getAppId(PatchalyzerService.this), TestUtils.getDeviceModel(), TestUtils.getBuildFingerprint(), TestUtils.getBuildDisplayName(), TestUtils.getBuildDateUtc(), Constants.APP_VERSION);
                            Log.i(Constants.LOG_TAG,"Uploading test results finished...");
                            uploadTestResultsProgress.update(1.0);
                            apiRunning = false;
                        }
                    } catch(IOException e){
                        reportError(NO_INTERNET_CONNECTION_ERROR);
                        handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                        Log.e(Constants.LOG_TAG, "Exception in pendingTestResultsUploadRunnable", e);
                        apiRunning = false;
                    } catch( JSONException e){
                        reportError(NO_INTERNET_CONNECTION_ERROR);
                        Log.e(Constants.LOG_TAG,"JSONException in pendingTestResultsUploadRunnable: "+e.getMessage());
                        handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                        apiRunning = false;
                    } catch(OutOfMemoryError e) {
                        Log.e(Constants.LOG_TAG,"OutOfMemoryError in pendingTestResultsUploadRunnable: "+e.getMessage());
                        handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_error_out_of_memory));
                    }
                }
            };

            final Runnable pendingDeviceInfoUploadRunnable = new Runnable(){
                @Override
                public void run() {
                    basicTestsRunning = false;
                    try{
                        if(uploadDeviceInfo){
                            apiRunning = true;
                            if (deviceInfoJson != null) {
                                if(!isConnectedToInternet()){
                                    handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                                    return;
                                }
                                api.reportSys(deviceInfoJson, TestUtils.getAppId(PatchalyzerService.this), TestUtils.getDeviceModel(), TestUtils.getBuildFingerprint(), TestUtils.getBuildDisplayName(), TestUtils.getBuildDateUtc(), Constants.APP_VERSION);
                            }
                            Log.i(Constants.LOG_TAG,"Uploading device info finished...");
                            uploadDeviceInfoProgress.update(1.0);
                            apiRunning = false;
                        }
                    } catch(IOException e){
                        reportError(NO_INTERNET_CONNECTION_ERROR);
                        Log.e(Constants.LOG_TAG, "Exception in pendingDeviceInfoUploadRunnable()", e);
                        handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                        apiRunning = false;
                    } catch(JSONException e){
                        Log.e(Constants.LOG_TAG,"JSONException in pendingDeviceInfoUploadRunnable: "+e.getMessage());
                        handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                        apiRunning = false;
                    }
                }
            };

            if(generateDeviceInfo) {
                ProgressItem deviceInfoProgress = addProgressItem("deviceInfo", 2);
                // Run sysinfo in background
                if (deviceInfoThread != null && deviceInfoThread.isAlive()) {
                    return;
                }
                if (deviceInfoJson != null)
                    deviceInfoJson = null;
                deviceInfoThread = new DeviceInfoThread(deviceInfoProgress, pendingDeviceInfoUploadRunnable);
                Log.i(Constants.LOG_TAG, "Starting DeviceInfoThread");
                deviceInfoRunning = true;
                deviceInfoThread.start();
            }

            if(updateTests){
                Thread downloadAndParseTestSuiteThread = new DownloadThread(downloadTestSuiteProgress, parseTestSuiteProgress,evaluateTests, basicTestsProgress, pendingTestResultsUploadRunnable);
                downloadAndParseTestSuiteThread.start();
            } else{
                if(evaluateTests){
                    Log.i(Constants.LOG_TAG, "Calling basicTestCache.startTesting()");
                    basicTestCache.startTesting(basicTestsProgress, pendingTestResultsUploadRunnable);
                }
            }
        }

    };

    private void checkIfCVETestsAvailable(TestSuite testSuite) {
        if(testSuite != null){
            String noCVETestsMessage = testSuite.getNoCVETestMessage();
            if(noCVETestsMessage != null){
                //show dialog in UI after testing
                showNoCVETestsForApiLevel(noCVETestsMessage);
            }
        }
        else{
            Log.e(Constants.LOG_TAG,"checkIfCVETestsAvailable: testSuite is null");
        }
    }

    public void finishedBasicTests(){
        Log.i(Constants.LOG_TAG,"Finished performing basic tests.");
        //vulnerabilitiesJSONResult = getJSONFromVulnerabilitiesResults();
    }

    private void clearProgress(){
        this.progressItems = new Vector<ProgressItem>();
    }

    private ProgressItem addProgressItem(String name, double weight){
        Log.d(Constants.LOG_TAG,"Adding progressItem: "+name+" weight:"+weight);
        ProgressItem item = new ProgressItem(this, name, weight);
        progressItems.add(item);
        return item;
    }

    private double getTotalProgress(){
        double weightSum = 0;
        double progressSum = 0;
        if(progressItems == null){
            return 0;
        }
        for(ProgressItem progressItem : progressItems){
            //Log.i(Constants.LOG_TAG, "getTotalProgres(): name=" + progressItem.getName() + "  weight=" + progressItem.getWeight() + "  progress=" + progressItem.getProgress());
            weightSum += progressItem.getWeight();
            progressSum += progressItem.getProgress() * progressItem.getWeight();
        }
        double totalProgress;
        if(weightSum == 0){
            totalProgress = 0;
        } else {
            totalProgress = progressSum / weightSum;
        }
        //Log.i(Constants.LOG_TAG, "getTotalProgres() returns "+ totalProgress);
        return totalProgress;
    }

    public void updateProgress() {
        double totalProgress = getTotalProgress();
        sendProgressToCallback(totalProgress);
        if(totalProgress == 1.0) {
            onFinishedAnalysis();
        }
    }

    private void onFinishedAnalysis() {
        String analysisResultString = null;
        try {
            analysisResultString = mBinder.evaluateVulnerabilitiesTests();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isAnalysisRunning = false;
        sendFinishedToCallback(analysisResultString);

        NotificationHelper.showAnalysisFinishedNotification(this);

        stopForeground(true);
        stopSelf();
    }

    private void sendFinishedToCallback(final String analysisResultString){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.finished(analysisResultString);
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService.updateProgress() => callback.finished() RemoteException", e);
                }
            }
        });
    }

    // Calling this will cause the service to be killed
    private void handleFatalErrorViaCallback(final String stickyErrorMessage){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.handleFatalError(stickyErrorMessage);
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService => callback.cancelled() RemoteException", e);
                }
                cancelAnalysis();
            }
        });
    }

    private void sendProgressToCallback(final double totalProgress){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.updateProgress(totalProgress);
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService.updateProgress() RemoteException", e);
                }
            }
        });
    }

    private void sendReloadViewStateToCallback(){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.reloadViewState();
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService.sendReloadViewStateToCallback() RemoteException", e);
                }
            }
        });
    }

    private void reportError(final String error){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.showErrorMessage(error);
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService.reportError() RemoteException", e);
                }
            }
        });
    }

    private void showNoCVETestsForApiLevel(final String message){
        handler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    callback.showNoCVETestsForApiLevel(message);
                } catch (RemoteException e) {
                    Log.e(Constants.LOG_TAG, "PatchalyzerService.showNoCVETestsForApiLevel() RemoteException", e);
                }
            }
        });
    }

    private class DownloadThread extends Thread{
        private ProgressItem downloadProgress;
        private ProgressItem parsingProgress;
        private boolean evaluateTests = false;
        private ProgressItem basicTestsProgress = null;
        private Runnable pendingTestResultsUploadRunnable = null;

        public DownloadThread(ProgressItem downloadProgress, ProgressItem parsingProgress, boolean evaluateTests, ProgressItem basicTestsProgress, Runnable pendingTestResultsUploadRunnable){
            this.evaluateTests = evaluateTests;
            this.downloadProgress = downloadProgress;
            this.parsingProgress = parsingProgress;
            this.basicTestsProgress = basicTestsProgress;
            this.pendingTestResultsUploadRunnable = pendingTestResultsUploadRunnable;
        }

        @Override
        public void run(){
            try {
                Log.i(Constants.LOG_TAG, "Starting to download testsuite");
                apiRunning = true;
                if(!isConnectedToInternet()){
                    handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                    return;
                }
                downloadingTestSuite = true;
                Log.d(Constants.LOG_TAG,"Downloading testsuite from server...");
                File f = api.downloadTestSuite("newtestsuite",PatchalyzerService.this,TestUtils.getAppId(PatchalyzerService.this), Build.VERSION.SDK_INT,"0", Constants.APP_VERSION);
                Log.i(Constants.LOG_TAG,"Downloading testsuite finished. Fetching additional data chunks...");
                downloadProgress.update(1.0);
                Log.d(Constants.LOG_TAG,"Finished downloading testsuite JSON to file:"+f.getAbsolutePath());
                downloadingTestSuite = false;
                parseTestSuiteFile(f,parsingProgress);
                if(assertAppIsUpToDate()) {
                    return;
                }
                checkIfCVETestsAvailable(testSuite);

            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, "JSONException in DownloadThread", e);
                reportError("JSONException in api.downloadTests" + e);
                return;
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "IOException in DownloadThread", e);
                reportError(NO_INTERNET_CONNECTION_ERROR);
                handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                return;
            } finally{
                apiRunning = false;
            }
            Log.i(Constants.LOG_TAG, "Calling basicTestCache.startTesting()");
            if(evaluateTests) {
                basicTestsRunning = true;
                basicTestCache.startTesting(basicTestsProgress, pendingTestResultsUploadRunnable);
            }
        }
    }

    private class DeviceInfoThread extends Thread{
        private ProgressItem progress;
        private Runnable onFinishedRunnable;

        public DeviceInfoThread(ProgressItem progress, Runnable onFinishedRunnable){
            this.progress = progress;
            this.onFinishedRunnable = onFinishedRunnable;
        }

        public void doNotRunFinishedRunnable(){
            this.onFinishedRunnable = null;
        }

        @Override
        public void run() {
            deviceInfoJson = TestUtils.makeDeviceinfoJson(PatchalyzerService.this, progress);
            deviceInfoThread = null;
            deviceInfoRunning = false;
            if(onFinishedRunnable != null) {
                onFinishedRunnable.run();
            }
        }
    }

    private class RequestsThread extends Thread{
        private ProgressItem downloadRequestsProgress;
        public RequestsThread(ProgressItem downloadRequestsProgress){
            this.downloadRequestsProgress = downloadRequestsProgress;
        }
        @Override
        public void run(){
            Vector<ProgressItem> requestProgress = new Vector<>();
            try {
                if(!isConnectedToInternet()){
                    handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                    return;
                }
                JSONArray requestsJson = api.getRequests(TestUtils.getAppId(PatchalyzerService.this), Build.VERSION.SDK_INT, TestUtils.getDeviceModel(), TestUtils.getBuildFingerprint(), TestUtils.getBuildDisplayName(), TestUtils.getBuildDateUtc(), Constants.APP_VERSION);
                downloadRequestsProgress.update(1.0);
                Log.i(Constants.LOG_TAG,"Downloading requests finished...");
                for(int i=0;i<requestsJson.length();i++){
                    Log.i(Constants.LOG_TAG, "Adding progress item for request " + i);
                    requestProgress.add(addProgressItem("Request_" + i, 1.0));
                }
                updateProgress();
                for(int i=0;i<requestsJson.length();i++){
                    JSONObject request = requestsJson.getJSONObject(i);
                    String requestType = request.getString("requestType");
                    if(requestType.equals("UPLOAD_FILE")){
                        String filename = request.getString("filename");
                        TestUtils.validateFilename(filename);
                        Log.d(Constants.LOG_TAG,"Uploading file: "+filename);
                        if(!isConnectedToInternet()){
                            handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                            return;
                        }
                        api.reportFile(filename, TestUtils.getAppId(PatchalyzerService.this), TestUtils.getDeviceModel(), TestUtils.getBuildFingerprint(), TestUtils.getBuildDisplayName(), TestUtils.getBuildDateUtc(), Constants.APP_VERSION);
                        requestProgress.get(i).update(1.0);
                        updateProgress();
                    } else{
                        Log.e(Constants.LOG_TAG, "Received invalid request type " + requestType);
                        requestProgress.get(i).update(1.0);
                        updateProgress();
                    }
                }
                Log.i(Constants.LOG_TAG,"Reporting files to server finished...");
            } catch(Exception e){
                Log.e(Constants.LOG_TAG, "RequestsThread.run() exception", e);
                if(e instanceof IOException) {
                    reportError(NO_INTERNET_CONNECTION_ERROR);
                    handleFatalErrorViaCallback(getResources().getString(R.string.patchalyzer_dialog_no_internet_connection_text));
                }
                downloadRequestsProgress.update(1.0);
                for(ProgressItem x:requestProgress){
                    x.update(1.0);
                }
                updateProgress();
            }
        }
    }

    private boolean isConnectedToInternet(){
        if(!TestUtils.isConnectedToInternet(PatchalyzerService.this)){
            reportError(NO_INTERNET_CONNECTION_ERROR);
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Log.v(Constants.LOG_TAG, "onStartCommand called");

        isAnalysisRunning = true;

        Notification notification = NotificationHelper.getAnalysisOngoingNotification(this);
        startForeground(NotificationHelper.ONGOING_NOTIFICATION_ID, notification);

        sendReloadViewStateToCallback();
        doWorkAsync();

        return START_NOT_STICKY;

        // stopSelf is called in updateProgress when 100% progress has been reached
    }

}