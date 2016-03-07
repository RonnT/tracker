package jp.co.skybus.tracker.api;

import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.co.skybus.tracker.api.service.ApiService;
import jp.co.skybus.tracker.helper.PrefsHelper;
import jp.co.skybus.tracker.model.DefaultResponseWrapper;
import jp.co.skybus.tracker.model.Info;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class Api {

    private static final String
            PARAM_LAT = "lat",
            PARAM_LNG = "lng",
            PARAM_TYPE = "type",
            PARAM_PAGE = "page",
            PARAM_LIMIT = "limit",
            PARAM_TIMESTAMP = "timestamp",
            PARAM_KEY = "key",
            PARAM_REGION = "region",
            PARAM_SERIES = "series",
            PARAM_NUMBER = "number",
            PARAM_BIRTH_DATE = "birth_date",
            PARAM_FIELD = "field",
            PARAM_DISTRICT = "district",
            PARAM_DOCUMENT = "document",
            PARAM_OPERATION = "operation",
            PARAM_DEPARTMENT = "department",
            PARAM_DATE = "date",

            VALUE_REGION = "region",
            VALUE_DISTRICT = "district",
            VALUE_DOCUMENT = "document",
            VALUE_OPERATION = "operation",
            VALUE_DEPARTMENT = "department",
            VALUE_TIME = "time";

    private static final String TAG = "RETROFIT";

    private static ApiService sApiService = createService(ApiService.class, PrefsHelper.getInstance().getServerAddress());

    protected static <S> S createService(Class<S> serviceClass, String pUrl) {

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.networkInterceptors().add(new StethoInterceptor());

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(pUrl)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL).
                        setLog(new RestAdapter.Log() {
                            @Override
                            public void log(String msg) {
                                Log.i(TAG, msg);
                            }
                        }).build();

        return adapter.create(serviceClass);
    }

    public static void sendData(List<Info> pData, Callback<DefaultResponseWrapper> pCallback){
        sApiService.sendData(pData, pCallback);
    }

    public static void refreshApiUrl(){
        sApiService = createService(ApiService.class, PrefsHelper.getInstance().getServerAddress());
    }

/*
    public static void getCurrentRegionId(double pLatitude, double pLongitude, Callback<RegionIdWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_LAT, String.valueOf(pLatitude));
        query.put(PARAM_LNG, String.valueOf(pLongitude));
        sApiService.getRegion(query, pCallback);
    }

    public static void getAppealDictionary(Callback<AppealDictionary> pCallback) {
        sApiService.getAppealDictionary(pCallback);
    }

    public static void sendAppeal(Appeal pAppeal, Callback<DefaultMessageWrapper> pCallback) {
        MultipartTypedOutput output = new MultipartTypedOutput();
        output.addPart("surname", new TypedString(pAppeal.getSurname()));
        output.addPart("firstname", new TypedString(pAppeal.getFirstname()));
        output.addPart("patronymic", new TypedString(pAppeal.getPatronymic()));
        output.addPart("country_id", new TypedString(pAppeal.getCountry_id()));
        output.addPart("region_id", new TypedString(pAppeal.getRegion_id()));
        output.addPart("phone", new TypedString(pAppeal.getPhone()));
        output.addPart("response_type", new TypedString(String.valueOf(pAppeal.getResponseType())));
        output.addPart("is_secondary", new TypedString(String.valueOf(pAppeal.isSecondary())));
        output.addPart("subject_id", new TypedString(pAppeal.getSubjectId()));
        output.addPart("site_id", new TypedString(pAppeal.getSiteId()));
        output.addPart("text", new TypedString(pAppeal.getText()));
        output.addPart("is_confirm", new TypedString(String.valueOf(pAppeal.isConfirm())));
        output.addPart("email", new TypedString(pAppeal.getEmail()));
        output.addPart("address_zip", new TypedString(pAppeal.getAddressZip()));
        output.addPart("address_city", new TypedString(pAppeal.getAddressCity()));
        output.addPart("address_street", new TypedString(pAppeal.getAddressStreet()));
        output.addPart("address_building", new TypedString(pAppeal.getAddressBuilding()));
        output.addPart("address_building2", new TypedString(pAppeal.getAddressBuilding2()));
        output.addPart("address_room", new TypedString(pAppeal.getAddressRoom()));
        for(int i = 0; i < pAppeal.getFileupload().size(); i++){
            File file = pAppeal.getFileupload().get(i);
            output.addPart("fileupload_" + String.valueOf(i), new TypedFile("multipart/form-data", file));
        }
        sApiService.sendAppeal(output, pCallback);
    }

    public static void getConsultationDictionary(Callback<AppealDictionary> pCallback) {
        sApiService.getConsultationDictionary(pCallback);
    }

    public static void sendConsultation(Appeal pAppeal, Callback<DefaultMessageWrapper> pCallback) {
        sApiService.sendConsultation(pAppeal, pCallback);
    }

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public static void getPolls(int pPage, int pLimit, Callback<PollsWrapper> pCallback) {
        Map<String, String> map = new HashMap<>();
        if (pPage != CONST.NOT_DEFINED) map.put(PARAM_PAGE, String.valueOf(pPage));
        if (pLimit != CONST.NOT_DEFINED) map.put(PARAM_LIMIT, String.valueOf(pLimit));
        sApiService.getPolls(map, pCallback);
    }

    public static void getPollById(int pId, Callback<Poll> pCallback) {
        sApiService.getPollById(pId, pCallback);
    }

    public static void sendPollAnswer(int pId, AnswerWrapper pWrapper, Callback<DefaultMessageWrapper> pCallback) {
        sApiService.sendPollAnswer(pId, pWrapper, pCallback);
    }

    public static void getNewsList(String pType, int pPage, int pLimit, Callback<ShortNewsWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_TYPE, pType);
        query.put(PARAM_PAGE, String.valueOf(pPage));
        query.put(PARAM_LIMIT, String.valueOf(pLimit));
        sApiService.getNewsList(query, pCallback);
    }

    public static void checkReadinessPassport(ReadinessPassport pPassport, Callback<ReadinessPassportWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_TYPE, pPassport.getType());
        query.put(PARAM_REGION, pPassport.getRegion());
        query.put(PARAM_SERIES, pPassport.getSeries());
        query.put(PARAM_NUMBER, pPassport.getNumber());
        query.put(PARAM_BIRTH_DATE, pPassport.getBirthDate());
        sApiService.checkReadinessPassport(query, pCallback);
    }

    public static void getReadinessPassportRegions(Callback<ReadinessPassportRegionWrapper> pCallback) {
        sApiService.getReadinessPassportRegions(pCallback);
    }

    public static void getNewsById(int pNewsId, Callback<DetailedNewsWrapper> pCallback) {
        sApiService.getNewsById(pNewsId, pCallback);
    }

    public static void getDepartmentsUpdate(long pTimestamp, Callback<DepartmentWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_TIMESTAMP, String.valueOf(pTimestamp));
        sApiService.getDepartments(query, pCallback);
    }

    public static void geDepartmentById(int pDepartmentId, Callback<DetailedDepartmentWrapper> pCallback) {
        sApiService.getDepartmentById(pDepartmentId, pCallback);
    }

    public static void getEmployeeById(int pEmployeeId, Callback<EmployeeWrapper> pCallback) {
        sApiService.getEmployeeById(pEmployeeId, pCallback);
    }

    public static void getSitesForMakePassportAdult(Callback<SitesWrapper> pCallback) {
        sApiService.getSitesForMakePassportAdult(pCallback);
    }

    public static void sendPassportAdultProfile(MakePassObject object, Callback<DefaultMessageWrapper> pCallback) {
        sApiService.sendPassportAdultProfile(object, pCallback);
    }

    public static void getCategoryList(Callback<CategoryWrapper> pCallback) {
        sApiService.getCategoryList(pCallback);
    }

    public static void sendFeedback(Feedback pFeedback, Callback<DefaultMessageWrapper> pCallback) {
        sApiService.sendFeedback(pFeedback, pCallback);
    }

    public static void getDocumentList(String pKey, int pPage, int pLimit, Callback<DocumentListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_KEY, pKey);
        query.put(PARAM_PAGE, String.valueOf(pPage));
        query.put(PARAM_LIMIT, String.valueOf(pLimit));
        sApiService.getDocumentList(query, pCallback);
    }

    public static void getDocumentById(int pDocumentId, Callback<DocumentWrapper> pCallback) {
        sApiService.getDocumentById(pDocumentId, pCallback);
    }

    public static void getAppointmentRegions(Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_REGION);
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void getAppointmentDistricts(int pRegionId, Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_DISTRICT);
        query.put(PARAM_REGION, String.valueOf(pRegionId));
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void getAppointmentService(int pRegionId, int pDistrictId,
                                             Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_DOCUMENT);
        query.put(PARAM_DISTRICT, String.valueOf(pDistrictId));
        query.put(PARAM_REGION, String.valueOf(pRegionId));
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void getAppointmentOperation(int pRegionId, int pDistrictId, int pServiceId,
                                             Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_OPERATION);
        query.put(PARAM_DOCUMENT, String.valueOf(pServiceId));
        query.put(PARAM_DISTRICT, String.valueOf(pDistrictId));
        query.put(PARAM_REGION, String.valueOf(pRegionId));
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void getAppointmentDepartments(int pRegionId, int pDistrictId, int pServiceId,
                                                 int pOperationId,
                                               Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_DEPARTMENT);
        query.put(PARAM_OPERATION, String.valueOf(pOperationId));
        query.put(PARAM_DOCUMENT, String.valueOf(pServiceId));
        query.put(PARAM_DISTRICT, String.valueOf(pDistrictId));
        query.put(PARAM_REGION, String.valueOf(pRegionId));
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void getAppointmentTime(int pRegionId, int pDistrictId, int pServiceId,
                                          int pOperationId, int pDepartmentId, String pDateString,
                                          Callback<OptionsListWrapper> pCallback) {
        Map<String, String> query = new HashMap<>();
        query.put(PARAM_FIELD, VALUE_TIME);
        query.put(PARAM_DATE, String.valueOf(pDateString));
        query.put(PARAM_DEPARTMENT, String.valueOf(pDepartmentId));
        query.put(PARAM_OPERATION, String.valueOf(pOperationId));
        query.put(PARAM_DOCUMENT, String.valueOf(pServiceId));
        query.put(PARAM_DISTRICT, String.valueOf(pDistrictId));
        query.put(PARAM_REGION, String.valueOf(pRegionId));
        sApiService.getAppointmentOptions(query, pCallback);
    }

    public static void sendAppointment(Appointment pAppointment, Callback<DefaultMessageWrapper> pCallback){
        sApiService.sendAppointment(pAppointment, pCallback);
    }

    public static void getPaymentRegions(Callback<PaymentWrapper> pCallback){
        sApiService.getPaymentRegions(pCallback);
    }

    public static void getPaymentDistrics(int regionId, Callback<PaymentWrapper> pCallback){
        sApiService.getPaymentDistrics(regionId, pCallback);
    }

    public static void getPaymentLocations(int regionId, Callback<PaymentWrapper> pCallback){
        sApiService.getPaymentLocations(regionId, pCallback);
    }

    public static void getPaymentPurposes(Callback<PaymentWrapper> pCallback){
        sApiService.getPaymentPurposes(pCallback);
    }

    public static void sendPayment(Payment object, Callback<RegistrationReceiptWrapper> pCallback){
        sApiService.sendPayment(object, pCallback);
    }

    public static void sendPaymentToMail(Payment object, Callback<DefaultMessageWrapper> pCallback){
        sApiService.sendPaymentToMail(object, pCallback);
    }

    public static void downloadPayment(Payment object, Callback<PaymentDownloadWrapper> pCallback){
        sApiService.downloadPayment(object, pCallback);
    }
    */
}
