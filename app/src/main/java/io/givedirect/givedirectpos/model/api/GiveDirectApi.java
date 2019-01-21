package io.givedirect.givedirectpos.model.api;

import io.givedirect.givedirectpos.model.persistence.givedirect.AuthFile;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GiveDirectApi {
    @GET("auth/{authFile}")
    Single<AuthFile> getAuthFile(@Path("authFile") String authFile);
}
