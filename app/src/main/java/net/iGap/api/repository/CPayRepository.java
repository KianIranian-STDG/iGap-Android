package net.iGap.api.repository;

import net.iGap.api.CPayApi;
import net.iGap.api.apiService.RetrofitFactory;

public class CPayRepository {

    private static CPayRepository instance ;
    private CPayApi api ;

    private CPayRepository() {
        api = new RetrofitFactory().getCPayApi();
    }

    public static CPayRepository getInstance(){
        if(instance == null){
            instance = new CPayRepository();
        }
        return instance;
    }

    public void registerNewPlaque(){

    }
}
