package aor.paj.bean;

import aor.paj.dto.Task;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.util.List;

@Singleton
@Startup
public class StartupBean {
    @Inject
    UserBean userBean;
    @Inject
    CategoryBean categoryBean;
    @Inject
    TaskBean taskBean;

    @PostConstruct
    public void init() {

        userBean.createDefaultUsersIfNotExistent();
        //categoryBean.createDefaultCategoryIfNotExistent();


    }


}