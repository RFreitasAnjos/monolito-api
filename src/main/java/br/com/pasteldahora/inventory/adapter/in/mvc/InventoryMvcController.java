package br.com.pasteldahora.inventory.adapter.in.mvc;

import br.com.pasteldahora.inventory.application.port.in.CreateStockItemCommand;
import br.com.pasteldahora.inventory.application.port.in.InventoryUseCase;
import br.com.pasteldahora.inventory.application.port.in.RegisterStockMovementCommand;
import br.com.pasteldahora.inventory.application.port.in.UpdateMinimumStockCommand;
import br.com.pasteldahora.inventory.domain.exception.DuplicateStockItemException;
import br.com.pasteldahora.inventory.domain.exception.InsufficientStockException;
import br.com.pasteldahora.inventory.domain.exception.ProductNotEligibleForStockException;
import br.com.pasteldahora.inventory.domain.model.StockMovementType;
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
import java.util.Arrays;

@Controller
@RequestMapping("/employee/inventory")
public class InventoryMvcController {

    private final InventoryUseCase inventoryUseCase;

    public InventoryMvcController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String list(Authentication authentication, Model model) {
        model.addAttribute("items", inventoryUseCase.findAll());
        model.addAttribute("canManage", canManage(authentication));
        return "inventory/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String newStockItem(Model model) {
        model.addAttribute("form", new CreateStockItemForm());
        addAvailableProducts(model);
        return "inventory/create";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String create(
            @Valid @ModelAttribute("form") CreateStockItemForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addAvailableProducts(model);
            return "inventory/create";
        }
        try {
            inventoryUseCase.createStockItem(new CreateStockItemCommand(
                    form.itemId(),
                    form.itemType(),
                    form.getMinimumQuantity(),
                    authentication.getName()
            ));
        } catch (DuplicateStockItemException | ProductNotEligibleForStockException exception) {
            bindingResult.reject("inventory.invalid", exception.getMessage());
            addAvailableProducts(model);
            return "inventory/create";
        }
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Item de estoque cadastrado com sucesso."
        );
        return "redirect:/employee/inventory";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String details(
            @PathVariable UUID id,
            Authentication authentication,
            Model model
    ) {
        var item = inventoryUseCase.findById(id);
        MinimumStockForm minimumForm = new MinimumStockForm();
        minimumForm.setMinimumQuantity(item.minimumQuantity());
        model.addAttribute("item", item);
        model.addAttribute("movements", inventoryUseCase.findMovements(id));
        model.addAttribute("movementForm", new StockMovementForm());
        model.addAttribute("minimumForm", minimumForm);
        model.addAttribute("movementTypes", manualMovementTypes());
        model.addAttribute("canManage", canManage(authentication));
        return "inventory/details";
    }

    @PostMapping("/{id}/minimum")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateMinimum(
            @PathVariable UUID id,
            @Valid @ModelAttribute("minimumForm") MinimumStockForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateDetails(id, model);
            model.addAttribute("minimumForm", form);
            return "inventory/details";
        }
        inventoryUseCase.updateMinimumStock(new UpdateMinimumStockCommand(
                id,
                form.getMinimumQuantity(),
                authentication.getName()
        ));
        redirectAttributes.addFlashAttribute("successMessage", "Estoque mínimo atualizado.");
        return "redirect:/employee/inventory/" + id;
    }

    @PostMapping("/{id}/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String registerMovement(
            @PathVariable UUID id,
            @Valid @ModelAttribute("movementForm") StockMovementForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateDetails(id, model);
            model.addAttribute("movementForm", form);
            return "inventory/details";
        }
        try {
            inventoryUseCase.registerMovement(new RegisterStockMovementCommand(
                    id,
                    form.getType(),
                    form.getQuantity(),
                    form.getUnitPrice(),
                    form.getReason(),
                    authentication.getName()
            ));
        } catch (InsufficientStockException
                 | ProductNotEligibleForStockException
                 | IllegalArgumentException exception) {
            populateDetails(id, model);
            model.addAttribute("movementForm", form);
            model.addAttribute("movementError", exception.getMessage());
            return "inventory/details";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Movimentação registrada.");
        return "redirect:/employee/inventory/" + id;
    }

    private void addAvailableProducts(Model model) {
        model.addAttribute(
                "itemsAvailable",
                inventoryUseCase.findItemsAvailableForStock()
        );
    }

    private void populateDetails(UUID id, Model model) {
        var item = inventoryUseCase.findById(id);
        MinimumStockForm minimumForm = new MinimumStockForm();
        minimumForm.setMinimumQuantity(item.minimumQuantity());
        model.addAttribute("item", item);
        model.addAttribute("movements", inventoryUseCase.findMovements(id));
        model.addAttribute("movementForm", new StockMovementForm());
        model.addAttribute("minimumForm", minimumForm);
        model.addAttribute("movementTypes", manualMovementTypes());
        model.addAttribute("canManage", true);
    }

    private static java.util.List<StockMovementType> manualMovementTypes() {
        return Arrays.stream(StockMovementType.values())
                .filter(type -> type != StockMovementType.SALE_EXIT)
                .toList();
    }

    private static boolean canManage(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority ->
                "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_MANAGER".equals(authority.getAuthority())
        );
    }
}
