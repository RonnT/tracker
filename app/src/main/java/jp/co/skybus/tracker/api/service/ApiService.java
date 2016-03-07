package jp.co.skybus.tracker.api.service;

import java.util.List;

import jp.co.skybus.tracker.model.DefaultResponseWrapper;
import jp.co.skybus.tracker.model.Info;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Roman Titov on 18.01.2016.
 */
public interface ApiService {

    @POST("/")
    void sendData(@Body List<Info> objectList, Callback<DefaultResponseWrapper> pCallback);

/*
    @GET("/location/region")
    void getRegion(@QueryMap Map<String, String> pParams, Callback<RegionIdWrapper> pCallback);

    @GET("/appeal/dictionary")
    void getAppealDictionary(Callback<AppealDictionary> pCallback);

    @GET("/consultation/dictionary")
    void getConsultationDictionary(Callback<AppealDictionary> pCallback);

    @POST("/appeal")
    void sendAppeal(@Body MultipartTypedOutput attachments, Callback<DefaultMessageWrapper> pCallback);

    @POST("/consultation")
    void sendConsultation(@Body Appeal pAppeal, Callback<DefaultMessageWrapper> pCallback);

    @GET("/polls/list")
    void getPolls(@QueryMap Map<String, String> pParams, Callback<PollsWrapper> pCallback);

    @GET("/polls/{id}")
    void getPollById(@Path("id") int pId, Callback<Poll> pCallback);

    @POST("/polls/{id}")
    void sendPollAnswer(@Path("id") int pId, @Body AnswerWrapper pWrapper, Callback<DefaultMessageWrapper> pCallback);

    @GET("/news/list")
    void getNewsList(@QueryMap Map<String, String> pParams, Callback<ShortNewsWrapper> pCallback);

    @GET("/services/passport")
    void checkReadinessPassport(@QueryMap Map<String, String> pParams, Callback<ReadinessPassportWrapper> pCallback);

    @GET("/services/passport/dictionary?type=regions")
    void getReadinessPassportRegions(Callback<ReadinessPassportRegionWrapper> pCallback);

    @GET("/news/{id}")
    void getNewsById(@Path("id") int pId, Callback<DetailedNewsWrapper> pCallback);

    @GET("/structure/list")
    void getDepartments(@QueryMap Map<String, String> pParams, Callback<DepartmentWrapper> pCallback);

    @GET("/structure/{id}")
    void getDepartmentById(@Path("id") int pId, Callback<DetailedDepartmentWrapper> pCallback);

    @GET("/structure/employee/{id}")
    void getEmployeeById(@Path("id") int pId, Callback<EmployeeWrapper> pCallback);

    @GET("/documents/category")
    void getCategoryList(Callback<CategoryWrapper> pCallback);

    @GET("/documents/list")
    void getDocumentList(@QueryMap Map<String, String> pParams, Callback<DocumentListWrapper> pCallback);

    @GET("/documents/{id}")
    void getDocumentById(@Path("id") int pId, Callback<DocumentWrapper> pCallback);

    @POST("/feedback")
    void sendFeedback(@Body Feedback pFeedback, Callback<DefaultMessageWrapper> pCallback);

    @GET("/services/passport/dictionary?type=sites")
    void getSitesForMakePassportAdult(Callback<SitesWrapper> pCallback);

    @POST("/services/passport")
    void sendPassportAdultProfile(@Body MakePassObject object, Callback<DefaultMessageWrapper> pCallback);

    @GET("/services/appointment")
    void getAppointmentOptions(@QueryMap Map<String, String> pParams, Callback<OptionsListWrapper> pCallback);

    @POST("/services/appointment")
    void sendAppointment(@Body Appointment object, Callback<DefaultMessageWrapper> pCallback);

    @GET("/services/payment/dictionary?type=regions")
    void getPaymentRegions(Callback<PaymentWrapper> pCallback);

    @GET("/services/payment/dictionary?type=districts")
    void getPaymentDistrics(@Query("region_id") int regionId, Callback<PaymentWrapper> pCallback);

    @GET("/services/payment/dictionary?type=locations")
    void getPaymentLocations(@Query("region_id") int regionId, Callback<PaymentWrapper> pCallback);

    @GET("/services/payment/dictionary?type=purposes")
    void getPaymentPurposes(Callback<PaymentWrapper> pCallback);

    @POST("/services/payment")
    void sendPayment(@Body Payment object, Callback<RegistrationReceiptWrapper> pCallback);

    @POST("/services/payment/sendmail")
    void sendPaymentToMail(@Body Payment object, Callback<DefaultMessageWrapper> pCallback);

    @POST("/services/payment/download")
    void downloadPayment(@Body Payment object, Callback<PaymentDownloadWrapper> pCallback);
    */
}