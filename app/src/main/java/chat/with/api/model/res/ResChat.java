package chat.with.api.model.res;

import java.util.List;

public class ResChat {
    private int kode;
    private String status;
    private List<ResDetailChat> dataChat;

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResDetailChat> getDataChat() {
        return dataChat;
    }

    public void setDataChat(List<ResDetailChat> dataChat) {
        this.dataChat = dataChat;
    }
}
