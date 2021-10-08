package chat.with.api.connection;



import chat.with.api.model.req.ReqKirimChat;
import chat.with.api.model.req.ReqRegister;
import chat.with.api.model.res.ResChat;
import chat.with.api.model.res.ResLogin;
import chat.with.api.model.res.ResRoom;
import chat.with.api.model.res.ResUser;
import chat.with.api.model.res.ResUtama;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Service {
//    @Headers({
//            "Content-Type:application/json"
//    })

    @FormUrlEncoded
    @POST("post_regis.php")
    Call<ResUtama> registerRequest(@Field("username") String username,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResLogin> loginRequest(@Field("username") String username,
                                @Field("password") String password
    );

    @FormUrlEncoded
    @POST("post_chat.php")
    Call<ResUtama> kirimPesanRequest(
            @Field("id_room") int id_room,
            @Field("chat") String chat,
            @Field("usr_pengirim") String usr_pengirim,
            @Field("waktu_chat") String nama_lengkap,
            @Field("usr_penerima") String usr_penerima);

//    @FormUrlEncoded
//    @POST("get_list_kontak.php")
//    Call<ResChat> chatByFilterRequest(@Field("usrpenerima") String usrpenerima,
//                                      @Field("usrpengirim") String usrpengirim);

    @FormUrlEncoded
    @POST("filter_nama_get_chat.php")
    Call<ResChat> ambilpesanbynama(
            @Field("id_room") int id_room,
            @Field("usr_pengirim") String usr_pengirim,
            @Field("usr_penerima") String usr_penerima);

    @FormUrlEncoded
    @POST("cek_room.php")
    Call<ResRoom> cekroom(@Field("usr_pengirim") String usr_pengirim,
                          @Field("usr_penerima") String usr_penerima);

    @GET("get_chat.php")
    Call<ResChat> chatRequest();

    @GET("get_list_kontak.php")
    Call<ResUser> userRequest();
}
