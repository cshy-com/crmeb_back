package com.cshy.admin.task.category;

import com.cshy.service.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CategoryTask {
    @Autowired
    private CategoryService categoryService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        //启动后加载分类到缓存
        categoryService.load2Cache();
    }
}
