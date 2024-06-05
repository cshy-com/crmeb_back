package com.cshy.service.impl.yqt;

import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.yqt.YqtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YqtServiceImpl implements YqtService {
    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreProductService storeProductService;

//    public String getToken(){
//
//    }
}
