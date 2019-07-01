package de.wackernagel.essbar.web;

import org.jsoup.nodes.Document;

import androidx.lifecycle.LiveData;

import de.wackernagel.essbar.EssbarConstants;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebService {

    @GET( "/content" )
    LiveData<Resource<Document>> getHome();

    @Headers("Referer: " + EssbarConstants.Urls.HOME )
    @FormUrlEncoded
    @POST( "/accounts/login/" )
    LiveData<Resource<Document>> postLoginData(@Field("csrfmiddlewaretoken") String csrfToken, @Field("login") String username, @Field("password") String password );

    @Headers("Referer: https://bestellung-pipapo-catering.mms-rcs.de/menu/0/2019-07-01/2019-07-07/" )
    @FormUrlEncoded
    @POST( "/menu/0/{start}/{end}/" )
    LiveData<Resource<Document>> postMenusStartDate(@Path("start") String start, @Path("end") String end, @Field("csrfmiddlewaretoken") String csrfToken, @Field("week_id") String calendarWeekWithYear );

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> postChangedMenus(@Body RequestBody formData );

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> postConfirmedMenus( @Body RequestBody formData );

}
