package br.com.pasteldahora.order.adapter.in.mvc;

import br.com.pasteldahora.inventory.domain.exception.InsufficientStockException;
import br.com.pasteldahora.inventory.domain.exception.StockItemNotFoundException;
import br.com.pasteldahora.order.application.port.in.AddOrderItemCommand;
import br.com.pasteldahora.order.application.port.in.CreateOrderCommand;
import br.com.pasteldahora.order.application.port.in.OrderUseCase;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/employee/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
public class OrderMvcController {

    private final OrderUseCase orderUseCase;

    public OrderMvcController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderUseCase.findAll());
        return "order/list";
    }

    @PostMapping
    public String create(Authentication authentication) {
        var order = orderUseCase.create(new CreateOrderCommand(
                null,
                authentication.getName()
        ));
        return "redirect:/employee/orders/" + order.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        populateDetails(id, model);
        model.addAttribute("itemForm", new AddOrderItemForm());
        return "order/details";
    }

    @PostMapping("/{id}/items")
    public String addItem(
            @PathVariable UUID id,
            @Valid @ModelAttribute("itemForm") AddOrderItemForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateDetails(id, model);
            return "order/details";
        }
        try {
            orderUseCase.addItem(new AddOrderItemCommand(
                    id,
                    form.getProductId(),
                    form.getQuantity(),
                    authentication.getName()
            ));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            bindingResult.reject("order.item.invalid", exception.getMessage());
            populateDetails(id, model);
            return "order/details";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Item adicionado ao pedido.");
        return "redirect:/employee/orders/" + id;
    }

    @PostMapping("/{id}/items/{itemId}/remove")
    public String removeItem(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderUseCase.removeItem(id, itemId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Item removido.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/orders/" + id;
    }

    @PostMapping("/{id}/complete")
    public String complete(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderUseCase.complete(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Pedido concluído.");
        } catch (InsufficientStockException
                 | StockItemNotFoundException
                 | IllegalArgumentException
                 | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderUseCase.cancel(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Pedido cancelado.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/orders/" + id;
    }

    private void populateDetails(UUID id, Model model) {
        model.addAttribute("order", orderUseCase.findById(id));
        model.addAttribute("products", orderUseCase.findProductsAvailableForSale());
    }
}
