package com.example.java17_06_11.controllers;

import com.azure.core.annotation.Get;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import com.example.java17_06_11.CookieFunctions;
import com.example.java17_06_11.models.*;
import com.example.java17_06_11.repositories.ApartmentRepository;
import com.example.java17_06_11.repositories.ImageRepository;
import com.example.java17_06_11.repositories.RentRepository;
import com.example.java17_06_11.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;

@Controller
public class ApartmentController {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentRepository rentRepository;
//
//    public ApartmentController(ApartmentRepository apartmentRepository) {
//        this.apartmentRepository = apartmentRepository;
//    }
private BlobServiceClient client;
    private BlobContainerClient container;
    public ApartmentController(){
        BlobServiceClientBuilder builder = new BlobServiceClientBuilder();
        client = builder.connectionString("UseDevelopmentStorage=true").buildClient();
        //ВАЖНО!!!!
        //azurite --silent --location c:\azurite --debug c:\azurite\debug.log --skipApiVersionCheck
        //Запускать Azurite ИМЕННО с этой команды, иначе выдаст ошибку
        //ВАЖНО!!!!
        BlobSignedIdentifier identifier = new BlobSignedIdentifier().setId("test policy")
                .setAccessPolicy(new BlobAccessPolicy().setStartsOn(OffsetDateTime.now())
                        .setExpiresOn(OffsetDateTime.now().plusDays(7))
                        .setPermissions("cd"));

        ArrayList<BlobSignedIdentifier> identifiers = new ArrayList<BlobSignedIdentifier>();
        identifiers.add(identifier);
        container = client.createBlobContainerIfNotExists("apartmentimages");
        container.setAccessPolicy(PublicAccessType.CONTAINER,identifiers);
    }

    @GetMapping(value = "/")
    public String Index(HttpServletRequest request,Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        var apartmentList = apartmentRepository.findAll();
        ArrayList<Apartment> ls = new ArrayList<>();
        apartmentList.forEach(t->ls.add(t));
        Counter count = new Counter();

        model.addAttribute("list",apartmentList);
        model.addAttribute("counter",count);
        return "Apartment/index";
    }
    @GetMapping("/apartments/create")
    public String Create(HttpServletRequest request,Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null)
        {
            return "redirect:/";
        }
       return "Apartment/create";
    }
    @PostMapping("/apartments/create")
    public String Create(HttpServletRequest request,@RequestParam String address, @RequestParam int rooms, @RequestParam double price, @RequestParam int metters, @RequestParam ArrayList<MultipartFile> images, Model model){
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null)
        {
            return "redirect:/";
        }
        Apartment ap = new Apartment();
        ap.setAddress(address);
        ap.setRooms(rooms);
        ap.setPrice(price);
        ap.setMetters(metters);
        ap.setUser(user);
        ArrayList<Image> ls = new ArrayList<>();
        apartmentRepository.save(ap);
        try {
            for(var img: images)
            {
                try(var stream = img.getInputStream())
                {
                    BlobClient cl = container.getBlobClient(img.getOriginalFilename());
                    cl.upload(stream,stream.available(),true);
                    Image im = new Image();
                    im.setImage(cl.getBlobUrl());
                    im.setApartment(ap);
                    imageRepository.save(im);
                    ls.add(im);
                }
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return "redirect:/";
    }
    @GetMapping(value = "/rents")
    private String getRents(HttpServletRequest request,Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        var rents = user.getRents();
        model.addAttribute("list",rents);
        Counter count = new Counter();
        model.addAttribute("counter",count);
        return "Apartment/rents";
    }
    @PostMapping(value = "/rent")
    private String rentHouse(@RequestParam int nights,@RequestParam long apartment, HttpServletRequest request,Model model)
    {
        var user = CookieFunctions.getAuthorisedUser(request,userRepository);
        model.addAttribute("user",user);
        model.addAttribute("isAdmin", User.isAdmin(user));
        if(user==null)
        {
            return "redirect:/";
        }
        Rent rent = new Rent();
        rent.setNights(nights);
        var apart = apartmentRepository.findById(apartment).orElse(null);
        rent.setApartment(apart);
        rent.setUser(user);
        var pr = apart.getPrice()*nights;
        rent.setPrice(pr);
        rentRepository.save(rent);
        return "redirect:/";
    }
}
