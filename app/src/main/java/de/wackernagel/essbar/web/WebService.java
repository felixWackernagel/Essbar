package de.wackernagel.essbar.web;

import org.jsoup.nodes.Document;

import androidx.lifecycle.LiveData;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface WebService {

    @GET( "/content" )
    LiveData<Resource<Document>> getHome();

    @Headers("Referer: https://bestellung-pipapo-catering.mms-rcs.de/content/")
    @FormUrlEncoded
    @POST( "/accounts/login/" )
    LiveData<Resource<Document>> postLoginData(@Field("csrfmiddlewaretoken") String csrfToken, @Field("login") String username, @Field("password") String password );

    @GET( "/index.php?m=2;0" )
    LiveData<Resource<Document>> getMenus();

    @FormUrlEncoded
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> postMenusStartDate(@Field("sel_datum") String selectedDate );

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> postChangedMenus(@Body RequestBody formData );

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> postConfirmedMenus( @Body RequestBody formData );

}
