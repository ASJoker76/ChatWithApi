package chat.with.api.model.req;

public class ReqKirimChat {
    private String chat;
    private String usr_pengirim;
    private String waktu_chat;
    private String usr_penerima;

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getUsr_pengirim() {
        return usr_pengirim;
    }

    public void setUsr_pengirim(String usr_pengirim) {
        this.usr_pengirim = usr_pengirim;
    }

    public String getWaktu_chat() {
        return waktu_chat;
    }

    public void setWaktu_chat(String waktu_chat) {
        this.waktu_chat = waktu_chat;
    }

    public String getUsr_penerima() {
        return usr_penerima;
    }

    public void setUsr_penerima(String usr_penerima) {
        this.usr_penerima = usr_penerima;
    }
}
