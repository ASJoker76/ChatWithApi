package chat.with.api.connection;



import chat.with.api.model.req.ReqKirimChat;
import chat.with.api.model.req.ReqRegister;
import chat.with.api.model.res.ResChat;
import chat.with.api.model.res.ResUtama;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Service {
    @Headers({
            "Content-Type:application/json"
    })

    @FormUrlEncoded
    @POST("bikin_akun.php")
    Call<ResUtama> registerRequest(@Field("username_") String username_,
                                   @Field("password_") String password_,
                                   @Field("nama_lengkap_") String nama_lengkap_);
    @FormUrlEncoded
    @POST("post_chat.php")
    Call<ResUtama> kirimPesanRequest(@Field("chat") String chat,
                                     @Field("usr_pengirim") String usr_pengirim,
                                     @Field("waktu_chat") String nama_lengkap,
                                     @Field("usr_penerima") String usr_penerima);

    @GET("get_chat.php")
    Call<ResChat> chatRequest();
}
