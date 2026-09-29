package br.com.pasteldahora.catalog.adapter.in.mvc;

import br.com.pasteldahora.catalog.domain.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryForm {

    @NotBlank
    @Size(max = 80)
    private String name;

    public static CategoryForm from(Category category) {
        CategoryForm form = new CategoryForm();
        form.setName(category.getName());
        return form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
