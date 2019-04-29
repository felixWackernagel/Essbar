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

    @FormUrlEncoded
    @POST( "/index.php?ear_a=akt_login" )
    LiveData<Resource<Document>> requestLoginDocument( @Field("Login_Name") String username, @Field("Login_Passwort") String password );

    @GET( "/index.php?m=2;0" )
    LiveData<Resource<Document>> requestMenusDocument();

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> requestMenuConfirmationDocument( @Body RequestBody formData );

    @FormUrlEncoded
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> requestMenuDocumentByDate( @Field("sel_datum") String selectedDate );
}
