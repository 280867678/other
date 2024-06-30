package com.carrydream.cardrecorder.base;

import com.carrydream.cardrecorder.Model.Captcha;
import com.carrydream.cardrecorder.Model.Config;
import com.carrydream.cardrecorder.Model.Info;
import com.carrydream.cardrecorder.Model.Order;
import com.carrydream.cardrecorder.Model.Picture;
import com.carrydream.cardrecorder.Model.Pictures;
import com.carrydream.cardrecorder.Model.Plans;
import com.carrydream.cardrecorder.Model.Platform;
import com.carrydream.cardrecorder.Model.Sms;
import com.carrydream.cardrecorder.Model.Update;
import com.carrydream.cardrecorder.Model.User;
import io.reactivex.Observable;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/* loaded from: classes.dex */
public interface Api {
    @FormUrlEncoded
    @POST("/api/jpq/auth/captcha?")
    Observable<BaseResult<Captcha>> captcha(@Field("mobile") String str);

    @FormUrlEncoded
    @POST("/api/jpq/auth/captcha-check?")
    Observable<BaseResult<Sms>> check(@Field("key") String str, @Field("captcha") String str2);

    @POST("/api/jpq/auth/conf?")
    Observable<BaseResult<Config>> conf();

    @FormUrlEncoded
    @POST("/api/jpq/user/feedback?")
    Observable<BaseResult<Object>> feedback(@Field("content") String str, @Field("mobile") String str2, @Field("images[]") String[] strArr);

    @POST("/api/jpq/upload/image?")
    @Multipart
    Observable<BaseResult<Picture>> image(@Part MultipartBody.Part part);

    @POST("/api/jpq/upload/images?")
    @Multipart
    Observable<BaseResult<Pictures>> images(@Part List<MultipartBody.Part> list);

    @POST("/api/jpq/user/info?")
    Observable<BaseResult<User>> info();

    @FormUrlEncoded
    @POST("/api/jpq/auth/login?")
    Observable<BaseResult<Info>> login(@Field("mobile") String str, @Field("code") String str2);

    @POST("/api/jpq/user/logout?")
    Observable<BaseResult<Object>> logout();

    @FormUrlEncoded
    @POST("/api/jpq/pay/order?")
    Observable<BaseResult<Order>> order(@Field("plan_id") String str);

    @POST("/api/jpq/pay/plans?")
    Observable<BaseResult<Plans>> plans();

    @FormUrlEncoded
    @POST("/api/jpq/platform/report?")
    Observable<BaseResult<Object>> report(@Field("platform_id") int i, @Field("platform") String str);

    @FormUrlEncoded
    @POST("/api/jpq/auth/sms-code?")
    Observable<BaseResult<Sms>> sms_code(@Field("mobile") String str);

    @POST("/api/jpq/platform/support?")
    Observable<BaseResult<List<Platform>>> support();

    @FormUrlEncoded
    @POST("/api/jpq/upgrade?")
    Observable<BaseResult<Update>> upgrade(@Field("version_code") String str, @Field("package_name") String str2, @Field("package_tag") String str3);
}
