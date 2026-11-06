package com.example.demo.controller;

import com.example.demo.model.Brinquedo;
import com.example.demo.service.BrinquedoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Interface Web (Thymeleaf): landing page pública, tela de login e painel de brinquedos.
 */
@Controller
public class WebController {

    private final BrinquedoService service;

    public WebController(BrinquedoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String landing() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/painel")
    public String painel(Model model, Authentication auth) {
        carregarPainel(model, auth);
        model.addAttribute("brinquedo", new Brinquedo());
        return "painel";
    }

    @GetMapping("/painel/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, Authentication auth) {
        carregarPainel(model, auth);
        model.addAttribute("brinquedo", service.buscarPorId(id));
        return "painel";
    }

    @PostMapping("/painel/salvar")
    public String salvar(@Valid @ModelAttribute("brinquedo") Brinquedo brinquedo,
                         BindingResult resultado,
                         Model model,
                         Authentication auth,
                         RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            carregarPainel(model, auth);
            return "painel";
        }
        if (brinquedo.getId() == null) {
            service.criar(brinquedo);
            redirect.addFlashAttribute("mensagem", "Brinquedo cadastrado com sucesso!");
        } else {
            service.atualizar(brinquedo.getId(), brinquedo);
            redirect.addFlashAttribute("mensagem", "Brinquedo atualizado com sucesso!");
        }
        return "redirect:/painel";
    }

    @PostMapping("/painel/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("mensagem", "Brinquedo excluído com sucesso!");
        return "redirect:/painel";
    }

    private void carregarPainel(Model model, Authentication auth) {
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("brinquedos", service.listar(null, null));
        model.addAttribute("usuario", auth.getName());
        model.addAttribute("admin", admin);
    }
}
