package de.wackernagel.essbar.web;

import org.jsoup.nodes.Document;

import androidx.lifecycle.LiveData;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WebService {

    @FormUrlEncoded
    @POST( "/index.php?ear_a=akt_login" )
    LiveData<Resource<Document>> requestLoginDocument( @Field("Login_Name") String username, @Field("Login_Passwort") String password );

    @GET( "/index.php?m=2;0" )
    LiveData<Resource<Document>> requestMenusDocument();

    @FormUrlEncoded
    @POST( "/index.php?m=2;0" )
    LiveData<Resource<Document>> requestMenuDocumentByDate( @Field("sel_datum") String selectedDate );
}
