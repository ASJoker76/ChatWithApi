package chat.with.api.model.res;

import java.util.List;

public class ResUser {

    private List<ResUserDetail> dataUser;

    public List<ResUserDetail> getDataUser() {
        return dataUser;
    }

    public void setDataUser(List<ResUserDetail> dataUser) {
        this.dataUser = dataUser;
    }
}
