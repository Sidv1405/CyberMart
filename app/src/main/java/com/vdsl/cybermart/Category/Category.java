package com.vdsl.cybermart.Category;

import java.util.List;

public class Category {
    private List<Category_Element> list;

    public Category(List<Category_Element> list) {
        this.list = list;
    }

    public List<Category_Element> getList() {
        return list;
    }

    public void setList(List<Category_Element> list) {
        this.list = list;
    }

}
