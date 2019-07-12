package de.wackernagel.essbar.web;

import androidx.lifecycle.LiveData;

import org.jsoup.nodes.Document;

import java.util.Map;

import de.wackernagel.essbar.EssbarConstants;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebService {

    @GET( "/content/" )
    LiveData<Resource<Document>> getHomePage();

    @Headers("Referer: " + EssbarConstants.Urls.HOME )
    @FormUrlEncoded
    @POST( "/accounts/login/" )
    LiveData<Resource<Document>> postLoginData( @FieldMap Map<String, String> fields );

    @GET( "/menu/0/{start}/{end}/" )
    LiveData<Resource<Document>> getCalendarWeekMenusPage(@Path("start") String start, @Path("end") String end );

    @FormUrlEncoded
    @POST( "/menu/0/{start}/{end}/" )
    LiveData<Resource<Document>> postChangedMenusData(@Header( "Referer" ) String referer, @Path("start") String start, @Path("end") String end, @FieldMap Map<String, String> fields );

    @Headers("Referer: " + EssbarConstants.Urls.ORDER_CONFIRMATION )
    @FormUrlEncoded
    @POST( "/orders/confirmation/" )
    LiveData<Resource<Document>> postChangedAndConfirmedMenusData( @FieldMap Map<String, String> fields );

}
