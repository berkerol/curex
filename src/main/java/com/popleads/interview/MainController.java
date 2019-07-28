package com.popleads.interview;

import com.google.gson.JsonParser;
import com.popleads.interview.entities.History;
import com.popleads.interview.entities.User;
import com.popleads.interview.repositories.HistoryRepository;
import com.popleads.interview.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("/signup")
    public String signup() {
        return "/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password) {
        userRepository.save(new User(username, new BCryptPasswordEncoder().encode(password), 20000.0, 0.0, 0.0));
        return "redirect:/signin";
    }

    @GetMapping("/signin")
    public String signin() {
        return "/signin";
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("try", user.getTl());
        model.addAttribute("usd", user.getUsd());
        model.addAttribute("eur", user.getEur());
        model.addAttribute("history", user.getHistories());
        return "/home";
    }

    @PostMapping("/exchange")
    public String exchange(@RequestParam int buyAmount, @RequestParam String buyCurrency,
                           @RequestParam String sellCurrency, Model model, Principal principal) {
        if (buyCurrency.equals(sellCurrency)) {
            return "redirect:/";
        }
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("https://api.exchangeratesapi.io/latest?base=" + buyCurrency + "&symbols=" + sellCurrency, String.class);
        if (response == null) {
            return "redirect:/";
        }
        double rate = Double.parseDouble(new JsonParser().parse(response).getAsJsonObject().get("rates").getAsJsonObject().get(sellCurrency).getAsString());
        double sellAmount = rate * buyAmount;
        User user = userRepository.findByUsername(principal.getName());
        double buyMoney;
        if (buyCurrency.equals("TRY")) {
            buyMoney = user.getTl();
        } else if (buyCurrency.equals("USD")) {
            buyMoney = user.getUsd();
        } else {
            buyMoney = user.getEur();
        }
        double sellMoney;
        if (sellCurrency.equals("TRY")) {
            sellMoney = user.getTl();
        } else if (sellCurrency.equals("USD")) {
            sellMoney = user.getUsd();
        } else {
            sellMoney = user.getEur();
        }
        if (sellAmount > sellMoney) {
            return "redirect:/";
        }
        buyMoney += buyAmount;
        if (buyCurrency.equals("TRY")) {
            user.setTl(buyMoney);
        } else if (buyCurrency.equals("USD")) {
            user.setUsd(buyMoney);
        } else {
            user.setEur(buyMoney);
        }
        sellMoney -= sellAmount;
        if (sellCurrency.equals("TRY")) {
            user.setTl(sellMoney);
        } else if (sellCurrency.equals("USD")) {
            user.setUsd(sellMoney);
        } else {
            user.setEur(sellMoney);
        }
        userRepository.save(user);
        historyRepository.save(new History(user, "Bought " + buyAmount + " " + buyCurrency + " sold " + sellAmount + " " + sellCurrency));
        return "redirect:/";
    }
}
