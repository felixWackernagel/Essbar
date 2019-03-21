package de.wackernagel.essbar.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JSoupConverter implements Converter<ResponseBody, Document> {

    public static final Converter.Factory FACTORY = new Converter.Factory() {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter( Type type, Annotation[] annotations, Retrofit retrofit ) {
            if (type == Document.class) {
                return new JSoupConverter();
            }
            return null;
        }
    };

    @Override
    public Document convert( final ResponseBody responseBody ) throws IOException {
        return Jsoup.parse(responseBody.string());
    }
}